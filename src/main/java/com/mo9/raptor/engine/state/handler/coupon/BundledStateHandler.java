package com.mo9.raptor.engine.state.handler.coupon;

import com.mo9.raptor.engine.entity.CouponEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.coupon.CouponEntryResponseEvent;

import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;

import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component("bundledState")
@StateHandler(name = StatusEnum.BUNDLED)
public class BundledStateHandler implements IStateHandler<CouponEntity> {

    @Override
    public CouponEntity handle(CouponEntity coupon, IEvent event, IActionExecutor actionExecutor) throws Exception {

        if (event instanceof CouponEntryResponseEvent) {
            CouponEntryResponseEvent entryResponseEvent = (CouponEntryResponseEvent) event;
            coupon.setEntryAmount(coupon.getEntryAmount().add(entryResponseEvent.getActualEntry()));
            coupon.setPayOrderId(entryResponseEvent.getPayOrderId());
            /**
             * 无论入账了多少钱, 都是一次性使用完
             */
            coupon.setStatus(StatusEnum.ENTRY_DONE.name());
            coupon.setEndTime(entryResponseEvent.getEventTime());
            coupon.setDescription(coupon.getDescription() + ";" + event.getEventTime() + ":" + StatusEnum.valueOf(coupon.getStatus()).getExplanation());
        }  else {
            throw new InvalidEventException("优惠券状态与事件类型不匹配，状态：" + coupon.getStatus() + "，事件：" + event);
        }
        return coupon;
    }
}
