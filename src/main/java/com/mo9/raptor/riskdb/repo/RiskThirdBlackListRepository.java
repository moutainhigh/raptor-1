package com.mo9.raptor.riskdb.repo;

import com.mo9.raptor.riskdb.entity.TRiskThirdBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface RiskThirdBlackListRepository extends JpaRepository<TRiskThirdBlackList, Long> {

    @Query(value = "SELECT count(*)>0 from t_risk_third_black_list WHERE VALUE_TYPE in ('mobile','idcard')  and BLACK_VALUE IS NOT NULL and BLACK_VALUE = ?1", nativeQuery = true)
    Integer isInBlackList(String value);
}
