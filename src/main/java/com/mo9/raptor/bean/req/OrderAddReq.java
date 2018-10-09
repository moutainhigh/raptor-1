package com.mo9.raptor.bean.req;


import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 还款请求
 * Created by xzhang on 2018/9/13.
 */
public class OrderAddReq {

    /**
     * 本金
     */
    @NotNull
    private BigDecimal capital;

    /**
     * 周期
     */
    private int period;

    /**
     * 放款银行卡
     */
    private String card;

    /**
     * 银行预留手机
     */
    private String cardMobile;

    public BigDecimal getCapital() {
        return capital;
    }

    public void setCapital(BigDecimal capital) {
        this.capital = capital;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
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
}
