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

    public LendResponseEvent(String entityUniqueId, boolean succeeded, BigDecimal actualLent, String lendSignature, String channelOrderId, String channelResponse, long successTime, String explanation) {
        super(entityUniqueId);
        this.succeeded = succeeded;
        this.actualLent = actualLent;
        this.lendSignature = lendSignature;
        this.channelOrderId = channelOrderId;
        this.channelResponse = channelResponse;
        this.successTime = successTime;
        this.explanation = explanation;
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
}
