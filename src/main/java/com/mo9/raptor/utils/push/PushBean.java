package com.mo9.raptor.utils.push;

/**
 * Created by jyou on 2018/10/11.
 *
 * @author jyou
 */
public class PushBean {

    /**
     * 推送标识，为userCode
     */
    private String pushNo;

    /**
     * 推送渠道
     */
    private String channel = "UMENG";

    /**
     * 客户端
     */
    private String client = "TTYQ";

    /**
     * 必填，与title相同即可
     */
    private String ticker;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String text;


    public PushBean(String pushNo, String title, String text) {
        this.pushNo = pushNo;
        this.ticker = title;
        this.title = title;
        this.text = text;
    }

    public String getPushNo() {
        return pushNo;
    }

    public void setPushNo(String pushNo) {
        this.pushNo = pushNo;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
