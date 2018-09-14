package com.mo9.raptor.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 还款log
 * Created by xzhang on 2018/9/13.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_raptor_pay_order_log")
public class PayOrderLogEntity extends BaseEntity {

    /**
     * 还的借款订单号
     */
    @Column(name = "order_id")
    private String orderId;

    /**
     * 还款订单号
     */
    @Column(name = "pay_order_id")
    private String payOrderId;

    /**
     * 还款渠道
     */
    @Column(name = "channel")
    private String channel;

    /**
     * 客户端Id
     */
    @Column(name = "client_id")
    private Integer clientId;

    /**
     * 客户端版本号
     */
    @Column(name = "client_version")
    private String clientVersion;

    /**
     * 客户端版本号
     */
    @Column(name = "bank_card")
    private String bankCard;

    /**
     * 客户端版本号
     */
    @Column(name = "bank_mobile")
    private String bankMobile;

    /**
     * 客户端版本号
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 客户端版本号
     */
    @Column(name = "id_card")
    private String idCard;

    /**
     * 第三方放款流水号, 比如 mo9
     */
    @Column(name = "deal_code")
    private String dealCode;

    /**
     * 银行放款流水号
     */
    @Column(name = "third_channel_no")
    private String thirdChannelNo;

    /**
     * 渠道响应结果
     */
    @Column(name = "channel_response")
    private String channelResponse;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPayOrderId() {
        return payOrderId;
    }

    public void setPayOrderId(String payOrderId) {
        this.payOrderId = payOrderId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getBankMobile() {
        return bankMobile;
    }

    public void setBankMobile(String bankMobile) {
        this.bankMobile = bankMobile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getDealCode() {
        return dealCode;
    }

    public void setDealCode(String dealCode) {
        this.dealCode = dealCode;
    }

    public String getThirdChannelNo() {
        return thirdChannelNo;
    }

    public void setThirdChannelNo(String thirdChannelNo) {
        this.thirdChannelNo = thirdChannelNo;
    }

    public String getChannelResponse() {
        return channelResponse;
    }

    public void setChannelResponse(String channelResponse) {
        this.channelResponse = channelResponse;
    }
}
