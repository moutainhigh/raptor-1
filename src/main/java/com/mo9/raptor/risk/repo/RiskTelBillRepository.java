package com.mo9.raptor.risk.repo;

import com.mo9.raptor.risk.entity.TRiskTelBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 10:55 .
 */
public interface RiskTelBillRepository extends JpaRepository<TRiskTelBill, Long> {
    
    @Query(value = "select * from t_risk_tel_bill where mobile = ?1 and bill_month = ?2 and deleted = false limit 1", nativeQuery = true)
    TRiskTelBill findOneBill(String mobile, String billMonth);
}
