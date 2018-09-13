package com.mo9.raptor.engine.event;

/**
 * Created by gqwu on 2018/4/4.
 * 状态实体事件接口，会影响某一个实体状态的事件
 */
public interface IStateEvent extends IEvent {
    /**
     * @return 返回该事件所影响的目标实体的唯一标识符，用于对该实体对象的查询和更新操作
     */
    String getEntityUniqueId();
}
