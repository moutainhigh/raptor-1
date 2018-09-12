package com.mo9.raptor.engine.entity;

/**
 * Created by gqwu on 2018/4/19.
 */
public interface IStateEntity {
    String getStatus();
    void setStatus(String status);
    String getDescription();
    void setDescription(String description);
}
