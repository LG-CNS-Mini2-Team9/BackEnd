package com.team9.statistic_service.domain.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "goal")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 목표 타입 (DAILY=일일목표, WEEKLY=주간목표)
    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type", nullable = false)
    private GoalType goalType;

    // 목표 답변 개수 (예: 하루 3개, 일주일 10개)
    @Column(name = "target_count", nullable = false)
    private Integer targetCount;

    // 현재까지 달성한 답변 개수
    @Column(name = "current_count", nullable = false)
    private Integer currentCount = 0;

    // 목표 시작일
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // 목표 종료일 (일일목표는 시작일과 동일, 주간목표는 +6일)
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // 목표 달성 여부 (target_count 도달 시 true)
    @Column(name = "is_achieved", nullable = false)
    private Boolean isAchieved = false;

    // 목표 활성화 여부 (기간 만료 또는 사용자가 비활성화 시 false)
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 목표 생성 시간
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 목표 수정 시간 (진행도 업데이트 시 갱신)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    // 엔티티 생성 시 자동으로 시간 설정
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }


    //엔티티 업데이트 시 자동으로 수정 시간 갱신
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    public enum GoalType {
        DAILY,  // 일일 목표 (하루에 몇개 답변하기)
        WEEKLY  // 주간 목표 (일주일에 몇개 답변하기)
    }
}
