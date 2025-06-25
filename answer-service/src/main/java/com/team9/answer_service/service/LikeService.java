package com.team9.answer_service.service;

import com.team9.answer_service.domain.AnswerLike;
import com.team9.answer_service.domain.dto.LikeResponse;
import com.team9.answer_service.domain.repository.LikeRepository;
import com.team9.answer_service.remote.user.RemoteUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final RemoteUserService remoteUserService;

    public Long countMyAnswerLikes(Long userId) {
        return likeRepository.countByAuthorId(userId);
    }

    public LikeResponse getLikes(Long userId, Long answerId) {
        Optional<AnswerLike> like = likeRepository.findByUserIdAndAnswerId(userId, answerId);
        boolean isLiked;
        if (like.isPresent()) {
            isLiked = true;
        } else {
            isLiked = false;
        }
        return LikeResponse.builder().count(likeRepository.countByAnswerId(answerId)).isLiked(isLiked).build();
    }



}
