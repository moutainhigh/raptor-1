package com.mo9.raptor.service;

import com.mo9.raptor.entity.CashAccountEntity;
import com.mo9.raptor.enums.ResCodeEnum;

import java.math.BigDecimal;

/**
 * Created by xtgu on 2018/11/1.
 * @author xtgu
 * 现金钱包service
 */
public interface CashAccountService {

    /**
     * 线上还款
     * @param userCode
     * @param amount
     * @return
     */
    ResCodeEnum repay(String userCode , BigDecimal amount , String businessNo);

    /**
     * 线下还款
     * @param userCode
     * @param amount
     * @return
     */
    ResCodeEnum underLine(String userCode , BigDecimal amount , String businessNo);

    /**
     * 入账
     * @param userCode
     * @param amount
     * @return
     */
    ResCodeEnum entry(String userCode , BigDecimal amount , String businessNo);

    /**
     * 根据用户id查询
     * @param userCode
     * @return
     */
    CashAccountEntity findByUserCode(String userCode);

}
