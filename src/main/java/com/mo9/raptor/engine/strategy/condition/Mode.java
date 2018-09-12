package com.mo9.raptor.engine.strategy.condition;

public enum Mode {

    SCOPE("域"),
    VALUE("值"),
    ;

    private String explanation;

    public String getExplanation() {
        return explanation;
    }

    Mode(String explanation){
        this.explanation = explanation;
    }
}
