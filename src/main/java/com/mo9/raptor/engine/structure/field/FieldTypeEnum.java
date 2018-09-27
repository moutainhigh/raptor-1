package com.mo9.raptor.engine.structure.field;

/**
 * 款项类型
 */
public enum FieldTypeEnum {

    PENALTY(0, "罚息"),
    ALL_CHARGE(1, "延期服务费"),
    INTEREST(2, "利息"),
    PRINCIPAL(3, "本金"),
    CUT_CHARGE(4, "砍头息"),
    ALL(5, "所有"),

    /** 以下为服务费的细分枚举类型 */
    EXCHANGE_CHARGE(-1, "兑换服务费"),
    ADDRESS_FEE(-2, "地址生成费"),
    GAS_FEE(-3, "矿工费"),
    ;

    /** 枚举顺序，标识基本的支付顺序，提前还款项除外，用于二维数组运算 */
    private int sequence;

    private String description;

    FieldTypeEnum(int sequence, String description){
        this.sequence = sequence;
        this.description = description;
    }

    public int getSequence() {
        return sequence;
    }

    public String getDescription() {
        return description;
    }
}
