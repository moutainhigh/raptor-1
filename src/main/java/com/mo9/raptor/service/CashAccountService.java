package com.mo9.raptor.service;

import com.mo9.raptor.bean.condition.CashAccountLogCondition;
import com.mo9.raptor.entity.CashAccountEntity;
import com.mo9.raptor.entity.CashAccountLogEntity;
import com.mo9.raptor.enums.BusinessTypeEnum;
import com.mo9.raptor.enums.ResCodeEnum;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

/**
 * Created by xtgu on 2018/11/1.
 * @author xtgu
 * 现金钱包service
 */
public interface CashAccountService {

    /**
     * 充值
     * @param userCode
     * @param amount
     * @param businessNo
     * @param businessTypeEnum
     * @return
     */
    ResCodeEnum recharge(String userCode , BigDecimal amount , String businessNo, BusinessTypeEnum businessTypeEnum);


    /**
     * 入账
     * @param userCode
     * @param amount
     * @param businessNo
     * @param businessTypeEnum
     * @return
     */
    ResCodeEnum entry(String userCode , BigDecimal amount , String businessNo , BusinessTypeEnum businessTypeEnum);

    /**
     * 根据用户id查询
     * @param userCode
     * @return
     */
    CashAccountEntity findByUserCode(String userCode);

    /**
     * 条件查询日志
     * @param cashAccountLogCondition
     * @return
     */
    Page<CashAccountLogEntity> findLogByCondition(CashAccountLogCondition cashAccountLogCondition);
}
