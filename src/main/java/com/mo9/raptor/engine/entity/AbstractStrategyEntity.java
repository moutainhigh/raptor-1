package com.mo9.raptor.engine.entity;


import com.alibaba.fastjson.JSON;
import com.mo9.raptor.engine.strategy.condition.Condition;
import com.mo9.raptor.engine.strategy.weight.Weight;
import com.mo9.raptor.engine.strategy.weight.WeightMode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;

/**
 * 抽象策略 Created by gqwu on 2018/7/6.
 */
@MappedSuperclass
public abstract class AbstractStrategyEntity extends AbstractStateEntity {

    @Column(name = "strategy_id")
    private String strategyId;

    /** 约束的业务 */
    @Column(name = "business")
    private String business;

    /** 优先级 */
    @Column(name = "priority")
    private Integer priority;

    /** 生效条件 */
    @Column(name = "condition")
    private String condition;

    /** 约束的分期类型 */
    @Column(name = "item_type")
    private String itemType;

    /** 约束的项类型 */
    @Column(name = "field_type")
    private String fieldType;

    /** 权重计算类型-百分比/固定量 */
    @Column(name = "weight_mode")
    private String weightMode;

    /** 权重值 */
    @Column(name = "weight_value")
    private BigDecimal weightValue;

    /** 生效起始日期 */
    @Column(name = "effective_date")
    private Long effectiveDate;

    /** 有效期 */
    @Column(name = "effective_days")
    private int effectiveDays;

    public Weight weight() {
        Weight weight = new Weight();
        weight.setId(strategyId);
        weight.setWeightMode(WeightMode.valueOf(this.weightMode));
        weight.setWeightValue(this.weightValue);

        return weight;
    }

    public Condition getCondition() {
        return JSON.parseObject(this.condition, Condition.class);
    }

    public void setCondition(Condition condition) {
        this.condition = JSON.toJSONString(condition);
    }

    public String getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(String strategyId) {
        this.strategyId = strategyId;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getWeightMode() {
        return weightMode;
    }

    public void setWeightMode(String weightMode) {
        this.weightMode = weightMode;
    }

    public BigDecimal getWeightValue() {
        return weightValue;
    }

    public void setWeightValue(BigDecimal weightValue) {
        this.weightValue = weightValue;
    }

    public Long getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Long effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public int getEffectiveDays() {
        return effectiveDays;
    }

    public void setEffectiveDays(int effectiveDays) {
        this.effectiveDays = effectiveDays;
    }

}
