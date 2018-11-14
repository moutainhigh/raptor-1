package com.mo9.raptor.entity;

import javax.persistence.*;

/**
 * @author wtwei .
 * @date 2018/10/8 .
 * @time 14:39 .
 * 电话黄页
 */


@Entity
@Table(name = "t_risk_tel_yellow_page")
public class RiskTelYellowPage {

    @Id
    @Column(name = "format_tel")
    private String formatTel;
    
    @Column(name = "tags_label")
    private String tagsLabel;
    
    @Column(name = "tags_yellow_page")
    private String tagsYellowPage;
    
    @Column(name = "tags_financial")
    private String tagsFinancial;
    
    @Column(name = "tags_label_times")
    private Integer tagsLabelTimes;
    
    @Column(name = "fancha_telloc")
    private String fanchaTelloc;


    @Column(name = "update_time")
    private Long updateTime = System.currentTimeMillis();

    @Column(name = "create_time")
    private Long createTime;

    

    public String getFormatTel() {
        return formatTel;
    }

    public void setFormatTel(String formatTel) {
        this.formatTel = formatTel;
    }

    public String getTagsLabel() {
        return tagsLabel;
    }

    public void setTagsLabel(String tagsLabel) {
        this.tagsLabel = tagsLabel;
    }

    public String getTagsYellowPage() {
        return tagsYellowPage;
    }

    public void setTagsYellowPage(String tagsYellowPage) {
        this.tagsYellowPage = tagsYellowPage;
    }

    public String getTagsFinancial() {
        return tagsFinancial;
    }

    public void setTagsFinancial(String tagsFinancial) {
        this.tagsFinancial = tagsFinancial;
    }

    public Integer getTagsLabelTimes() {
        return tagsLabelTimes;
    }

    public void setTagsLabelTimes(Integer tagsLabelTimes) {
        this.tagsLabelTimes = tagsLabelTimes;
    }

    public String getFanchaTelloc() {
        return fanchaTelloc;
    }

    public void setFanchaTelloc(String fanchaTelloc) {
        this.fanchaTelloc = fanchaTelloc;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
