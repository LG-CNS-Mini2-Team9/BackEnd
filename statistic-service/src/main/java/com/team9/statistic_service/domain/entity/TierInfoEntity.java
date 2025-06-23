package com.team9.statistic_service.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tier_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TierInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 티어 이름 (뉴비, 루키, 에이스, 마스터, 레전드)
    @Column(name = "tier_name", nullable = false, unique = true, length = 20)
    private String tierName;

    // 티어 레벨 (1=뉴비, 2=루키, 3=에이스, 4=마스터, 5=레전드)
    @Column(name = "tier_level", nullable = false)
    private Integer tierLevel;

    // 최소 총 답변 수 ( 모든 티어 )
    @Column(name = "min_answer_count", nullable = false)
    private Integer minAnswerCount;

    // 카테고리별 최소 답변 수 ( 에이스, 마스터, 레전드 )
    @Column(name = "min_category_answer", nullable = false)
    private Integer minCategoryAnswer;

    // 최소 평균 점수 ( 마스터, 레전드 )
    @Column(name = "min_average_score", precision = 5, scale = 2)
    private BigDecimal minAverageScore;

    // 티어 설명
    @Column(name = "description", length = 500)
    private String description;

    // 티어 정보 생성 시간
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 엔티티 생성 시 자동으로 시간 설정
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
