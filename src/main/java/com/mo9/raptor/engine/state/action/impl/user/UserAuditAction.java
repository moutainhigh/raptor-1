package com.mo9.raptor.engine.state.action.impl.user;

import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.risk.service.RiskAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by gqwu on 2018/4/4.
 * 用户审核，并发送审核结果响应事件
 */
public class UserAuditAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(UserAuditAction.class);

    private String userCode;

    private IEventLauncher userEventLauncher;

    private RiskAuditService riskAuditService;

    public UserAuditAction(String userCode, IEventLauncher userEventLauncher, RiskAuditService riskAuditService) {
        this.userCode = userCode;
        this.userEventLauncher = userEventLauncher;
        this.riskAuditService = riskAuditService;
    }

    @Override
    public void run() {
        /** 发送审核结果 */
        try {
            logger.info("开始审核" + userCode);
            AuditResponseEvent event = riskAuditService.audit(this.userCode);
            if (event == null) {
                logger.info("发送审核结果返回结果为null，方法结束userCode={}", userCode);
                return;
            }
            userEventLauncher.launch(event);
            logger.info("审核" + userCode + "成功,结果:" + event);
        } catch (Exception e) {
            logger.error("审核失败", e);
        }
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
