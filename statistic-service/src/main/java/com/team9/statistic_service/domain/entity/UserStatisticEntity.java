package com.team9.statistic_service.domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "user_statistic")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatisticEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 ID (user-service의 사용자와 연결)
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    // 전체 답변 개수 (모든 카테고리 합계)
    @Column(name = "total_answer_count", nullable = false)
    private Long totalAnswerCount = 0L;

    // 자료구조 카테고리 답변 개수
    @Column(name = "data_structure_count", nullable = false)
    private Long dataStructureCount = 0L;

    // 컴퓨터구조 카테고리 답변 개수
    @Column(name = "computer_architecture_count", nullable = false)
    private Long computerArchitectureCount = 0L;

    // 운영체제 카테고리 답변 개수
    @Column(name = "operating_system_count", nullable = false)
    private Long operatingSystemCount = 0L;

    // 데이터베이스 카테고리 답변 개수
    @Column(name = "database_count", nullable = false)
    private Long databaseCount = 0L;

    // 네트워크 카테고리 답변 개수
    @Column(name = "network_count", nullable = false)
    private Long networkCount = 0L;

    // 소프트웨어공학 카테고리 답변 개수
    @Column(name = "software_engineering_count", nullable = false)
    private Long softwareEngineeringCount = 0L;

    // 알고리즘 카테고리 답변 개수
    @Column(name = "algorithm_count", nullable = false)
    private Long algorithmCount = 0L;

    // 디자인패턴 카테고리 답변 개수
    @Column(name = "design_pattern_count", nullable = false)
    private Long designPatternCount = 0L;

    // 웹프론트엔드 카테고리 답변 개수
    @Column(name = "web_frontend_count", nullable = false)
    private Long webFrontendCount = 0L;

    // 웹백엔드 카테고리 답변 개수
    @Column(name = "web_backend_count", nullable = false)
    private Long webBackendCount = 0L;

    // 클라우드 카테고리 답변 개수
    @Column(name = "cloud_count", nullable = false)
    private Long cloudCount = 0L;


    // 전체 점수 합계 (모든 답변의 점수 총합)
    @Column(name = "total_score", precision = 10, scale = 2)
    private BigDecimal totalScore = BigDecimal.ZERO;

    // 자료구조 카테고리 점수 합계
    @Column(name = "data_structure_score", precision = 10, scale = 2)
    private BigDecimal dataStructureScore = BigDecimal.ZERO;

    // 컴퓨터구조 카테고리 점수 합계
    @Column(name = "computer_architecture_score", precision = 10, scale = 2)
    private BigDecimal computerArchitectureScore = BigDecimal.ZERO;

    // 운영체제 카테고리 점수 합계
    @Column(name = "operating_system_score", precision = 10, scale = 2)
    private BigDecimal operatingSystemScore = BigDecimal.ZERO;

    // 데이터베이스 카테고리 점수 합계
    @Column(name = "database_score", precision = 10, scale = 2)
    private BigDecimal databaseScore = BigDecimal.ZERO;

    // 네트워크 카테고리 점수 합계
    @Column(name = "network_score", precision = 10, scale = 2)
    private BigDecimal networkScore = BigDecimal.ZERO;

    // 소프트웨어공학 카테고리 점수 합계
    @Column(name = "software_engineering_score", precision = 10, scale = 2)
    private BigDecimal softwareEngineeringScore = BigDecimal.ZERO;

    // 알고리즘 카테고리 점수 합계
    @Column(name = "algorithm_score", precision = 10, scale = 2)
    private BigDecimal algorithmScore = BigDecimal.ZERO;

    // 디자인패턴 카테고리 점수 합계
    @Column(name = "design_pattern_score", precision = 10, scale = 2)
    private BigDecimal designPatternScore = BigDecimal.ZERO;

    // 웹프론트엔드 카테고리 점수 합계
    @Column(name = "web_frontend_score", precision = 10, scale = 2)
    private BigDecimal webFrontendScore = BigDecimal.ZERO;

    // 웹백엔드 카테고리 점수 합계
    @Column(name = "web_backend_score", precision = 10, scale = 2)
    private BigDecimal webBackendScore = BigDecimal.ZERO;

    // 클라우드 카테고리 점수 합계
    @Column(name = "cloud_score", precision = 10, scale = 2)
    private BigDecimal cloudScore = BigDecimal.ZERO;


    // 전체 평균 점수 (총점수 ÷ 총답변수)
    @Column(name = "average_score", precision = 5, scale = 2)
    private BigDecimal averageScore = BigDecimal.ZERO;

    // 자료구조 카테고리 평균 점수
    @Column(name = "data_structure_avg", precision = 5, scale = 2)
    private BigDecimal dataStructureAvg = BigDecimal.ZERO;

    // 컴퓨터구조 카테고리 평균 점수
    @Column(name = "computer_architecture_avg", precision = 5, scale = 2)
    private BigDecimal computerArchitectureAvg = BigDecimal.ZERO;

    // 운영체제 카테고리 평균 점수
    @Column(name = "operating_system_avg", precision = 5, scale = 2)
    private BigDecimal operatingSystemAvg = BigDecimal.ZERO;

    // 데이터베이스 카테고리 평균 점수
    @Column(name = "database_avg", precision = 5, scale = 2)
    private BigDecimal databaseAvg = BigDecimal.ZERO;

    // 네트워크 카테고리 평균 점수
    @Column(name = "network_avg", precision = 5, scale = 2)
    private BigDecimal networkAvg = BigDecimal.ZERO;

    // 소프트웨어공학 카테고리 평균 점수
    @Column(name = "software_engineering_avg", precision = 5, scale = 2)
    private BigDecimal softwareEngineeringAvg = BigDecimal.ZERO;

    // 알고리즘 카테고리 평균 점수
    @Column(name = "algorithm_avg", precision = 5, scale = 2)
    private BigDecimal algorithmAvg = BigDecimal.ZERO;

    // 디자인패턴 카테고리 평균 점수
    @Column(name = "design_pattern_avg", precision = 5, scale = 2)
    private BigDecimal designPatternAvg = BigDecimal.ZERO;

    // 웹프론트엔드 카테고리 평균 점수
    @Column(name = "web_frontend_avg", precision = 5, scale = 2)
    private BigDecimal webFrontendAvg = BigDecimal.ZERO;

    // 웹백엔드 카테고리 평균 점수
    @Column(name = "web_backend_avg", precision = 5, scale = 2)
    private BigDecimal webBackendAvg = BigDecimal.ZERO;

    // 클라우드 카테고리 평균 점수
    @Column(name = "cloud_avg", precision = 5, scale = 2)
    private BigDecimal cloudAvg = BigDecimal.ZERO;

    // 현재 티어 (뉴비, 루키, 에이스, 마스터, 레전드)
    @Column(name = "current_tier", length = 20)
    private String currentTier = "뉴비";

    // 통계 데이터 최초 생성 시간
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 통계 데이터 마지막 업데이트 시간
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    // 엔티티 생성 시 자동으로 시간 설정
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }


    // 엔티티 업데이트 시 자동으로 수정 시간 갱신
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
