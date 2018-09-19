package com.mo9.raptor.bean.res;

/**
 * 放款查单返回值
 * Created by xzhang on 2018/9/14.
 */
public class LoanOrderLendRes {

    /**
     * 查询状态
     */
    private String status;

    private String description;

    private String invoice;

    private String dealcode;

    /**
     * 订单状态(  真状态)
     */
    private String orderStatus;

    private String amount;

    private String statusTime;

    private String channel;

    private String thirdDealcode;

    private String failReason;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getDealcode() {
        return dealcode;
    }

    public void setDealcode(String dealcode) {
        this.dealcode = dealcode;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(String statusTime) {
        this.statusTime = statusTime;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getThirdDealcode() {
        return thirdDealcode;
    }

    public void setThirdDealcode(String thirdDealcode) {
        this.thirdDealcode = thirdDealcode;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }
}
