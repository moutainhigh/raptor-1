package com.mo9.raptor.repository;

import com.mo9.raptor.entity.CashAccountLogEntity;
import com.mo9.raptor.enums.BusinessTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by xtgu on 2018/9/12.
 * @author xtgu
 */
public interface CashAccountLogRepository extends JpaRepository<CashAccountLogEntity,Long> {

    /**
     * 根据 业务流水号 和业务类型查询
     * @param businessNo
     * @param repay
     * @return
     */
    CashAccountLogEntity findByBusinessNoAndBusinessType(String businessNo, BusinessTypeEnum repay);
}
