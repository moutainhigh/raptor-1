package com.mo9.raptor.engine.state.event.impl.coupon;

import com.mo9.raptor.engine.state.event.AbstractStateEvent;
import com.mo9.raptor.engine.state.event.IStateEvent;

import java.math.BigDecimal;

/**
 * Created by gqwu on 2018/4/8.
 */
public class CouponEntryResponseEvent extends AbstractStateEvent implements IStateEvent {

    private final BigDecimal actualEntry;

    private final String payOrderId;

    public CouponEntryResponseEvent(String couponId, BigDecimal actualEntry, String payOrderId) {
        super(couponId);
        this.actualEntry = actualEntry;
        this.payOrderId = payOrderId;
    }

    public BigDecimal getActualEntry() {
        return actualEntry;
    }

    public String getPayOrderId() {
        return payOrderId;
    }
}
