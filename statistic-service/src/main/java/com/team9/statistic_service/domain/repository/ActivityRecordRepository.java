package com.team9.statistic_service.domain.repository;

import com.team9.statistic_service.domain.entity.ActivityRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRecordRepository extends JpaRepository<ActivityRecordEntity, Long> {
    Optional<ActivityRecordEntity> findByUserIdAndActivityDate(Long userId, LocalDate activityDate);

    @Query("SELECT ar FROM ActivityRecordEntity ar WHERE ar.userId = :userId AND ar.activityDate BETWEEN :startDate AND :endDate ORDER BY ar.activityDate")
    List<ActivityRecordEntity> findByUserIdAndDateRange(@Param("userId") Long userId,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    @Query("SELECT ar FROM ActivityRecordEntity ar WHERE ar.userId = :userId AND YEAR(ar.activityDate) = :year ORDER BY ar.activityDate")
    List<ActivityRecordEntity> findByUserIdAndYear(@Param("userId") Long userId, @Param("year") int year);

    @Query("SELECT COUNT(ar) FROM ActivityRecordEntity ar WHERE ar.userId = :userId AND ar.isActive = true")
    Long countActiveRecordsByUserId(@Param("userId") Long userId);

    @Query("SELECT ar FROM ActivityRecordEntity ar WHERE ar.userId = :userId AND ar.isActive = true ORDER BY ar.activityDate DESC")
    List<ActivityRecordEntity> findActiveRecordsByUserIdOrderByDateDesc(@Param("userId") Long userId);
}
