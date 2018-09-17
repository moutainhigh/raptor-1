package com.mo9.raptor.risk.repo;

import com.mo9.raptor.entity.BankEntity;
import com.mo9.raptor.risk.entity.TRiskCallLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 10:54 .
 */
public interface RiskCallLogRepository extends JpaRepository<TRiskCallLog,Long> {
}
