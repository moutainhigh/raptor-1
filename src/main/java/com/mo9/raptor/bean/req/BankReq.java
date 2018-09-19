package com.mo9.raptor.bean.req;

import javax.validation.constraints.NotBlank;

/**
 * Created by xtgu on 2018/9/13.
 * @author xtgu
 */
public class BankReq {
    /**
     * 银行名称
     */
    @NotBlank(message = "银行名称不能为空")
    private String bankName;
    @NotBlank(message = "预留手机号不能为空")
    private String cardMobile;
    @NotBlank(message = "卡号不能为空")
    private String card;
    @NotBlank(message = "银行卡扫描开始计数不能为空")
    private String cardStartCount ;
    @NotBlank(message = "银行卡扫描成功计数不能为空")
    private String cardSuccessCount ;
    @NotBlank(message = "银行卡扫描失败计数不能为空")
    private String cardFailCount ;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardMobile() {
        return cardMobile;
    }

    public void setCardMobile(String cardMobile) {
        this.cardMobile = cardMobile;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getCardStartCount() {
        return cardStartCount;
    }

    public void setCardStartCount(String cardStartCount) {
        this.cardStartCount = cardStartCount;
    }

    public String getCardSuccessCount() {
        return cardSuccessCount;
    }

    public void setCardSuccessCount(String cardSuccessCount) {
        this.cardSuccessCount = cardSuccessCount;
    }

    public String getCardFailCount() {
        return cardFailCount;
    }

    public void setCardFailCount(String cardFailCount) {
        this.cardFailCount = cardFailCount;
    }
}
