package com.mo9.raptor.engine.strategy.weight;

public enum WeightMode {

    PERCENT("百分比"),
    QUANTITY("固定量"),
    ;

    private String explanation;

    public String getExplanation() {
        return explanation;
    }

    WeightMode(String explanation){
        this.explanation = explanation;
    }
}
