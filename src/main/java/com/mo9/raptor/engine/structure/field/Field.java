package com.mo9.raptor.engine.structure.field;


import com.mo9.raptor.engine.strategy.weight.Weight;

import java.math.BigDecimal;

/** 款项 */
public class Field {

    /** 款项类型 */
    private FieldTypeEnum fieldType;

    /** 入账源 */
    private SourceTypeEnum sourceType;

    /** 入账源ID */
    private String sourceId;

    /** 入账目标 */
    private DestinationTypeEnum destinationType;

    /** 入账目标ID */
    private String destinationId;

    /** 入账数目 */
    private BigDecimal number = BigDecimal.ZERO;

    /** 当支付金额为null时，以足额支付方式计算 */
    public FieldEntryMap pay (String payOrderId, BigDecimal payNumber, Weight strategyWeight, Weight couponWeight) {

        FieldEntryMap fieldEntryMap = new FieldEntryMap();
        if (this.number.compareTo(BigDecimal.ZERO) <= 0) {
            return fieldEntryMap;
        }

        /** 实际支付 */
        BigDecimal payValue;

        /** 支付金额入账到订单部分 */
        BigDecimal payLoan;
        /** 支付金额入账到策略部分 */
        BigDecimal payStrategy;
        /** 优惠金额入账到策略部分 */
        BigDecimal couponLoan;
        /** 优惠金额入账到策略部分 */
        BigDecimal couponStrategy;

        /** 通过应还金额（this.number）反向计算出加权金额 */
        BigDecimal strategyValue = strategyWeight.inverse(this.number);
        /** 计算优惠足额加权金额（策略加权后） */
        BigDecimal couponValue = couponWeight.forward(this.number.add(strategyValue));
        /** 加权优惠后应支付总金额 */
        BigDecimal shouldPay = this.number.add(strategyValue).subtract(couponValue);

        /** 当全额优惠该款项时，等价于由优惠完成了对订单和策略的支付 */
        if (shouldPay.compareTo(BigDecimal.ZERO) <= 0) {

            Field couponLoanField = this.clone();
            couponLoanField.sourceType = SourceTypeEnum.COUPON;
            couponLoanField.sourceId = couponWeight.getId();
            couponLoanField.number = this.number;

            Field couponStrategyField = this.clone();
            couponStrategyField.sourceType = SourceTypeEnum.COUPON;
            couponStrategyField.sourceId = couponWeight.getId();
            couponStrategyField.destinationType = DestinationTypeEnum.PAY_STRATEGY;
            couponStrategyField.destinationId = strategyWeight.getId();
            couponStrategyField.number = strategyValue;

            fieldEntryMap.put(EntryEnum.COUPON_LOAN, couponLoanField);
            fieldEntryMap.put(EntryEnum.COUPON_STRATEGY, couponStrategyField);

            return fieldEntryMap;
        }

        /** 当payNumber为null时，按足额支付进行计算 */
        if (payNumber != null && payNumber.compareTo(shouldPay) < 0) {
            payValue = payNumber;
        } else {
            payValue = shouldPay;
        }

        /** 根据支付金额，反向计算优惠权重，再计算策略权重 */
        couponValue = couponWeight.inverse(payValue);
        payStrategy = strategyWeight.forward(payValue);
        payLoan = payValue.subtract(payStrategy);
        couponStrategy = strategyWeight.forward(couponValue);
        couponLoan = couponValue.subtract(couponStrategy);

        Field payLoanField = this.clone();
        payLoanField.sourceType = SourceTypeEnum.PAY_ORDER;
        payLoanField.sourceId = payOrderId;
        payLoanField.number = payLoan;
        fieldEntryMap.put(EntryEnum.PAY_LOAN, payLoanField);

        Field couponLoanField = this.clone();
        couponLoanField.sourceType = SourceTypeEnum.COUPON;
        couponLoanField.sourceId = couponWeight.getId();
        couponLoanField.number = couponLoan;
        fieldEntryMap.put(EntryEnum.COUPON_LOAN, couponLoanField);

        Field payStrategyField = this.clone();
        payStrategyField.sourceType = SourceTypeEnum.PAY_ORDER;
        payStrategyField.sourceId = payOrderId;
        payStrategyField.destinationType = DestinationTypeEnum.PAY_STRATEGY;
        payStrategyField.destinationId = strategyWeight.getId();
        payStrategyField.number = payStrategy;
        fieldEntryMap.put(EntryEnum.PAY_STRATEGY, payStrategyField);

        Field couponStrategyField = this.clone();
        couponStrategyField.sourceType = SourceTypeEnum.COUPON;
        couponStrategyField.sourceId = couponWeight.getId();
        couponStrategyField.destinationType = DestinationTypeEnum.PAY_STRATEGY;
        couponStrategyField.destinationId = strategyWeight.getId();
        couponStrategyField.number = couponStrategy;
        fieldEntryMap.put(EntryEnum.COUPON_STRATEGY, couponStrategyField);

        return fieldEntryMap;
    }

    public Field clone () {

        Field field = new Field();

        field.fieldType = this.fieldType;
        field.sourceId = this.sourceId;
        field.sourceType = this.sourceType;
        field.destinationId = this.destinationId;
        field.destinationType = this.destinationType;
        field.number = this.number;

        return field;
    }

    public Field opposite () {
        Field clone = this.clone();
        clone.number = clone.number.multiply(new BigDecimal(-1));
        return clone;
    }

    public Field add (Field field) {

        Field clone = this.clone();

        if (field != null) {
            clone.number = clone.number
                    .add(field.number);
        }

        return clone;
    }

    public Field subtract (Field field) {

        Field clone = this.clone();

        if (field != null) {
            clone.number = clone.number
                    .subtract(field.number);
        }

        return clone;
    }

    public SourceTypeEnum getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceTypeEnum sourceType) {
        this.sourceType = sourceType;
    }

    public DestinationTypeEnum getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(DestinationTypeEnum destinationType) {
        this.destinationType = destinationType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public BigDecimal getNumber() {
        return number;
    }

    public void setNumber(BigDecimal number) {
        this.number = number;
    }

    public FieldTypeEnum getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldTypeEnum fieldType) {
        this.fieldType = fieldType;
    }
}
