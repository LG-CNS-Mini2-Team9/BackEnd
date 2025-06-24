package com.team9.statistic_service.domain.repository;

import com.team9.statistic_service.domain.entity.TierInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TierInfoRepository extends JpaRepository<TierInfoEntity, Long> {
    Optional<TierInfoEntity> findByTierName(String tierName);

    Optional<TierInfoEntity> findByTierLevel(Integer tierLevel);

    @Query("SELECT ti FROM TierInfoEntity ti ORDER BY ti.tierLevel")
    List<TierInfoEntity> findAllOrderByTierLevel();

    @Query("SELECT ti FROM TierInfoEntity ti WHERE ti.tierLevel > :currentLevel ORDER BY ti.tierLevel ASC")
    Optional<TierInfoEntity> findNextTier(@Param("currentLevel") Integer currentLevel);

    @Query("SELECT ti FROM TierInfoEntity ti WHERE ti.tierLevel < :currentLevel ORDER BY ti.tierLevel DESC")
    Optional<TierInfoEntity> findPreviousTier(@Param("currentLevel") Integer currentLevel);
}
