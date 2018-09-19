package com.mo9.raptor.engine.state.event.impl.lend;

import com.mo9.raptor.engine.state.event.AbstractStateEvent;
import com.mo9.raptor.engine.state.event.IStateEvent;

import java.math.BigDecimal;

/**
 * Created by gqwu on 2018/4/4.
 */
public class LendResponseEvent extends AbstractStateEvent implements IStateEvent {

    private final boolean succeeded;

    private final BigDecimal actualLent;

    private final String lendSignature;

    private final String channelOrderId;

    private final String channelResponse;

    private final long successTime;

    private final String explanation;

    private final String channel;

    private final String failReason;

    /**
     * 放款成功
     */
    public LendResponseEvent(String entityUniqueId, boolean succeeded, BigDecimal actualLent, String lendSignature, String channelOrderId, String channelResponse, long successTime, String explanation, String channel) {
        super(entityUniqueId);
        this.succeeded = succeeded;
        this.actualLent = actualLent;
        this.lendSignature = lendSignature;
        this.channelOrderId = channelOrderId;
        this.channelResponse = channelResponse;
        this.successTime = successTime;
        this.explanation = explanation;
        this.channel = channel;
        this.failReason = null;
    }

    /**
     * 放款失败
     */
    public LendResponseEvent(String entityUniqueId, boolean succeeded, String lendSignature, String channelOrderId, String channelResponse, String explanation, String channel, String failReason) {
        super(entityUniqueId);
        this.succeeded = succeeded;
        this.explanation = explanation;
        this.actualLent = null;
        this.lendSignature = lendSignature;
        this.successTime = -1L;
        this.channel = channel;
        this.failReason = failReason;
        this.channelOrderId = channelOrderId;
        this.channelResponse = channelResponse;

    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public long getSuccessTime() {
        return successTime;
    }

    public String getExplanation() {
        return explanation;
    }

    public BigDecimal getActualLent() {
        return actualLent;
    }

    public String getLendSignature() {
        return lendSignature;
    }

    public String getChannelOrderId() {
        return channelOrderId;
    }

    public String getChannelResponse() {
        return channelResponse;
    }

    public String getChannel() {
        return channel;
    }

    public String getFailReason() {
        return failReason;
    }
}
