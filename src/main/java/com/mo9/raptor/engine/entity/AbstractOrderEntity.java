package com.mo9.raptor.engine.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractOrderEntity extends AbstractStateEntity implements IStateEntity {

    /** 订单业务流水号 */
    @Column(name = "order_id")
    private String orderId;

    /** 订单所有者 */
    @Column(name = "owner_id")
    private String ownerId;

    /** 订单类型 */
    @Column(name = "type")
    private String type;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
