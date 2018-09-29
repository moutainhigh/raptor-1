package com.mo9.raptor.engine.calculator;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.exception.MergeException;
import com.mo9.raptor.engine.exception.UnSupportTimeDiffException;
import com.mo9.raptor.engine.structure.Scheme;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.exception.LoanEntryException;

import java.math.BigDecimal;
import java.util.List;

public interface ILoanCalculator {

    /** 最少应还 */
    BigDecimal minRepay (LoanOrderEntity loanOrder);

    /**
     * 计算订单原始分期明细
     * @param loanOrder
     * @return
     */
    Item originItem(LoanOrderEntity loanOrder);

    /**
     * 实时账单明细，包括提前还款明细
     * @param date
     * @param loanOrder
     * @return
     */
    Item realItem(Long date, LoanOrderEntity loanOrder, String payType);

    /**
     * 获得入账item
     * @param date       日期
     * @param paid       还款金额
     * @param loanOrder  借款订单
     * @return
     * @throws UnSupportTimeDiffException
     * @throws MergeException
     */
    Item entryItem(Long date, String payType, BigDecimal paid, LoanOrderEntity loanOrder) throws LoanEntryException;

    /**
     * 检查是否是合法的还款金额
     * @param date
     * @param paid
     * @param loanOrder
     * @return
     */
    Boolean checkValidRepayAmount(Long date, String payType, BigDecimal paid, LoanOrderEntity loanOrder) throws LoanEntryException;

    /**
     * 根据入账明细，完成入账处理，返回处理后的订单
     * @param loanOrder
     * @param realItem
     * @param days               延期天数
     * @param entryItem
     * @return
     */
    LoanOrderEntity itemEntry(LoanOrderEntity loanOrder, String payType, Integer days, Item realItem, Item entryItem) throws LoanEntryException;

    /**
     * 延期还款列表
     * @param loanOrder
     * @return
     */
    List<JSONObject> getRenew (LoanOrderEntity loanOrder);
}
