package com.mo9.raptor.engine.state.handler.coupon;

import com.mo9.raptor.engine.entity.CouponEntity;
import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.service.CouponService;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.state.action.impl.loan.LoanResponseAction;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.lend.LendResponseEvent;
import com.mo9.raptor.engine.state.event.impl.pay.EntryLaunchEvent;
import com.mo9.raptor.engine.state.event.impl.pay.EntryResponseEvent;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandler;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component("bundledState")
@StateHandler(name = StatusEnum.BUNDLED)
public class BundledStateHandler implements IStateHandler<CouponEntity> {

    @Override
    public CouponEntity handle(CouponEntity coupon, IEvent event, IActionExecutor actionExecutor) throws Exception {

        if (event instanceof EntryResponseEvent) {
            EntryResponseEvent entryResponseEvent = (EntryResponseEvent) event;
            coupon.setEntryAmount(coupon.getEntryAmount().add(entryResponseEvent.getActualEntry()));
            coupon.setStatus(StatusEnum.ENTRY_DONE.name());
            coupon.setEndTime(entryResponseEvent.getEventTime());
            coupon.setDescription(coupon.getDescription() + ";" + event.getEventTime() + ":" + StatusEnum.valueOf(coupon.getStatus()).getExplanation());
        }  else {
            throw new InvalidEventException("优惠券状态与事件类型不匹配，状态：" + coupon.getStatus() + "，事件：" + event);
        }
        return coupon;
    }
}
