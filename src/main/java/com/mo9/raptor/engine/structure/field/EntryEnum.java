package com.mo9.raptor.engine.structure.field;


public enum EntryEnum {

    PAY_LOAN( "支付入账到借贷订单"),
    PAY_STRATEGY( "支付入账到策略"),
    COUPON_LOAN( "优惠入账到借贷订单"),
    COUPON_STRATEGY( "优惠入账到策略"),
    ;

    private String explanation;

    public String getExplanation() {
        return explanation;
    }

    EntryEnum(String explanation){
        this.explanation = explanation;
    }
}
