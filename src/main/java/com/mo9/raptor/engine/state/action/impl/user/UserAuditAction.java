package com.mo9.raptor.engine.state.action.impl.user;

import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.engine.state.event.IEvent;
import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by gqwu on 2018/4/4.
 * 用户审核，并发送审核结果响应事件
 */
public class UserAuditAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(UserAuditAction.class);

    private String userCode;

    public UserAuditAction(String userCode) {
        this.userCode = userCode;
    }

    @Override
    public void run() {
        /** TODO:调用用户审核 */
    }

    @Override
    public String getActionType() {
        return this.getClass().getName();
    }

    @Override
    public String getOrderId() {
        return userCode;
    }

}
