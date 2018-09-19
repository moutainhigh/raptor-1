package com.mo9.raptor.risk.repo;

import com.mo9.raptor.risk.entity.TRiskCallLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 10:54 .
 */
public interface RiskCallLogRepository extends JpaRepository<TRiskCallLog, Long> {

    @Query(value = "select * from t_risk_call_log where uid = ?1", nativeQuery = true)
    List<TRiskCallLog> getCallLogByUid(String uid);

}
