package com.mo9.raptor.enums;

/** 借贷方式 */
public enum LoanTypeEnum {

    MORTGAGE("抵押贷款"),
    CREDIT("信用贷款"),
    CONSUME("信用消费"),
    ;

    private String explanation;

    LoanTypeEnum(String explanation){
        this.explanation = explanation;
    }

    public static LoanTypeEnum getByName (String name) {

        return null;
    }

    public String getExplanation() {
        return explanation;
    }
}
