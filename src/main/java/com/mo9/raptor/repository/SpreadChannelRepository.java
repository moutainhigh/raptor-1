package com.mo9.raptor.repository;

import com.mo9.raptor.entity.LinkfaceLogEntity;
import com.mo9.raptor.entity.RuleLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RuleLogRepository extends JpaRepository<RuleLogEntity, Long> {
}
