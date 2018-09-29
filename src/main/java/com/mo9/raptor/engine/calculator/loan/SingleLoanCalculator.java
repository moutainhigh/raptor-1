package com.mo9.raptor.engine.calculator.loan;

import com.mo9.raptor.engine.calculator.AbstractLoanCalculator;
import com.mo9.raptor.engine.calculator.ILoanCalculator;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SingleLoanCalculator extends AbstractLoanCalculator implements ILoanCalculator {

    private static final Logger logger = LoggerFactory.getLogger(SingleLoanCalculator.class);

    @Override
    public BigDecimal minRepay(LoanOrderEntity loanOrder) {

        return loanOrder.getLoanNumber();
    }
}
