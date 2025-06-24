package com.team9.statistic_service.domain.repository;

import com.team9.statistic_service.domain.entity.UserStatisticEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStatisticRepository extends JpaRepository<UserStatisticEntity, Long> {

    Optional<UserStatisticEntity> findByUserId(Long userId);

    @Query("SELECT COUNT(us) FROM UserStatisticEntity us WHERE us.currentTier = :tierName")
    Long countByCurrentTier(@Param("tierName") String tierName);

    @Query("SELECT us FROM UserStatisticEntity us WHERE us.totalAnswerCount >= :minCount ORDER BY us.averageScore DESC")
    List<UserStatisticEntity> findTopUsersByAnswerCount(@Param("minCount") Long minCount);
}
