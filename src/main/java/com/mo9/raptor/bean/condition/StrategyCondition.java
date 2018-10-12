package com.mo9.raptor.bean.condition;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by jyou on 2018/10/6.
 *
 * @author jyou
 */
public class StrategyCondition {

    /**
     * 银行卡是否支持策略条件,银行卡名称key
     */
    public static final String BANK_NAME_CONDITION = "BANK_NAME_CONDITION";

    public static final String[] SUPPORT_BANK = {"中国银行", "工商银行", "建设银行", "交通银行", "储蓄银行", "广发银行", "兴业银行", "中信银行", "广大银行","浦发银行","广州银行","平安银行"};

    /**
     * 银行卡是否支持策略开关
     */
    private boolean loanBankSupport = false;

    /**
     * 策略条件，支持多种策略同时传入条件
     */
    private JSONObject condition;

    public StrategyCondition(boolean loanBankSupport) {
        this.loanBankSupport = loanBankSupport;
    }

    public boolean isLoanBankSupport() {
        return loanBankSupport;
    }

    public void setLoanBankSupport(boolean loanBankSupport) {
        this.loanBankSupport = loanBankSupport;
    }

    public JSONObject getCondition() {
        return condition;
    }

    public void setCondition(JSONObject condition) {
        this.condition = condition;
    }
}
