package com.mo9.raptor.bean.req.risk;

import com.mo9.raptor.risk.entity.TRiskTelBill;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 16:00 .
 */
public class CallLogData {
    private String tel;

    private String sid;

    private String uid;

    private List<CallLog> call_log;

    private List<CallLogBill> bill;

    private TelInfo tel_info;


    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<CallLog> getCall_log() {
        return call_log;
    }

    public void setCall_log(List<CallLog> call_log) {
        this.call_log = call_log;
    }

    public List<CallLogBill> getBill() {
        return bill;
    }

    public void setBill(List<CallLogBill> bill) {
        this.bill = bill;
    }

    public TelInfo getTel_info() {
        return tel_info;
    }

    public void setTel_info(TelInfo tel_info) {
        this.tel_info = tel_info;
    }

    public String toString(){
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
