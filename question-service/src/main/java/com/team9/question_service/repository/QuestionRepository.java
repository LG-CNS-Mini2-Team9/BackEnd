package com.team9.question_service.repository;

import com.team9.question_service.domain.Question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.team9.common.domain.Category;
import java.time.LocalDateTime;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findByCategory(Category category, Pageable pageable);

    Question findFirstByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
}
