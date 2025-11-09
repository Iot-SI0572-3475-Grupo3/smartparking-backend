package com.smartparking.Smartparking.repository.penalty;

import com.smartparking.Smartparking.entity.penalty.AbsenceCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AbsenceCounterRepository extends JpaRepository<AbsenceCounter, String> {

    Optional<AbsenceCounter> findByUserId(String userId);

    Optional<AbsenceCounter> findTopByUserIdOrderByLastUpdatedDesc(String userId);

    // @Query corregidos
    @Query("SELECT COALESCE(SUM(ac.absenceCount), 0) FROM AbsenceCounter ac WHERE ac.userId = :userId")
    Long getTotalAbsenceCountByUserId(@Param("userId") String userId);

    @Query("SELECT COALESCE(SUM(ac.strikeCount), 0) FROM AbsenceCounter ac WHERE ac.userId = :userId")
    Long getTotalStrikeCountByUserId(@Param("userId") String userId);
}