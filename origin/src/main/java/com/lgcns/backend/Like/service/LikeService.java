package com.lgcns.backend.Like.service;

import com.lgcns.backend.Like.dto.LikeResponse;
import com.lgcns.backend.Like.entity.Like;
import com.lgcns.backend.Like.repository.LikeRepository;
import com.lgcns.backend.comment.entity.Comment;
import com.lgcns.backend.comment.respository.CommentRepository;
import com.lgcns.backend.global.code.GeneralErrorCode;
import com.lgcns.backend.global.code.PostErrorCode;
import com.lgcns.backend.global.exception.CustomException;
import com.lgcns.backend.user.domain.User;
import com.lgcns.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public LikeResponse toggleLike(Long commentId, UserDetails userDetails) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(GeneralErrorCode._NOT_FOUND));

        User user = getUserFromDetails(userDetails);

        Optional<Like> existingLike = likeRepository.findByCommentAndUser(comment, user);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return LikeResponse.from(comment.getId(), false);
        } else {
            Like like = Like.builder()
                    .comment(comment)
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .build();
            likeRepository.save(like);

            return LikeResponse.from(comment.getId(), true);
        }

    }
    private User getUserFromDetails(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(PostErrorCode.UNAUTHORIZED_ACCESS));
    }

}
