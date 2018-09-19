package com.mo9.raptor.bean.req.risk;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
    
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
    
    public String toString(){
        return com.alibaba.fastjson.JSON.toJSONString(this);
    }
}







