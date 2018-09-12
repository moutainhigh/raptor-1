package com.mo9.raptor.engine.entity;

import com.mo9.raptor.entity.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 还款订单log, 存储第三方返回用户还款信息
 * Created by xzhang on 2018/9/12.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_raptor_pay_order_log")
public class PayOrderLogEntity extends BaseEntity {

    /**
     * 还款订单号
     */
    @Column(name = "mo9_receipt_id")
    private String payOrderId;

    /**
     * 还款渠道
     */
    @Column(name = "repay_channel")
    private String channel;

    /**
     * 客户端Id
     */
    @Column(name = "origin_client_id")
    private Integer clientId;
    /**
     * 客户端版本号
     */
    @Column(name = "origin_client_version")
    private String clientVersion;

    /**
     * 第三方放款流水号, 比如 mo9
     */
    @Column(name = "receipt_id")
    private String receiptId;

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

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
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
