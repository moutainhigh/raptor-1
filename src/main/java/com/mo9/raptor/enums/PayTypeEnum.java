package com.mo9.raptor.enums;

import java.util.Arrays;
import java.util.List;

public enum PayTypeEnum {


    REPAY_IN_ADVANCE("提前还款"),
    REPAY_AS_PLAN("按期还款"),
    REPAY_OVERDUE("逾期还款"),

    REPAY_POSTPONE("延期还款"),

    DEDUCT("减免"),
    ;

    private String explanation;

    public String getExplanation() {
        return explanation;
    }

    PayTypeEnum(String explanation){
        this.explanation = explanation;
    }
}
