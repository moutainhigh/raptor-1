package com.mo9.raptor.entity;


import javax.persistence.*;

/**
 *
 * @author zma
 * @date 2018/7/13
 */
@Entity
@Table(name = "t_raptor_message_notify")
public class MessageNotifyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 发送类型
     */
    @Column(name = "type")
    private String type;

    /**
     * 事件
     */
    @Column(name = "business_code")
    private String businessCode;

    /**
     * 状态
     */
    @Column(name = "status")
    private String status;

    /**
     * 发送时间
     */
    @Column(name = "send_time")
    private Long sendTime;

    /**
     * 发送内容
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 收件人
     */
    @Column(name = "receivers")
    private String receivers;

    /**
     * 发送凭证
     */
    @Column(name = "message_id")
    private String messageId;

    /**
     * 语言
     */
    @Column(name = "language")
    private String language;

    /**
     * 信息模板
     */
    @Column(name = "template_code")
    private String templateCode;

    @Column(name = "create_time")
    private Long createTime;

    @Column(name = "update_time")
    private Long updateTime;

    public enum MessageSendStatus {
        SUCCESS("成功"),
        FAILED("失败"),
        WAIT("等待发送");

        MessageSendStatus(String name) {
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getReceivers() {
        return receivers;
    }

    public void setReceivers(String receivers) {
        this.receivers = receivers;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}
