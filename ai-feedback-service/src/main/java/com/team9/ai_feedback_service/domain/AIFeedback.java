package com.team9.ai_feedback_service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class AIFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long answerId;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String feedback;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public AIFeedback(Long answerId, int score, String feedback) {
        this.answerId = answerId;
        this.score = score;
        this.feedback = feedback;
    }

    public static AIFeedback of(Long answerId, int score, String feedback) {
        return new AIFeedback(answerId, score, feedback);
    }

    public void update(int score, String feedback) {
        this.score = score;
        this.feedback = feedback;
    }
}
