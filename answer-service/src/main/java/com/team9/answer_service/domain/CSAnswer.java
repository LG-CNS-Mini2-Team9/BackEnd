package com.team9.answer_service.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class CSAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String feedback;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "question_id", nullable = false)
    private Long csQuestionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;
}
