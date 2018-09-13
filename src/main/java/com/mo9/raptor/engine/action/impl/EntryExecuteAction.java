package com.mo9.raptor.engine.action.impl;

import com.mo9.raptor.engine.action.IAction;
import com.mo9.raptor.engine.event.impl.entry.EntryEvent;
import com.mo9.raptor.engine.launcher.IEventLauncher;
import com.mo9.raptor.enums.PayTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntryExecuteAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(EntryExecuteAction.class);

    private String userCode;

    private String batchId;

    private PayTypeEnum payType;

    private IEventLauncher userPayEventLauncher;

    public EntryExecuteAction(String userCode, String batchId, PayTypeEnum payType, IEventLauncher userPayEventLauncher) {
        this.userCode = userCode;
        this.batchId = batchId;
        this.payType = payType;
        this.userPayEventLauncher = userPayEventLauncher;
    }

    @Override
    public void run() {
        EntryEvent entryEvent = new EntryEvent(this.userCode, this.batchId, this.payType, false);
        try {
            this.userPayEventLauncher.launch(entryEvent);
        } catch (Exception e) {
            logger.error("用户入账事件处理异常，事件：[{}]", entryEvent, e);
        }

    }

    @Override
    public String getActionType() {
        return this.getClass().getName();
    }

    @Override
    public String getOrderId() {
        //TODO:批次号
        return this.batchId;
    }
}
