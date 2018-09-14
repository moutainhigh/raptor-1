package com.mo9.raptor.engine.state.action;

/**
 * Created by gqwu on 2018/4/4.
 * 定义一个行为
 */
public interface IAction {

    String getActionType();

    String getOrderId();

    void run();
}
