package com.mo9.raptor.engine.calculator;

import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.exception.MergeException;
import com.mo9.raptor.engine.exception.UnSupportTimeDiffException;
import com.mo9.raptor.engine.structure.Scheme;

public interface ILoanCalculator {

    /** 计算订单原始分期明细 */
    Scheme originScheme(LoanOrderEntity loanOrder);

    /** 计划账单明细，不包含提前还款 */
    Scheme planScheme(Long date, LoanOrderEntity loanOrder);

    /** 实时账单明细，包括提前还款明细 */
    Scheme realScheme(Long date, LoanOrderEntity loanOrder) throws UnSupportTimeDiffException, MergeException;

    /** 根据入账明细，完成入账处理，返回处理后的订单 */
    LoanOrderEntity schemeEntry(LoanOrderEntity loanOrder, Scheme originalScheme, Scheme realScheme, Scheme entryScheme);

    /** 计算指定分期的还款日 */
    long repayDay(int period, LoanOrderEntity loanOrder);
}
