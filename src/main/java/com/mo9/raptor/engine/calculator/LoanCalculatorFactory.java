package com.mo9.raptor.engine.calculator;

import com.mo9.raptor.engine.calculator.loan.SingleLoanCalculator;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("loanCalculatorFactory")
public class LoanCalculatorFactory {

    @Autowired
    private SingleLoanCalculator singleLoanCalculator;

    public ILoanCalculator load(LoanOrderEntity loanOrder) {

        return singleLoanCalculator;
    }

}
