package com.team9.answer_service.domain.repository;

import com.team9.answer_service.domain.CSAnswer;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CSAnswerRepository extends JpaRepository<CSAnswer, Long>{
    List<CSAnswer> findAllByUserId(Long userId);
    Page<CSAnswer> findAllByUserId(Long userId, Pageable pageable);
    Page<CSAnswer> findAllByUserIdAndCsQuestionId(Long userId, Long csQuestionId, Pageable pageable);
}
