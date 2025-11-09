package com.smartparking.Smartparking.repository.penalty;

import com.smartparking.Smartparking.entity.penalty.PenaltyEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PenaltyEventRepository extends JpaRepository<PenaltyEvent, String> {}