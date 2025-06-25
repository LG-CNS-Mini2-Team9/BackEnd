package com.team9.answer_service.domain;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class AnswerLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "answer_id", nullable = false)
    private Long answerId;

    @Column(name="author_id", nullable = false)
    private Long authorId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public AnswerLike(Long userId, Long answerId, Long authorId){
        this.userId = userId;
        this.answerId = answerId;
        this.authorId = authorId;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
