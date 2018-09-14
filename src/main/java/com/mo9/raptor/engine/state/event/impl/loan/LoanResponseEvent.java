package com.mo9.raptor.engine.state.event.impl.loan;

import com.mo9.raptor.engine.state.event.AbstractStateEvent;
import com.mo9.raptor.engine.state.event.IStateEvent;

import java.math.BigDecimal;

/**
 * Created by gqwu on 2018/4/4.
 */
public class LoanResponseEvent extends AbstractStateEvent implements IStateEvent {

    private final boolean succeeded;

    private final BigDecimal actualLent;

    private final String lendSignature;

    private final long successTime;

    private final String explanation;

    public LoanResponseEvent(String loanOrderId, BigDecimal actualLent,
                             boolean succeeded, long successTime, String lendSignature, String explanation) {
        super(loanOrderId);
        this.actualLent = actualLent;
        this.succeeded = succeeded;
        this.successTime = successTime;
        this.explanation = explanation;
        this.lendSignature = lendSignature;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public long getSuccessTime() {
        return successTime;
    }

    public String getExplanation() {
        return explanation;
    }

    public BigDecimal getActualLent() {
        return actualLent;
    }

    public String getLendSignature() {
        return lendSignature;
    }
}
