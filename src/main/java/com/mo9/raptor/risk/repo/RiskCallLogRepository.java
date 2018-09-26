package com.mo9.raptor.risk.repo;

import com.mo9.raptor.risk.entity.TRiskCallLog;
import org.springframework.data.domain.Example;
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

    @Query(value = "select count(*) from t_risk_call_log where mobile = ?1 AND call_time>?2", nativeQuery = true)
    Integer getCallLogCountAfterTimestamp(String mobile, Long timestampAfter);

    @Query(value = "select * from t_risk_call_log where mobile = ?1 and call_tel = ?2 and call_time = ?3 and deleted = false limit 1", nativeQuery = true)
    TRiskCallLog findOneCallLog(String mobile, String callTel, String callTime);
}
