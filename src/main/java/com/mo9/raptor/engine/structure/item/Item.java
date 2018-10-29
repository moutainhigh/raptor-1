package com.mo9.raptor.engine.structure.item;

import com.mo9.raptor.engine.structure.Unit;
import com.mo9.raptor.engine.structure.field.Field;
import com.mo9.raptor.engine.structure.field.FieldTypeEnum;
import com.mo9.raptor.engine.structure.field.SourceTypeEnum;
import com.mo9.raptor.enums.PayTypeEnum;

import java.math.BigDecimal;
import java.util.HashMap;


public class Item extends HashMap<FieldTypeEnum, Unit> {

    /** 期数 */
    private Integer sequence;

    /** 类型 */
    private ItemTypeEnum itemType;

    /** 还款日 */
    private Long repayDate;

    /**
     * 延期天数
     */
    private Integer postponeDays;


    @Override
    public Item clone () {

        Item item = new Item();

        item.sequence = this.sequence;
        item.itemType = this.itemType;
        item.repayDate = this.repayDate;

        for (Unit unit : this.values()) {
            item.put(unit.getFieldType(), unit.clone());
        }
        return item;
    }

    public Item opposite () {
        Item clone = this.clone();
        for (Unit unit: this.values()) {
            clone.put(unit.getFieldType(), unit.opposite());
        }
        return clone;
    }

    public Item add (Item item) {
        Item clone = this.clone();

        if (item == null || item.size() == 0) {
            return clone;
        }

        for (Unit unit: item.values()) {
            Unit cloneUnit = clone.get(unit.getFieldType());
            if (cloneUnit == null) {
                clone.put(unit.getFieldType(), unit.clone());
            } else {
                cloneUnit.addAll(unit);
                clone.put(unit.getFieldType(), cloneUnit);
            }
        }

        return clone;
    }

    public BigDecimal sum () {
        BigDecimal sum = BigDecimal.ZERO;
        for (Unit unit: this.values()) {
            sum = sum.add(unit.sum());
        }
        return sum;
    }

    public BigDecimal sum (SourceTypeEnum sourceType) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Unit unit: this.values()) {
            for (Field field : unit) {
                if (field.getSourceType().equals(sourceType)) {
                    sum = sum.add(field.getNumber());
                }
            }
        }
        return sum;
    }


    public BigDecimal getFieldNumber(FieldTypeEnum fieldType) {
        Unit unit = this.get(fieldType);
        if (unit == null) {
            return BigDecimal.ZERO;
        }
        return unit.sum();
    }

    /**
     * 获取当前订单或当前还款的还款类型
     * @return  还款类型
     */
    public PayTypeEnum getRepaymentType () {
        PayTypeEnum payTypeEnum = PayTypeEnum.REPAY_IN_ADVANCE;
        if (ItemTypeEnum.PREVIOUS.equals(this.getItemType())) {
            payTypeEnum = PayTypeEnum.REPAY_OVERDUE;
        }
        if (ItemTypeEnum.PERIOD.equals(this.getItemType())) {
            payTypeEnum = PayTypeEnum.REPAY_AS_PLAN;
        }
        return payTypeEnum;
    }


    public ItemTypeEnum getItemType() {
        return itemType;
    }

    public void setItemType(ItemTypeEnum itemType) {
        this.itemType = itemType;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Long getRepayDate() {
        return repayDate;
    }

    public void setRepayDate(Long repayDate) {
        this.repayDate = repayDate;
    }

    public Integer getPostponeDays() {
        return postponeDays;
    }

    public void setPostponeDays(Integer postponeDays) {
        this.postponeDays = postponeDays;
    }
}
