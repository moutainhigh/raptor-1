package com.mo9.raptor.engine.structure.item;

import com.mo9.raptor.engine.exception.MergeException;
import com.mo9.raptor.engine.structure.field.DestinationTypeEnum;
import com.mo9.raptor.engine.structure.field.Field;
import com.mo9.raptor.engine.structure.field.FieldTypeEnum;
import com.mo9.raptor.engine.utils.EngineStaticValue;
import com.mo9.raptor.engine.utils.TimeUtils;
import com.mo9.raptor.enums.PayTypeEnum;

import java.math.BigDecimal;
import java.util.HashMap;


public class Item extends HashMap<FieldTypeEnum, Field> {

    /** 期数 */
    private Integer sequence;

    /** 类型 */
    private ItemTypeEnum itemType;

    /** 还款日 */
    private Long repayDate;


    @Override
    public Item clone () {

        Item item = new Item();

        item.sequence = this.sequence;
        item.itemType = this.itemType;
        item.repayDate = this.repayDate;

        for (Field field: this.values()) {
            item.put(field.getFieldType(), field.clone());
        }

        return item;
    }

    public Item opposite () {
        Item clone = this.clone();
        for (Field field: this.values()) {
            clone.put(field.getFieldType(), field.opposite());
        }
        return clone;
    }

    public Item add (Item item) {
        Item clone = this.clone();

        if (item == null || item.size() == 0) {
            return clone;
        }

        for (Field field: item.values()) {
            Field cloneField = clone.get(field.getFieldType());
            if (cloneField == null) {
                clone.put(field.getFieldType(), field.clone());
            } else {
                clone.put(field.getFieldType(), cloneField.add(field));
            }
        }

        return clone;
    }

    public Item subtract (Item item) {
        Item clone = this.clone();

        if (item == null || item.size() == 0) {
            return clone;
        }

        for (Field field: item.values()) {
            Field cloneField = clone.get(field.getFieldType());
            if (cloneField == null) {
                clone.put(field.getFieldType(), field.opposite());
            } else {
                clone.put(field.getFieldType(), cloneField.subtract(field));
            }
        }

        return clone;
    }

    public Item merge (Item item) throws MergeException {

        Item clone = this.clone();

        if (item == null || item.size() == 0) {
            return clone;
        }

        if (item.sequence != null && clone.sequence != null && !item.sequence.equals(clone.sequence)) {
            throw new MergeException("Item合并运算不合法！sequence变量冲突：" + item.sequence + "-" + clone.sequence);
        }
        if (item.itemType != null && clone.itemType != null && !item.itemType.equals(clone.itemType)) {
            throw new MergeException("Item合并运算不合法！itemType变量冲突：" + item.itemType + "-" + clone.itemType);
        }
        if (item.repayDate != null && clone.repayDate != null && !item.repayDate.equals(clone.repayDate)) {
            throw new MergeException("Item合并运算不合法！repayDate变量冲突：" + item.repayDate + "-" + clone.repayDate);
        }

        if (item.sequence != null) {
            clone.sequence = item.sequence;
        }
        if (item.itemType != null) {
            clone.itemType = item.itemType;
        }
        if (item.repayDate != null) {
            clone.repayDate = item.repayDate;
        }

        for (Field field: item.values()) {
            Field cloneField = clone.get(field.getFieldType());
            if (cloneField == null) {
                clone.put(field.getFieldType(), field.clone());
            } else {
                throw new MergeException("Item合并运算不合法，不能有同类型Field存在！冲突Field类型：" + field.getFieldType());
            }
        }

        return clone;
    }

    public BigDecimal sum () {
        BigDecimal sum = BigDecimal.ZERO;
        for (Field field: this.values()) {
            sum = sum.add(field.getNumber());
        }
        return sum;
    }

    /** 修正 */
    public BigDecimal revise(FieldTypeEnum fieldType, BigDecimal subtrahend) {

        Field field = this.get(fieldType);

        if (field == null) {
            return subtrahend;
        }

        BigDecimal fieldNumber = field.getNumber();
        if (subtrahend.compareTo(fieldNumber) > 0) {
            subtrahend = subtrahend.subtract(fieldNumber);
            field.setNumber(BigDecimal.ZERO);
        } else {
            field.setNumber(fieldNumber.subtract(subtrahend));
            subtrahend = BigDecimal.ZERO;
        }

        return subtrahend;
    }

    public BigDecimal getFieldNumber(FieldTypeEnum fieldType) {
        Field field = this.get(fieldType);
        if (field == null) {
            return BigDecimal.ZERO;
        }
        return field.getNumber();
    }

    public void setFieldNumber(FieldTypeEnum fieldType, BigDecimal fieldNumber) {
        Field field = this.get(fieldType);
        if (field == null) {
            field = new Field();
            field.setFieldType(fieldType);
            this.put(fieldType, field);
        }
        field.setNumber(fieldNumber);
    }

    public void setDestination(DestinationTypeEnum destination, String destinationId) {
        for (Field field: this.values()) {
            field.setDestinationType(destination);
            field.setDestinationId(destinationId);
        }
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
}
