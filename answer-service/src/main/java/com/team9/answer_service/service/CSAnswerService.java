package com.team9.answer_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.team9.answer_service.global.domain.Category;
import com.team9.answer_service.remote.csquestion.dto.CSQuestionDto;
import com.team9.answer_service.remote.csquestion.RemoteCSQuestionService;
import com.team9.answer_service.remote.user.RemoteUserService;
import com.team9.answer_service.remote.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.team9.answer_service.domain.CSAnswer;
import com.team9.answer_service.domain.dto.CSAnswerRequest;
import com.team9.answer_service.domain.dto.CSAnswerResponse;
import com.team9.answer_service.domain.repository.CSAnswerRepository;


@Service
@RequiredArgsConstructor
public class CSAnswerService {

    private final CSAnswerRepository csAnswerRepository;
    private final RemoteCSQuestionService remoteCSQuestionService;
    private final RemoteUserService remoteUserService;


    // 답변 작성
    public CSAnswerResponse.CSAnswerDetailResponse createAnswer(CSAnswerRequest.CSAnswerCreateRequest request,
                                                                UserDetails userDetails) {
        Long userId = getUserIdFromDetails(userDetails);
        UserDto.CSAnswerUserDto user = remoteUserService.getUserById(userId);

        CSQuestionDto.Response question = remoteCSQuestionService.getQuestionById(request.getCsquestion_id());
        if (question == null) {
            throw new IllegalArgumentException("질문이 존재하지 않습니다.");
        }

        CSAnswer answer = new CSAnswer();
        answer.setContent(request.getCsanswer_content());
        answer.setCreatedAt(LocalDateTime.now());
        answer.setFeedback("아직 피드백 없음");
        answer.setScore(0L);
        answer.setCsQuestionId(request.getCsquestion_id());
        answer.setUserId(userId);

        csAnswerRepository.save(answer);
        return buildAnswerDetailResponse(answer, user.getNickname(), question);
    }

    // 내 답변 리스트 조회
    public Page<CSAnswerResponse.CSAnswerListResponse> getMyAnswerList(UserDetails userDetails, Pageable pageable, Long questionId) {
        Long userId = getUserIdFromDetails(userDetails);

        Page<CSAnswer> answers;
        if (questionId == null) {       // questionId가 없으면 모든 question에 대해 내 답변 조회
            answers = csAnswerRepository.findAllByUserId(userId, pageable);
        } else {                        // questionId가 들어오면 특정 질문에 대한 내 답변 리스트 조회
            CSQuestionDto.Response question = remoteCSQuestionService.getQuestionById(questionId);
            if (question == null) {
                throw new IllegalArgumentException("질문이 존재하지 않습니다.");
            }
            answers = csAnswerRepository.findAllByUserIdAndCsQuestionId(userId, questionId, pageable);
        }

        return answers.map(answer -> {
            CSQuestionDto.Response question = remoteCSQuestionService.getQuestionById(answer.getCsQuestionId());
            UserDto.CSAnswerUserDto user = remoteUserService.getUserById(userId);

            return buildAnswerListResponse(answer, user.getNickname(), question, true);
        });
    }

    // 특정 질문의 전체 답변 리스트 조회
    public Page<CSAnswerResponse.CSAnswerListResponse> getAnswerList(UserDetails userDetails, Pageable pageable, Long questionId) {
        Long userId = getUserIdFromDetails(userDetails);

        Page<CSAnswer> answers;
        answers = csAnswerRepository.findAllByCsQuestionId(questionId, pageable);

        CSQuestionDto.Response question = remoteCSQuestionService.getQuestionById(questionId);

        // 이미 푼 문제인지 검사
        boolean canAccess = csAnswerRepository.existsByUserIdAndCsQuestionId(userId, questionId);

        return answers.map(answer -> {
            UserDto.CSAnswerUserDto user = remoteUserService.getUserById(answer.getUserId());
            return buildAnswerListResponse(answer, user.getNickname(), question, canAccess);
        });
    }

    // 특정 답변 조회
    public CSAnswerResponse.CSAnswerDetailResponse getAnswerDetail(Long answerId, UserDetails userDetails) {
        Long userId = getUserIdFromDetails(userDetails);

        CSAnswer answer = csAnswerRepository.findById(answerId) // 400
                .orElseThrow(() -> new IllegalArgumentException("답변이 존재하지 않습니다."));

        Long questionId = answer.getCsQuestionId();
        CSQuestionDto.Response question = remoteCSQuestionService.getQuestionById(questionId);

        // 이미 푼 문제인지 검사
        boolean canAccess = csAnswerRepository.existsByUserIdAndCsQuestionId(userId, answer.getCsQuestionId());
        if (!canAccess) { // 500
            throw new RuntimeException("문제를 푼 다음에 다른 사용자의 답변을 조회할 수 있습니다.");
        }

        // 답변 작성자 정보 조회
        UserDto.CSAnswerUserDto author = remoteUserService.getUserById(answer.getUserId());

        return buildAnswerDetailResponse(answer, author.getNickname(), question);
    }

