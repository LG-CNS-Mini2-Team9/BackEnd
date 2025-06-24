package com.team9.answer_service.api.backend;

import com.team9.answer_service.domain.dto.CSAnswerResponse;
import com.team9.answer_service.domain.repository.CSAnswerRepository;
import com.team9.answer_service.global.domain.Category;
import com.team9.answer_service.service.CSAnswerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/backend/answers/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BackendCSAnswerController {
    private final CSAnswerService csAnswerService;

    @GetMapping("/count")
    public Long countSolvedQuestion(@RequestParam(required=false) String categoryName, @AuthenticationPrincipal UserDetails userDetails){
        return csAnswerService.countSolvedQuestion(categoryName, userDetails);
    }

    // 통계를 위한 내 답변들 받아오기
    // 평균점수, 카테고리별 평균점수, 카테고리별 푼 문제 수 통계 서비스에서 구할 수 있게 데이터 전달
    @GetMapping
    public List<CSAnswerResponse.CSStatisticResponse> getAnswers(@AuthenticationPrincipal UserDetails userDetails){
        return csAnswerService.getStatisticAnswers(userDetails);
    }
}
