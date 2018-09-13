package com.mo9.raptor.engine.enums;

public enum NumberMode {

    PERCENT("百分比"),
    QUANTITY("固定量"),
    ;

    private String explanation;

    public String getExplanation() {
        return explanation;
    }

    NumberMode(String explanation){
        this.explanation = explanation;
    }
}
