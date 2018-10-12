package com.mo9.raptor.engine.state.action.impl.lend;

import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.utils.GatewayUtils;
import com.mo9.raptor.utils.push.PushBean;
import com.mo9.raptor.utils.push.PushUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xzhang on 2018/10/11
 * 放款成功通知Action
 */
public class LendSuccessNoticeAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(LendSuccessNoticeAction.class);

    private final String orderId;

    private final String userCode;

    private PushUtils pushUtils;

    public LendSuccessNoticeAction(String orderId, String userCode, PushUtils pushUtils) {
        this.orderId = orderId;
        this.userCode = userCode;
        this.pushUtils = pushUtils;
    }

    @Override
    public void run() {
        PushBean push = new PushBean(userCode, "放款成功", "放款成功, 记得按时还款哦~");
        // 推送
        pushUtils.push(push);
    }

    @Override
    public String getActionType() {
        return this.getClass().getName();
    }

    @Override
    public String getOrderId() {
        return orderId;
    }
}
