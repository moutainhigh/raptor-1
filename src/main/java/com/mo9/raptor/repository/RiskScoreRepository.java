package com.mo9.raptor.repository;

import com.mo9.raptor.entity.RiskScoreEntity;
import com.mo9.raptor.entity.RuleLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskScoreRepository extends JpaRepository<RiskScoreEntity, Long> {

}
