package com.lgcns.backend.csanswer.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.lgcns.backend.csanswer.domain.CSAnswer;
import com.lgcns.backend.csanswer.dto.CSAnswerRequest;
import com.lgcns.backend.csanswer.dto.CSAnswerResponse;
import com.lgcns.backend.csanswer.repository.CSAnswerRepository;
import com.lgcns.backend.csquestion.domain.CSQuestion;
import com.lgcns.backend.csquestion.repository.CSQuestionRepository;
import com.lgcns.backend.user.domain.User;
import com.lgcns.backend.user.repository.UserRepository;

@Service
public class CSAnswerService {

    @Autowired
    private CSAnswerRepository csAnswerRepository;

    @Autowired
    private CSQuestionRepository csQuestionRepository;

    @Autowired
    private UserRepository userRepository;

    // 답변 작성
    public CSAnswerResponse.CSAnswerDetailResponse createAnswer(CSAnswerRequest.CSAnswerCreateRequest request,
            UserDetails userDetails) {

        User user = getUserFromDetails(userDetails);

        CSQuestion question = csQuestionRepository.findById(request.getCsquestion_id()) // 400
                .orElseThrow(() -> new IllegalArgumentException("질문이 존재하지 않습니다."));

        CSAnswer answer = new CSAnswer();
        answer.setContent(request.getCsanswer_content());
        answer.setCreatedAt(LocalDateTime.now());
        answer.setFeedback("아직 피드백 없음");
        answer.setCsQuestion(question);
        answer.setUser(user);

        csAnswerRepository.save(answer);

        return CSAnswerResponse.CSAnswerDetailResponse.builder()
                .csquestion_id(question.getId())
                .csquestion_category(question.getCategory())
                .csquestion_content(question.getContent())
                .csanswer_id(answer.getId())
                .csanswer_content(answer.getContent())
                .csanswer_created_at(answer.getCreatedAt())
                .csanswer_feedback(answer.getFeedback())
                .build();
    }

    // 답변 리스트 조회 (페이지, 특정 질문)
    public Page<CSAnswerResponse.CSAnswerListResponse> getAnswerList(UserDetails userDetails, Pageable pageable, Long questionId) {

        User user = getUserFromDetails(userDetails);

        Page<CSAnswer> answers;
        if(questionId== null) {
            answers = csAnswerRepository.findAllByUser(user, pageable);
        } else {
            CSQuestion tquestion = csQuestionRepository.findById(questionId) // 400
                .orElseThrow(() -> new IllegalArgumentException("질문이 존재하지 않습니다."));
            answers = csAnswerRepository.findAllByUserAndCsQuestion(user, tquestion, pageable);
        }

        return answers.map(answer -> {
            CSQuestion question = answer.getCsQuestion();
            return CSAnswerResponse.CSAnswerListResponse.builder()
                    .user_nickname(answer.getUser().getNickname())
                    .csquestion_id(question.getId())
                    .csquestion_category(question.getCategory())
                    .csquestion_content(question.getContent())
                    .csanswer_id(answer.getId())
                    .csanswer_content(answer.getContent())
                    .csanswer_created_at(answer.getCreatedAt())
                    .build();
        });
    }

    // 특정 답변 조회
    public CSAnswerResponse.CSAnswerDetailResponse getAnswerDetail(Long answerId, UserDetails userDetails) {

        User user = getUserFromDetails(userDetails);

        CSAnswer answer = csAnswerRepository.findById(answerId) // 400
                .orElseThrow(() -> new IllegalArgumentException("답변이 존재하지 않습니다."));

        CSQuestion question = answer.getCsQuestion();

        if (!answer.getUser().getId().equals(user.getId())) { // 500
            throw new RuntimeException("자신의 답변만 조회할 수 있습니다.");
        }

        return CSAnswerResponse.CSAnswerDetailResponse.builder()
                .csquestion_id(question.getId())
                .csquestion_category(question.getCategory())
                .csquestion_content(question.getContent())
                .csanswer_id(answer.getId())
                .csanswer_content(answer.getContent())
                .csanswer_created_at(answer.getCreatedAt())
                .csanswer_feedback(answer.getFeedback())
                .build();
    }

    // 답변 수정
    public CSAnswerResponse.CSAnswerDetailResponse updateAnswer(Long answerId, CSAnswerRequest.CSAnswerUpdate request,
            UserDetails userDetails) {

        User user = getUserFromDetails(userDetails);

        CSAnswer answer = csAnswerRepository.findById(answerId) // 400
                .orElseThrow(() -> new IllegalArgumentException("답변이 존재하지 않습니다."));

        CSQuestion question = answer.getCsQuestion();

        if (!answer.getUser().getId().equals(user.getId())) { // 500
            throw new RuntimeException("자신의 답변만 수정할 수 있습니다.");
        }

        answer.setContent(request.getCsanswer_content());
        answer.setFeedback("아직 피드백 없음");
        csAnswerRepository.save(answer);

        return CSAnswerResponse.CSAnswerDetailResponse.builder()
                .csquestion_id(question.getId())
                .csquestion_category(question.getCategory())
                .csquestion_content(question.getContent())
                .csanswer_id(answer.getId())
                .csanswer_content(answer.getContent())
                .csanswer_created_at(answer.getCreatedAt())
                .csanswer_feedback(answer.getFeedback())
                .build();
    }

    // 답변 삭제
    public void deleteAnswer(Long answerId, UserDetails userDetails) {

        User user = getUserFromDetails(userDetails);

        CSAnswer answer = csAnswerRepository.findById(answerId) // 400
                .orElseThrow(() -> new IllegalArgumentException("답변이 존재하지 않습니다."));

        if (!answer.getUser().getId().equals(user.getId())) { // 500
            throw new RuntimeException("자신의 답변만 삭제할 수 있습니다.");
        }
        csAnswerRepository.deleteById(answerId);
    }

    private User getUserFromDetails(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));
    }

}
