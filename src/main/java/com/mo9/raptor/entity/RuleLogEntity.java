package com.mo9.raptor.entity;

import javax.persistence.*;

@Entity
@Table(name = "t_raptor_rule_log")
public class RuleLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 状态
     */
    @Column(name = "`rule_name`")
    private String ruleName;
    /**
     * 备注
     */
    @Column(name = "`remark`")
    private String remark;

    @Column(name = "`uid`")
    private String uid;


    @Column(name = "`create_time`")
    private Long createTime;

    /**
     * 规则是否回调
     */
    @Column(name = "`call`")
    private Boolean call;

    /**
     * 规则是否通过
     */
    @Column(name = "`hit`")
    private Boolean hit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
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

    public Boolean getCall() {
        return call;
    }

    public void setCall(Boolean call) {
        this.call = call;
    }

    public Boolean getHit() {
        return hit;
    }

    public void setHit(Boolean hit) {
        this.hit = hit;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
