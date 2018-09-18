package com.mo9.raptor.bean.res;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class LoanOrderRes {

    private String orderId;

    private String actuallyGet;

    private String repayAmount;

    private Long repayTime;

    private String state;

    private String abateAmount;

    private String receiveBankCard;

    private List<JSONObject> renew;

    private String agreementUrl;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRepayAmount() {
        return repayAmount;
    }

    public void setRepayAmount(String repayAmount) {
        this.repayAmount = repayAmount;
    }

    public Long getRepayTime() {
        return repayTime;
    }

    public void setRepayTime(Long repayTime) {
        this.repayTime = repayTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAbateAmount() {
        return abateAmount;
    }

    public void setAbateAmount(String abateAmount) {
        this.abateAmount = abateAmount;
    }

    public String getReceiveBankCard() {
        return receiveBankCard;
    }

    public void setReceiveBankCard(String receiveBankCard) {
        this.receiveBankCard = receiveBankCard;
    }

    public List<JSONObject> getRenew() {
        return renew;
    }

    public void setRenew(List<JSONObject> renew) {
        this.renew = renew;
    }

    public String getActuallyGet() {
        return actuallyGet;
    }

    public void setActuallyGet(String actuallyGet) {
        this.actuallyGet = actuallyGet;
    }

    public String getAgreementUrl() {
        return agreementUrl;
    }

    public void setAgreementUrl(String agreementUrl) {
        this.agreementUrl = agreementUrl;
    }
}
