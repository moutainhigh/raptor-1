package com.mo9.raptor.risk.repo;

import com.mo9.raptor.risk.entity.TRiskTelInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 10:56 .
 */
public interface RiskTelInfoRepository extends JpaRepository<TRiskTelInfo, Long>{
    
    @Query(value = "select * from t_risk_tel_info where mobile = ?1 and platform = ?2 and deleted = false ORDER by created_at desc limit 1", nativeQuery = true)
    TRiskTelInfo findByMobile(String mobile, String platform);
    
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update TRiskTelInfo t set t.sid=?1, t.uid=?2, t.fullName=?3, t.address=?4, t.idCard=?5, t.openDate=?6, t.updatedAt=?7, t.reportReceived=?8 where t.mobile = ?9 and t.platform = ?10")
    void update(String sid, String uid, String fullName, String address, String idCard, String openDate, Date updatedAt,boolean reportReceived, String mobile, String platform);

    
    @Query(value = "select * from t_risk_tel_info where created_at > ?1 and platform = ?2 and deleted = false and report_received = false ", nativeQuery = true)
    Set<TRiskTelInfo> findNoReportRecords(Date start, String platform);

    @Query(value = "select mobile from t_risk_tel_info where created_at > ?1 and platform = ?2 and deleted = false and report_received = false ", nativeQuery = true)
    Set<String> findNoReportMobiles(Date start, String platform);
}
