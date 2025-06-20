package com.lgcns.backend.Like.repository;

import com.lgcns.backend.Like.entity.Like;
import com.lgcns.backend.comment.entity.Comment;
import com.lgcns.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByCommentAndUser(Comment comment, User user);
    void deleteByUser(User user);
}
