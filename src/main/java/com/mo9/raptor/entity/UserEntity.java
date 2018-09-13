package com.mo9.raptor.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author zma
 * @date 2018/9/12
 */
@Entity
@Table(name = "t_raptor_user")
public class UserEntity {





    @Column(name = "create_time")
    private Long createTime;

    @Column(name = "update_time")
    private Long updateTime;

    @Column(name = "deleted")
    private Boolean deleted = false;
}
