package com.mo9.raptor.engine.event.impl.entry;

import com.mo9.raptor.engine.event.AbstractEvent;
import com.mo9.raptor.engine.event.IEvent;
import com.mo9.raptor.enums.PayTypeEnum;

/**
 * Created by gqwu on 2018/4/4.
 * 支付入账事件
 */
public class EntryEvent extends AbstractEvent implements IEvent {

    /** 用户ID */
    private final String userCode;

    /** 支付批次号 */
    private final String batchId;

    private final PayTypeEnum payType;

    private final boolean isBroke;

    public EntryEvent(String userCode, String batchId, PayTypeEnum payType, boolean isBroke) {
        this.batchId = batchId;
        this.userCode = userCode;
        this.payType = payType;
        this.isBroke = isBroke;
    }

    public String getBatchId() {
        return batchId;
    }

    public String getUserCode() {
        return userCode;
    }

    public PayTypeEnum getPayType() {
        return payType;
    }

    public boolean isBroke() {
        return isBroke;
    }
}
