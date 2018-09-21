package com.mo9.raptor.risk.repo;

import com.mo9.raptor.risk.entity.TRiskTelInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 10:56 .
 */
public interface RiskTelInfoRepository extends JpaRepository<TRiskTelInfo, Long>{
    
    @Query(value = "select * from t_risk_tel_info where mobile = ?1 and deleted = false ORDER by created_at desc limit 1", nativeQuery = true)
    TRiskTelInfo findByMobile(String mobile);
}
