package com.mo9.raptor.bean.res;

/**
 * Created by xzhang on 2018/9/13.
 */
public class PayOderChannelRes extends ChannelDetailRes {

    /**
     * 结果, html, json. link等
     */
    private String result;

    /**
     * 结果, 成功. 失败
     */
    private Boolean state;

    /**
     * 失败描述
     */
    private String message;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
