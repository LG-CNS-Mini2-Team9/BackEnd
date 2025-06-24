package com.team9.statistic_service.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_record",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "activity_date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 활동 날짜 (YYYY-MM-DD 형식)
    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;

    // 해당 날짜에 작성한 답변 개수
    @Column(name = "answer_count", nullable = false)
    private Integer answerCount = 0;

    // 잔디 활성화 여부 (하루 1개 이상 답변 시 true)
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    // 활동 기록 생성 시간
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 활동 기록 수정 시간 (답변 추가 시 갱신)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    //엔티티 생성 시 자동으로 시간 설정
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
