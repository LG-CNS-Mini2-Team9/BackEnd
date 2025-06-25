package com.team9.statistic_service.remote.answer;

import com.team9.statistic_service.remote.answer.dto.CSStatisticResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "answer-service",
        url = "${feign.client.config.answer-service.url:http://answer-service:8080}"
)
public interface RemoteAnswerService {

    /**
     * 사용자의 모든 답변 통계 데이터를 받아옴
     * answer-service의 /internal/answers/statistic 엔드포인트 호출
     */
    @GetMapping("/internal/answers/statistic")
    List<CSStatisticResponse> getMyAnswers(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(required = false) String categoryName
    );
}