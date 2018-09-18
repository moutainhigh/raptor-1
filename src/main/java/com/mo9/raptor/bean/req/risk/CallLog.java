package com.mo9.raptor.bean.req.risk;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 16:01 .
 */
public class CallLog {
    String call_cost;
    String call_time;
    String call_method;
    String call_type;
    String call_to;
    String call_from;
    String call_duration;
    String call_tel;

    public String getCall_cost() {
        return call_cost;
    }

    public void setCall_cost(String call_cost) {
        this.call_cost = call_cost;
    }

    public String getCall_time() {
        return call_time;
    }

    public void setCall_time(String call_time) {
        this.call_time = call_time;
    }

    public String getCall_method() {
        return call_method;
    }

    public void setCall_method(String call_method) {
        this.call_method = call_method;
    }

    public String getCall_type() {
        return call_type;
    }

    public void setCall_type(String call_type) {
        this.call_type = call_type;
    }

    public String getCall_to() {
        return call_to;
    }

    public void setCall_to(String call_to) {
        this.call_to = call_to;
    }

    public String getCall_from() {
        return call_from;
    }

    public void setCall_from(String call_from) {
        this.call_from = call_from;
    }

    public String getCall_duration() {
        return call_duration;
    }

    public void setCall_duration(String call_duration) {
        this.call_duration = call_duration;
    }

    public String getCall_tel() {
        return call_tel;
    }

    public void setCall_tel(String call_tel) {
        this.call_tel = call_tel;
    }

    public String toString(){
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
