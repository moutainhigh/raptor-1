package com.mo9.raptor.engine.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 订单表 Created by gqwu on 2018/7/6.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_raptor_lend_order")
public class LendOrderEntity extends AbstractOrderEntity {

    /** 请求唯一标识 */
    @Column(name = "apply_unique_code")
    private String applyUniqueCode;

    /** 请求放款数目 */
    @Column(name = "apply_number")
    private BigDecimal applyNumber;

    /** 请求放款时间 */
    @Column(name = "apply_time")
    private Long applyTime;

    /** 姓名 */
    @Column(name = "user_name")
    private String userName;

    /** 身份证 */
    @Column(name = "id_card")
    private String idCard;

    /** 银行名称 */
    @Column(name = "bank_name")
    private String bankName;

    /** 银行卡号 */
    @Column(name = "bank_card")
    private String bankCard;

    /** 银行预留电话 */
    @Column(name = "bank_mobile")
    private String bankMobile;

    /** 渠道 */
    @Column(name = "channel")
    private String channel;

    /** 渠道订单ID */
    @Column(name = "channel_order_id")
    private String channelOrderId;

    /** 渠道放款数目 */
    @Column(name = "channel_lend_number")
    private BigDecimal channelLendNumber;

    /** 渠道响应 */
    @Column(name = "channel_response")
    private String channelResponse;

    /** 渠道响应时间 */
    @Column(name = "channel_response_time")
    private Long chanelResponseTime;

    public String getApplyUniqueCode() {
        return applyUniqueCode;
    }

    public void setApplyUniqueCode(String applyUniqueCode) {
        this.applyUniqueCode = applyUniqueCode;
    }

    public BigDecimal getApplyNumber() {
        return applyNumber;
    }

    public void setApplyNumber(BigDecimal applyNumber) {
        this.applyNumber = applyNumber;
    }

    public Long getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Long applyTime) {
        this.applyTime = applyTime;
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

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
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

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannelOrderId() {
        return channelOrderId;
    }

    public void setChannelOrderId(String channelOrderId) {
        this.channelOrderId = channelOrderId;
    }

    public BigDecimal getChannelLendNumber() {
        return channelLendNumber;
    }

    public void setChannelLendNumber(BigDecimal channelLendNumber) {
        this.channelLendNumber = channelLendNumber;
    }

    public String getChannelResponse() {
        return channelResponse;
    }

    public void setChannelResponse(String channelResponse) {
        this.channelResponse = channelResponse;
    }

    public Long getChanelResponseTime() {
        return chanelResponseTime;
    }

    public void setChanelResponseTime(Long chanelResponseTime) {
        this.chanelResponseTime = chanelResponseTime;
    }
}
