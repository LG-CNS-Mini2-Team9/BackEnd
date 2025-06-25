package com.team9.question_service.domain;

import com.team9.question_service.global.domain.Category;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // private String difficulty; // difficulty 필드 제거

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean isActive = true;

    // hint 타입을 Map<String, Object>에서 String으로 변경
    // @JdbcTypeCode(SqlTypes.JSON) // JSON 타입 사용하지 않으므로 주석 처리 또는 제거
    @Column(columnDefinition = "TEXT") // TEXT 타입으로 변경
    private String hint; // Map<String, Object> -> String

    @Column(length = 50, unique = true)
    private String keyword;

    // content, hint 등을 수정할 수 있는 메서드 (difficulty 파라미터 제거)
    public void update(String content, String hint) { // difficulty 파라미터 제거
        this.content = content;
        this.hint = hint; // hint 타입 변경에 맞춰 수정
    }

    public void deactivate() {
        this.isActive = false;
    }
}