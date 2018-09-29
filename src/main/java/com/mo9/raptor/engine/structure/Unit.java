package com.mo9.raptor.engine.structure;

import com.mo9.raptor.engine.structure.field.Field;
import com.mo9.raptor.engine.structure.field.FieldTypeEnum;
import com.mo9.raptor.engine.structure.item.Item;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by xzhang on 2018/9/28.
 */
public class Unit extends ArrayList<Field> {

    /** 款项类型 */
    private FieldTypeEnum fieldType;

    @Override
    public Unit clone() {
        Unit unit = new Unit();
        for (Field field : this) {
            unit.add(field.clone());
        }
        return unit;
    }

    public Unit opposite () {
        Unit unit = new Unit();
        for (Field field : this) {
            unit.add(field.opposite());
        }
        return unit;
    }

    public BigDecimal sum() {
        BigDecimal sum = BigDecimal.ZERO;
        for (Field field : this) {
            sum = sum.add(field.getNumber());
        }
        return sum;
    }


    public FieldTypeEnum getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldTypeEnum fieldType) {
        this.fieldType = fieldType;
    }

    public Unit() {
    }

    public Unit(FieldTypeEnum fieldType) {
        this.fieldType = fieldType;
    }
}
