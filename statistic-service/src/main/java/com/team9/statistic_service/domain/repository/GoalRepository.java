package com.team9.statistic_service.domain.repository;

import com.team9.statistic_service.domain.entity.GoalEntity;
import com.team9.statistic_service.domain.entity.GoalEntity.GoalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<GoalEntity, Long>{
    @Query("SELECT g FROM GoalEntity g WHERE g.userId = :userId AND g.isActive = true ORDER BY g.createdAt DESC")
    List<GoalEntity> findActiveGoalsByUserId(@Param("userId") Long userId);

    @Query("SELECT g FROM GoalEntity g WHERE g.userId = :userId AND g.goalType = :goalType AND g.isActive = true")
    Optional<GoalEntity> findActiveGoalByUserIdAndType(@Param("userId") Long userId, @Param("goalType") GoalType goalType);

    @Query("SELECT g FROM GoalEntity g WHERE g.userId = :userId AND g.startDate <= :date AND g.endDate >= :date AND g.isActive = true")
    List<GoalEntity> findActiveGoalsByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT g FROM GoalEntity g WHERE g.userId = :userId AND g.isAchieved = true ORDER BY g.updatedAt DESC")
    List<GoalEntity> findAchievedGoalsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(g) FROM GoalEntity g WHERE g.userId = :userId AND g.isAchieved = true")
    Long countAchievedGoalsByUserId(@Param("userId") Long userId);
}
