package com.mo9.raptor.engine.state.event;

/**
 * Created by gqwu on 2018/4/4.
 */
public interface IEvent {

    String getEventId();

    long getEventTime();

    /**
     * 事件格式化为String
     * @return
     */
    String toString();
}
