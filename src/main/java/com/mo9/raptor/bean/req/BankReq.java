package com.mo9.raptor.bean.req;

import javax.validation.constraints.NotBlank;

/**
 * Created by xtgu on 2018/9/13.
 * @author xtgu
 */
public class BankReq {
    @NotBlank(message = "持卡人姓名不能为空")
    private String cardName;
    /**
     * 银行名称
     */
    private String bankName;
    @NotBlank(message = "预留手机号不能为空")
    private String cardMobile;
    @NotBlank(message = "卡号不能为空")
    private String card;

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

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
}
