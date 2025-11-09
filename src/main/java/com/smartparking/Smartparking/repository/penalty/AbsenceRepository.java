package com.smartparking.Smartparking.repository.penalty;

import com.smartparking.Smartparking.entity.penalty.Absence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbsenceRepository extends JpaRepository<Absence, String> {}