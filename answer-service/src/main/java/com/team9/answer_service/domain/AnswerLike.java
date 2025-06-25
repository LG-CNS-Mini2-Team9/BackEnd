package com.team9.answer_service.domain;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(
        name = "answer_like",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "answer_id"})}
)
@NoArgsConstructor
public class AnswerLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "answer_id", nullable = false)
    private Long answerId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public AnswerLike(Long userId, Long answerId){
        this.userId = userId;
        this.answerId = answerId;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
