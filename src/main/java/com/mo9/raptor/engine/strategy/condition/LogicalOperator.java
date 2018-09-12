package com.mo9.raptor.engine.strategy.condition;

public enum LogicalOperator {

    AND( "与"),
    OR("或"),
    ;

    private String explanation;

    public String getExplanation() {
        return explanation;
    }

    LogicalOperator(String explanation){
        this.explanation = explanation;
    }
}
