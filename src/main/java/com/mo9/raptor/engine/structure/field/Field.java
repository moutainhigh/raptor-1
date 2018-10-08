package com.mo9.raptor.engine.structure.field;

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

    @Override
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
