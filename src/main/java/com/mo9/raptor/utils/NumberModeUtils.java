package com.mo9.raptor.utils;

import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.NumberMode;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.utils.EngineStaticValue;
import com.mo9.raptor.engine.utils.TimeUtils;
import com.mo9.raptor.exception.NumberModeException;

import java.math.BigDecimal;

/**
 * 计算部分还款时, 本金利息的应还金额
 * Created by xzhang on 2018/10/15.
 */
public class NumberModeUtils {


    /**
     * 计算应还罚息
     * @param loanOrder
     * @return
     */
    public static BigDecimal getShouldPayPenalty(Long date, LoanOrderEntity loanOrder) throws NumberModeException {
        BigDecimal shouldPayPenalty = BigDecimal.ZERO;
        if (!StatusEnum.LENT.name().equals(loanOrder.getStatus())) {
            return shouldPayPenalty;
        }
        Long repaymentDate = TimeUtils.extractDateTime(loanOrder.getRepaymentDate());
        date = TimeUtils.extractDateTime(date);
        if (date > repaymentDate)  {
            Long lastPaidPrincipalDate = TimeUtils.extractDateTime(loanOrder.getLastPaidPrincipalDate());

            if (lastPaidPrincipalDate < repaymentDate) {
                lastPaidPrincipalDate = repaymentDate;
            }
            Long overDueDate = (date - lastPaidPrincipalDate) / EngineStaticValue.DAY_MILLIS;
            String penaltyMode = loanOrder.getPenaltyMode();
            if (penaltyMode.equals(NumberMode.QUANTITY.name())) {
                shouldPayPenalty = loanOrder.getPenaltyValue().multiply(new BigDecimal(overDueDate)).subtract(loanOrder.getPaidPenalty());
            } else if (penaltyMode.equals(NumberMode.PERCENT.name())) {
                /**
                 * 以1000元一天30元逾期费为比例计算罚息
                 * TODO: 计算罚息基准可能变化, 如果变化, 则入账的地方也需要变化
                 */
                BigDecimal penaltyPercent = loanOrder.getPenaltyValue().divide(loanOrder.getLentNumber().add(loanOrder.getChargeValue()), EngineStaticValue.RESULT_SCALE, BigDecimal.ROUND_UP);
                BigDecimal restPrincipal = loanOrder.getLentNumber().add(loanOrder.getChargeValue()).subtract(loanOrder.getPaidPrincipal()).subtract(loanOrder.getPaidCharge());
                shouldPayPenalty = restPrincipal.multiply(penaltyPercent).multiply(new BigDecimal(overDueDate)).subtract(loanOrder.getPaidPenalty()).setScale(EngineStaticValue.DATABASE_SCALE, BigDecimal.ROUND_UP);
            } else {
                throw new NumberModeException("不支持的NumberMode类型:" + penaltyMode);
            }
        } else {
            shouldPayPenalty = BigDecimal.ZERO;
        }
        return shouldPayPenalty;
    }

    /**
     * 计算应还利息
     * @param loanOrder
     * @return
     */
    public static BigDecimal getShouldPayInterest(Long date, LoanOrderEntity loanOrder) throws NumberModeException {
        BigDecimal shouldPayInterest = BigDecimal.ZERO;
        if (!StatusEnum.LENT.name().equals(loanOrder.getStatus())) {
            return shouldPayInterest;
        }
        // 利息永远是固定的
        return loanOrder.getInterestValue().subtract(loanOrder.getPaidInterest());
    }



}
