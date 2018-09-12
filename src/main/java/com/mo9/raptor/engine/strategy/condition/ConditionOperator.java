package com.mo9.raptor.engine.strategy.condition;

public enum ConditionOperator {

    EQUAL( "等于"),
    NOT_EQUAL( "不等于"),
    ;

    private String explanation;

    public String getExplanation() {
        return explanation;
    }

    ConditionOperator(String explanation){
        this.explanation = explanation;
    }
}
