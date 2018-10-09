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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    
    @Column(name = "format_tel")
    private String formatTel;
    
    @Column(name = "tags_label")
    private String tagsLabel;
    
    @Column(name = "tags_yellow_page")
    private String tagsYellowPage;
    
    @Column(name = "tags_financial")
    private String tagsFinancial;


    @Column(name = "update_time")
    private Long updateTime = System.currentTimeMillis();

    @Column(name = "create_time")
    private Long createTime = System.currentTimeMillis();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
