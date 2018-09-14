package com.mo9.raptor.engine.enums;

/**
 * Created by gqwu on 2018/3/23.
 * 审核类型
 */
public enum LendModeEnum {
    AUTO("自动放款"),
    MANUAL("手动放款"),
    ;

    private String explanation;

    LendModeEnum(String explanation){
        this.explanation = explanation;
    }

    public String getExplanation() {
        return explanation;
    }

}
