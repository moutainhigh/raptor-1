package com.mo9.raptor.bean.res;

/**
 * 银行账户信息
 * @author zma
 * @date 2018/9/17
 */
public class AccountBankCardRes {
    /**
     * 持卡人姓名
     */
    private String cardName;
    /**
     * 卡号
     */
    private String card;
    /**
     * 预留手机号
     */
    private String cardMobile;
    /**
     *开户行名称
     */
    private String bankName;

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getCardMobile() {
        return cardMobile;
    }

    public void setCardMobile(String cardMobile) {
        this.cardMobile = cardMobile;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
