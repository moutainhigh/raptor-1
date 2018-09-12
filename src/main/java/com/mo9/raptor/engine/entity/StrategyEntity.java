package com.mo9.raptor.engine.entity;


import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 策略表 Created by gqwu on 2018/7/6.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_raptor_strategy")
public class StrategyEntity extends AbstractStrategyEntity {

}
