package com.mo9.raptor.engine.event.impl.order;

import com.mo9.raptor.engine.event.AbstractStateEvent;
import com.mo9.raptor.engine.event.IStateEvent;

/**
 * Created by gqwu on 2018/4/4.
 * 发起审核
 */
public class AuditLaunchEvent extends AbstractStateEvent implements IStateEvent {

    private final String userCode;

    public AuditLaunchEvent(String userCode, String orderId) {
        super(orderId);
        this.userCode = userCode;
    }

    public String getUserCode() {
        return userCode;
    }
}
