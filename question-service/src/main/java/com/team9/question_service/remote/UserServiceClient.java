package com.team9.question_service.remote;

import com.team9.common.response.CustomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * user-service와의 내부 통신을 위한 Feign Client 인터페이스
 */
@FeignClient("user-service") // Eureka에 등록된 "user-service"를 찾아 연결합니다.
public interface UserServiceClient {

    /**
     * user-service의 내부 API를 호출하여 특정 사용자의 관심 카테고리 목록을 가져옵니다.
     * API Gateway를 거치지 않는 서비스 간 통신을 위해 '/internal' 경로를 사용하는 것이 일반적입니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return CustomResponse 형태의 응답. 결과 데이터는 String 타입의 카테고리 이름 리스트입니다.
     */
    @GetMapping("/internal/users/interests")
    ResponseEntity<CustomResponse<List<String>>> getUserInterests(@RequestParam("userId") Long userId);
}