package com.smartparking.Smartparking.repository.penalty;

import com.smartparking.Smartparking.entity.penalty.AbsenceCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AbsenceCounterRepository extends JpaRepository<AbsenceCounter, String> {

    @Query("SELECT COALESCE(SUM(ac.absenceCount), 0) FROM AbsenceCounter ac WHERE ac.user.userId = :userId")
    Long getTotalAbsenceCountByUserId(@Param("userId") String userId);

    // Alternativa: último registro
    AbsenceCounter findTopByUserUserIdOrderByLastUpdatedDesc(String userId);
}