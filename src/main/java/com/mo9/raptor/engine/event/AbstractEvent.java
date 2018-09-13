package com.mo9.raptor.engine.event;

import com.mo9.raptor.utils.IDWorker;

import java.util.Date;

/**
 * Created by gqwu on 2018/4/4.
 */
public abstract class AbstractEvent implements IEvent {

    private final String eventId;

    private final long eventTime;

    public AbstractEvent() {
        this.eventId = IDWorker.getNewID();
        this.eventTime = new Date().getTime();
    }

    @Override
    public String getEventId() {
        return this.eventId;
    }

    @Override
    public long getEventTime() {
        return this.eventTime;
    }
}
