package com.lgcns.backend.Like.controller;

import com.lgcns.backend.Like.service.LikeService;
import com.lgcns.backend.global.response.CustomResponse;
import com.lgcns.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comm")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{commentId}/like")
    public ResponseEntity<CustomResponse<?>> toggleLike(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails){

        return ResponseEntity.ok(CustomResponse.ok(likeService.toggleLike(commentId, userDetails)));
    }
}
