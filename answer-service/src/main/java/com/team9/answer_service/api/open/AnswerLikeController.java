package com.team9.answer_service.api.open;

import com.team9.answer_service.domain.AnswerLike;
import com.team9.answer_service.domain.dto.LikeResponse;
import com.team9.answer_service.domain.repository.LikeRepository;
import com.team9.answer_service.global.response.CustomResponse;
import com.team9.answer_service.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/likes", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AnswerLikeController {
    private final LikeRepository likeRepository;
    private final LikeService likeService;

    @PostMapping
    public void like(Long userId, Long answerId, Long authorId){
        AnswerLike like = new AnswerLike(userId, answerId, authorId);
        likeRepository.save(like);
    }

    @PostMapping("/delete")
    public void unlike(Long userId, Long answerId){
        likeRepository.deleteByUserIdAndAnswerId(userId, answerId);
    }

    @GetMapping
    public ResponseEntity<CustomResponse<LikeResponse>> getLikeCount(@RequestHeader(value = "X-User-Id") Long userId, Long answerId){
        return ResponseEntity.ok(CustomResponse.ok(likeService.getLikes(userId, answerId)));
    }



}
