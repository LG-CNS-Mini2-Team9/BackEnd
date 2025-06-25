package com.team9.ai_feedback_service.domain.repository;

import com.team9.ai_feedback_service.domain.AIFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AIFeedbackRepository extends JpaRepository<AIFeedback, Long> {
    Optional<AIFeedback> findByAnswerId(Long answerId);
}
