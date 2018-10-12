package com.mo9.raptor.bean.vo;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.enums.MessageNotifyEventEnum;
import com.mo9.raptor.enums.MessageNotifyTypeEnum;

import java.util.List;

/**
 * @author zma
 * @date 2018/7/11
 */
public class MessageNotifyRequestVo {

    /**
     * 系统名 -- 默认 RAPTOR
     */
    private String systemCode;

    /**
     * 事件类型 -- 自定义
     */
    private MessageNotifyEventEnum businessCode ;
    /**
     * 消息id
     */
    private String messageId ;
    /**
     * 回调url
     */
    private String notifyUrl ;
    /**
     *邮箱短信
     */
    private MessageNotifyTypeEnum transMode ;
    /**
     * 媒体类型 -- 仅支持文字
     */
    private String mediaType = "WORD" ;
    /**
     * 接收地区码（国际区号，语言相关）
     */
    private String receiverArea ;
    /**
     * 发送方
     */
    private List<String> receivers ;
    /**
     * 是否重试 -- 默认0
     */
    private int retry = 0 ;
    /**
     *邮件或短信网关
     */
    private String gatewayCode ;
    /**
     * 模版
     */
    private String templateCode ;
    /**
     * 模版所需变量参数
     */
    private JSONObject variableValues ;

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public MessageNotifyEventEnum getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(MessageNotifyEventEnum businessCode) {
        this.businessCode = businessCode;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public MessageNotifyTypeEnum getTransMode() {
        return transMode;
    }

    public void setTransMode(MessageNotifyTypeEnum transMode) {
        this.transMode = transMode;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getReceiverArea() {
        return receiverArea;
    }

    public void setReceiverArea(String receiverArea) {
        this.receiverArea = receiverArea;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public String getGatewayCode() {
        return gatewayCode;
    }

    public void setGatewayCode(String gatewayCode) {
        this.gatewayCode = gatewayCode;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public JSONObject getVariableValues() {
        return variableValues;
    }

    public void setVariableValues(JSONObject variableValues) {
        this.variableValues = variableValues;
    }
}
