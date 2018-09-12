package com.mo9.raptor.engine.entity;

import com.mo9.raptor.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractStateEntity extends BaseEntity implements IStateEntity {

    /** 状态 */
    @Column
    private String status;

    /** 一般用于记录需要进行解释的一些变更，使用时请添加到尾部 */
    @Column
    private String description;

    @Override
    public String getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}
