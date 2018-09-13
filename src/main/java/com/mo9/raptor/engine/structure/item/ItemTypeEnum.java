package com.mo9.raptor.engine.structure.item;

public enum ItemTypeEnum {

    PREVIOUS(0, "往期"),
    PERIOD(1, "当期"),
    PREPAY(2, "提前还款项"),
    ALL(3, "所有"),

    FUTURE(-1, "未到期分期"),
    ;

    /** 枚举顺序，标识基本的支付顺序，提前还款项除外，用于二维数组运算 */
    private int sequence;

    private String explanation;

    ItemTypeEnum(int sequence, String explanation){
        this.sequence = sequence;
        this.explanation = explanation;
    }

    public int getSequence() {
        return sequence;
    }

    public String getExplanation() {
        return explanation;
    }
}
