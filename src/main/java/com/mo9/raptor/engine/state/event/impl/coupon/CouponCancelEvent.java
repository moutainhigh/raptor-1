package com.mo9.raptor.engine.state.event.impl.coupon;

import com.mo9.raptor.engine.state.event.AbstractStateEvent;
import com.mo9.raptor.engine.state.event.IStateEvent;

import java.math.BigDecimal;

/**
 * 取消优惠券
 * Created by xzhang on 2018/10/29.
 */
public class CouponCancelEvent extends AbstractStateEvent implements IStateEvent {

    private final String operator;

    public CouponCancelEvent(String couponId, String operator) {
        super(couponId);
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
}
