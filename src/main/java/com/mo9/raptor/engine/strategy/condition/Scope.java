package com.mo9.raptor.engine.strategy.condition;

public enum Scope {

    PAY_CURRENCY( "支付币种"),
    LOAN_CURRENCY( "借贷币种"),
    ;

    private String explanation;

    public String getExplanation() {
        return explanation;
    }

    Scope(String explanation){
        this.explanation = explanation;
    }

    public static Scope instance (String name) {
        return Scope.valueOf(name);
    }
}
