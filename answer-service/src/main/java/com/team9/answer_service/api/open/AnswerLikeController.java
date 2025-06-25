package com.team9.answer_service.api.open;

import com.team9.answer_service.domain.AnswerLike;
import com.team9.answer_service.domain.repository.LikeRepository;
import com.team9.answer_service.global.response.CustomResponse;
import com.team9.answer_service.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/likes", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AnswerLikeController {
    private final LikeRepository likeRepository;
    private final LikeService likeService;

    @PostMapping
    public void like(Long userId, Long answerId){
        AnswerLike like = new AnswerLike(userId, answerId);
        likeRepository.save(like);
    }

    @PostMapping("/delete")
    public void unlike(Long userId, Long answerId){
        likeRepository.deleteByUserIdAndAnswerId(userId, answerId);
    }

    @GetMapping
    public ResponseEntity<CustomResponse<Long>> getLikeCount(Long answerId){
        return ResponseEntity.ok(CustomResponse.ok(likeRepository.countByAnswerId(answerId)));
    }

    // 내가 받은 좋아요 수 (마이페이지용)
    @GetMapping("/my")
    public ResponseEntity<CustomResponse<Long>> countMyTotalLikes(@AuthenticationPrincipal UserDetails userDetails){
//        Long count = likeService.countMyAnswerLikes(userDetails);
        Long count = likeService.countMyAnswerLikes();
        return ResponseEntity.ok(CustomResponse.ok(count));
    }


}
