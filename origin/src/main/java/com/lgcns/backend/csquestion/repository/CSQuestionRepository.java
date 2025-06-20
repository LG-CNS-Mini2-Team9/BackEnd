package com.lgcns.backend.csquestion.repository;

import com.lgcns.backend.csquestion.domain.CSQuestion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.lgcns.backend.global.domain.Category;
import java.time.LocalDateTime;

public interface CSQuestionRepository extends JpaRepository<CSQuestion, Long> {
    Page<CSQuestion> findByCategory(Category category, Pageable pageable);

    CSQuestion findFirstByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
}
