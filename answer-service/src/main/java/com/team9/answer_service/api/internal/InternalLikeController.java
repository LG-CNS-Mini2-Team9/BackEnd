package com.team9.answer_service.api.internal;

import com.team9.answer_service.global.response.CustomResponse;
import com.team9.answer_service.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/internal/likes", produces = MediaType.APPLICATION_JSON_VALUE)
public class InternalLikeController {
    private final LikeService likeService;

    // 내가 받은 좋아요 수 (마이페이지용)
    @GetMapping("/my")
    public ResponseEntity<CustomResponse<Long>> countMyTotalLikes(@RequestHeader(value = "X-Auth-UserId") Long userId){
        Long count = likeService.countMyAnswerLikes(userId);
        return ResponseEntity.ok(CustomResponse.ok(count));
    }
}
