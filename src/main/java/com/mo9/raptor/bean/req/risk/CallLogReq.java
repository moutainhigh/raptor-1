package com.mo9.raptor.bean.req.risk;

import java.util.List;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 15:40 .
 */

/**
 * 第三方通话记录回调数据结构
 */
public class CallLogReq{
    private Integer status;
    
    private String msg;
    
    private CallLogData data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public CallLogData getData() {
        return data;
    }

    public void setData(CallLogData data) {
        this.data = data;
    }
}







