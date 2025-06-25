package com.team9.answer_service.api.internal;

import com.team9.answer_service.domain.CSAnswer;
import com.team9.answer_service.domain.dto.CSAnswerResponse;
import com.team9.answer_service.domain.repository.CSAnswerRepository;
import com.team9.answer_service.service.CSAnswerService;
import com.team9.common.response.CustomResponse; // common 모듈의 응답 객체
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/answers") // 서비스 간 내부 통신용 경로는 보통 /internal 로 시작합니다.
public class InternalAnswerController {
    private final CSAnswerService csAnswerService;
    private final CSAnswerRepository csAnswerRepository;

    /**
     * [내부 통신용] question-service의 요청을 받아 특정 사용자가 답변한 모든 질문의 ID 목록을 반환합니다.
     * N+1 문제를 방지하기 위해 단일 API 호출로 모든 ID를 제공합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 해당 사용자가 답변한 모든 질문의 ID 목록
     */
    @GetMapping("/submitted-ids")
    public ResponseEntity<CustomResponse<List<Long>>> getSubmittedQuestionIdsByUserId(@RequestParam("userId") Long userId) {
        log.info("내부 API 호출: userId {}의 제출된 질문 ID 목록 조회", userId);

        // Repository를 통해 해당 사용자의 모든 답변을 조회합니다.
        List<CSAnswer> answers = csAnswerRepository.findAllByUserId(userId);

        // 전체 답변 엔티티에서 질문 ID(csQuestionId)만 추출하여 리스트로 만듭니다.
        List<Long> questionIds = answers.stream()
                .map(CSAnswer::getCsQuestionId)
                .distinct() // 혹시 모를 중복을 제거합니다.
                .collect(Collectors.toList());

        log.info("조회 결과: userId {}가 답변한 질문 ID {}개 반환", userId, questionIds.size());
        return ResponseEntity.ok(CustomResponse.ok(questionIds));
    }

    @GetMapping("/count")
    public Long countSolvedQuestion(@RequestParam(required=false) String categoryName, @AuthenticationPrincipal UserDetails userDetails){
        return csAnswerService.countSolvedQuestion(categoryName, userDetails);
    }

    // 통계를 위한 내 답변들 받아오기
    // 평균점수, 카테고리별 평균점수, 카테고리별 푼 문제 수 통계 서비스에서 구할 수 있게 데이터 전달
    @GetMapping("/statistic")
    public List<CSAnswerResponse.CSStatisticResponse> getMyAnswers(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(required = false) String categoryName){
        return csAnswerService.getStatisticAnswers(userDetails, categoryName);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<String> deleteAnswersByUserId(@PathVariable Long userId){
        csAnswerService.deleteAllByUserId(userId);
        return ResponseEntity.ok("해당 유저의 답변 삭제 완료");
    }

}