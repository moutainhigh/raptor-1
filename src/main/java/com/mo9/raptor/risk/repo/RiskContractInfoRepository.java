package com.mo9.raptor.risk.repo;

import com.mo9.raptor.risk.entity.TRiskContractInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by jyou on 2018/10/18.
 *
 * @author jyou
 */
public interface RiskContractInfoRepository extends JpaRepository<TRiskContractInfo, Long> {

    @Query(value = "select * from t_risk_contract_info where mobile = ?1 and contact_mobile in ?2 and is_deleted = 0", nativeQuery = true)
    List<TRiskContractInfo> findByMobileAndContractMobilesList(String mobile, List<String> contractMobilesList);

    @Modifying
    @Query(value = "update t_risk_contract_info set is_matching = 1 where mobile = ?1 and  contact_mobile in ?2", nativeQuery = true)
    @Transactional
    void updateMatchingMobile(String userCode, List<String> inListMobiles);
}
