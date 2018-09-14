package com.mo9.raptor.engine.state.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by gqwu on 2018/4/5.
 *
 * 避免不同actionExecutor之间相互干扰，
 * 所以，需要使用者新建executor实例，但建议使用同一个线程池进行任务执行
 */

public class ActionExecutorImpl implements IActionExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ActionExecutorImpl.class);

    private List<IAction> actions = new ArrayList<IAction>();

    @Override
    public void execute() {
        Iterator<IAction> iterator = actions.iterator();
        while (iterator.hasNext()) {
            IAction action = iterator.next();
            logger.info("开始执行订单的id为[{}], 类型为[{}]的Action, 当前任务队列长度[{}]", 
                    action.getOrderId(), action.getActionType(), actions.size());
            action.run();
            iterator.remove();
        }
    }

    @Override
    public void append(IAction action) {
        this.actions.add(action);
    }
}
