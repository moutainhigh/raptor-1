package com.mo9.raptor.engine.calculator.loan;

import com.mo9.raptor.engine.calculator.AbstractLoanCalculator;
import com.mo9.raptor.engine.calculator.ILoanCalculator;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.utils.EngineStaticValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DailyLoanCalculator extends AbstractLoanCalculator implements ILoanCalculator {

    private static final Logger logger = LoggerFactory.getLogger(DailyLoanCalculator.class);

    /** 按日计息的还款日计算方式，n期还款日 = n期账单日 + 账期 - 1天，例：第1期账单日为8月10号，账期为30天，则还款日为9月8日 */
    @Override
    public long repayDay (int installment, LoanOrderEntity loanOrder) {
        return super.repayDay(installment, loanOrder) - EngineStaticValue.DAY_MILLIS;
    }
}
