package com.team9.answer_service.domain.repository;

import com.team9.answer_service.domain.AnswerLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<AnswerLike, Long> {
    Optional<AnswerLike> findByUserIdAndAnswerId(Long userId, Long answerId);

    Long countByAnswerId(Long answerId);

    void deleteByUserIdAndAnswerId(Long userId, Long answerId);

    @Query("SELECT COUNT(al) FROM AnswerLike al WHERE al.answerId IN " +
            "(SELECT a.id FROM Answer a WHERE a.userId = :userId)")
    long countAllLikesReceivedByUser(@Param("userId") Long userId);
}