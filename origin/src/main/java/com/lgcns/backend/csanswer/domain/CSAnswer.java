package com.lgcns.backend.csanswer.domain;

import java.time.LocalDateTime;

import com.lgcns.backend.user.domain.User;
import com.lgcns.backend.csquestion.domain.CSQuestion;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CSAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Lob
    @Column(nullable = false)
    private String feedback;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private CSQuestion csQuestion;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
