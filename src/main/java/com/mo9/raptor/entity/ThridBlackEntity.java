package com.mo9.raptor.entity;

import javax.persistence.*;

/**
 * Created by jyou on 2018/10/25.
 *
 * @author jyou
 */
@Entity
@Table(name = "t_raptor_thrid_black")
public class ThridBlackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_code")
    private String userCode;

    @Column(name = "channel")
    private String channel;

    @Column(name = "result")
    private String result;

    @Column(name = "thrid_res")
    private String thridRes;

    @Column(name = "remark")
    private String remark;

    @Column(name = "create_time")
    private Long createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getThridRes() {
        return thridRes;
    }

    public void setThridRes(String thridRes) {
        this.thridRes = thridRes;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