    // 답변 수정
    public CSAnswerResponse.CSAnswerDetailResponse updateAnswer(Long answerId, CSAnswerRequest.CSAnswerUpdate request,
                                                                UserDetails userDetails) {

        Long userId = getUserIdFromDetails(userDetails);
        UserDto.CSAnswerUserDto user = remoteUserService.getUserById(userId);

        CSAnswer answer = csAnswerRepository.findById(answerId) // 400
                .orElseThrow(() -> new IllegalArgumentException("답변이 존재하지 않습니다."));

        Long questionId = answer.getCsQuestionId();
        CSQuestionDto.Response question = remoteCSQuestionService.getQuestionById(questionId);

        if (!answer.getUserId().equals(userId)) { // 500
            throw new RuntimeException("자신의 답변만 수정할 수 있습니다.");
        }

        answer.setContent(request.getCsanswer_content());
        answer.setFeedback("아직 피드백 없음");
        answer.setScore(0L);
        csAnswerRepository.save(answer);

        return buildAnswerDetailResponse(answer, user.getNickname(), question);
    }

    // 답변 삭제
    public void deleteAnswer(Long answerId, UserDetails userDetails) {
        Long userId = getUserIdFromDetails(userDetails);
        UserDto.CSAnswerUserDto user = remoteUserService.getUserById(userId);

        CSAnswer answer = csAnswerRepository.findById(answerId) // 400
                .orElseThrow(() -> new IllegalArgumentException("답변이 존재하지 않습니다."));

        if (!answer.getUserId().equals(userId)) { // 500
            throw new RuntimeException("자신의 답변만 삭제할 수 있습니다.");
        }
        csAnswerRepository.deleteById(answerId);
    }

    // 푼 문제 개수
    public Long countSolvedQuestion(String categoryName, UserDetails userDetails) {

        Long userId = getUserIdFromDetails(userDetails);
        List<CSAnswer> answers = csAnswerRepository.findAllByUserId(userId);

        Set<Long> distinctQuestionIds = answers.stream().map(CSAnswer::getCsQuestionId).collect(Collectors.toSet());

        if (categoryName == null) {
            return (long) distinctQuestionIds.size();
        }

        Long count = distinctQuestionIds.stream().filter(questionId -> {
                    try {
                        CSQuestionDto.Response question = remoteCSQuestionService.getQuestionById(questionId);
                        return categoryName.equals(question.getCategory().name());
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();


        return count;

    }

    // 통계를 위한 내 답변들 받아오기
    // 평균점수, 카테고리별 평균점수, 카테고리별 푼 문제 수 통계 서비스에서 구할 수 있게 데이터 전달
    public List<CSAnswerResponse.CSStatisticResponse> getStatisticAnswers(UserDetails userDetails) {
        Long userId = getUserIdFromDetails(userDetails);
        List<CSAnswer> answers = csAnswerRepository.findAllByUserId(userId);

        List<CSAnswerResponse.CSStatisticResponse> statisticAnswers = answers.stream().map((answer) ->
        {
            CSQuestionDto.Response question = remoteCSQuestionService.getQuestionById(answer.getCsQuestionId());


            return CSAnswerResponse.CSStatisticResponse.builder()
                    .csanswer_id(answer.getId())
                    .csanswer_score(answer.getScore())
                    .csquestion_id(answer.getCsQuestionId())
                    .csquestion_category(question.getCategory())
                    .build();
        }).toList();

        return statisticAnswers;
    }

    private Long getUserIdFromDetails(UserDetails userDetails) {
        // TODO: remoteUserService에서 findByEmail
        Long id = remoteUserService.getUserIdByEmail(userDetails.getUsername());
        if (id == null) {
            throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");
        }
        return id;
    }

    private CSAnswerResponse.CSAnswerDetailResponse buildAnswerDetailResponse(CSAnswer answer, String nickname, CSQuestionDto.Response question) {
        return CSAnswerResponse.CSAnswerDetailResponse.builder()
                .user_nickname(nickname)
                .csquestion_id(question.getId())
                .csquestion_category(question.getCategory())
                .csquestion_content(question.getContent())
                .csanswer_id(answer.getId())
                .csanswer_content(answer.getContent())
                .csanswer_score(answer.getScore())
                .csanswer_created_at(answer.getCreatedAt())
                .csanswer_feedback(answer.getFeedback())
                .build();
    }

    private CSAnswerResponse.CSAnswerListResponse buildAnswerListResponse(CSAnswer answer, String nickname, CSQuestionDto.Response question, boolean canAccess) {
        return CSAnswerResponse.CSAnswerListResponse.builder()
                .user_nickname(nickname)
                .csquestion_id(question.getId())
                .csquestion_category(question.getCategory())
                .csquestion_content(question.getContent())
                .csanswer_id(answer.getId())
                .csanswer_content(answer.getContent())
                .csanswer_score(answer.getScore())
                .csanswer_created_at(answer.getCreatedAt())
                .canAccess(canAccess)
                .build();
    }
}
