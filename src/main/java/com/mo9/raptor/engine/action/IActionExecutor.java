package com.mo9.raptor.engine.action;

/**
 * Created by gqwu on 2018/4/4.
 * 定义一个行为
 */
public interface IActionExecutor {

    void execute();

    void append(IAction action);
}
