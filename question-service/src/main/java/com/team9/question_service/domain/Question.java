package com.team9.question_service.domain;

import com.team9.common.domain.Category; // common 모듈의 Category 사용
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class) // createdAt 자동 생성을 위해 추가
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "question") // 테이블명을 'question'으로 명시
public class Question { // 클래스 이름 변경: CSQuestion -> Question

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(columnDefinition = "TEXT", nullable = false) // TEXT 타입 명시
    private String content;

    @Column(length = 10) // 길이 제한
    private String difficulty; // 'EASY', 'MEDIUM', 'HARD'

    @CreatedDate // JPA Auditing 기능으로 자동 생성
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean isActive = true; // 기본값 true

    @JdbcTypeCode(SqlTypes.JSON) // JPA 2.2 이상에서 JSON 타입을 다루는 표준 방법
    @Column(columnDefinition = "json")
    private Map<String, Object> hint; // JSON 타입의 힌트 (예: {"keyword": "TCP/IP"})

    // content, difficulty, hint 등을 수정할 수 있는 메서드 (필요시)
    public void update(String content, String difficulty, Map<String, Object> hint) {
        this.content = content;
        this.difficulty = difficulty;
        this.hint = hint;
    }

    // 질문 비활성화 메서드
    public void deactivate() {
        this.isActive = false;
    }
}