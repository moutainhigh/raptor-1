package com.mo9.raptor.entity;

import javax.persistence.*;

/**
 * Created by jyou on 2018/10/22.
 *
 * @author jyou
 */
@Entity
@Table(name = "shixin")
public class ShixinEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "age")
    private String age;

    @Column(name = "areaName")
    private String areaName;

    @Column(name = "businessEntity")
    private String businessEntity;

    @Column(name = "cardNum")
    private String cardNum;

    @Column(name = "caseCode")
    private String caseCode;

    @Column(name = "changefreq")
    private String changefreq;

    @Column(name = "courtName")
    private String courtName;

    @Column(name = "disruptTypeName")
    private String disruptTypeName;

    @Column(name = "duty")
    private String duty;

    @Column(name = "focusNumber")
    private String focusNumber;

    @Column(name = "gistId")
    private String gistId;

    @Column(name = "gistUnit")
    private String gistUnit;

    @Column(name = "iname")
    private String iname;

    @Column(name = "lastmod")
    private String lastmod;

    @Column(name = "loc")
    private String loc;

    @Column(name = "partyTypeName")
    private String partyTypeName;

    @Column(name = "performance")
    private String performance;

    @Column(name = "performedPart")
    private String performedPart;

    @Column(name = "priority")
    private String priority;

    @Column(name = "publishDate")
    private String publishDate;

    @Column(name = "publishDateStamp")
    private String publishDateStamp;

    @Column(name = "regDate")
    private String regDate;

    @Column(name = "sexy")
    private String sexy;

    @Column(name = "sitelink")
    private String sitelink;

    @Column(name = "type")
    private String type;

    @Column(name = "unperformPart")
    private String unperformPart;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getBusinessEntity() {
        return businessEntity;
    }

    public void setBusinessEntity(String businessEntity) {
        this.businessEntity = businessEntity;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getCaseCode() {
        return caseCode;
    }

    public void setCaseCode(String caseCode) {
        this.caseCode = caseCode;
    }

    public String getChangefreq() {
        return changefreq;
    }

    public void setChangefreq(String changefreq) {
        this.changefreq = changefreq;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public String getDisruptTypeName() {
        return disruptTypeName;
    }

    public void setDisruptTypeName(String disruptTypeName) {
        this.disruptTypeName = disruptTypeName;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getFocusNumber() {
        return focusNumber;
    }

    public void setFocusNumber(String focusNumber) {
        this.focusNumber = focusNumber;
    }

    public String getGistId() {
        return gistId;
    }

    public void setGistId(String gistId) {
        this.gistId = gistId;
    }

    public String getGistUnit() {
        return gistUnit;
    }

    public void setGistUnit(String gistUnit) {
        this.gistUnit = gistUnit;
    }

    public String getIname() {
        return iname;
    }

    public void setIname(String iname) {
        this.iname = iname;
    }

    public String getLastmod() {
        return lastmod;
    }

    public void setLastmod(String lastmod) {
        this.lastmod = lastmod;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getPartyTypeName() {
        return partyTypeName;
    }

    public void setPartyTypeName(String partyTypeName) {
        this.partyTypeName = partyTypeName;
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }

    public String getPerformedPart() {
        return performedPart;
    }

    public void setPerformedPart(String performedPart) {
        this.performedPart = performedPart;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getPublishDateStamp() {
        return publishDateStamp;
    }

    public void setPublishDateStamp(String publishDateStamp) {
        this.publishDateStamp = publishDateStamp;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getSexy() {
        return sexy;
    }

    public void setSexy(String sexy) {
        this.sexy = sexy;
    }

    public String getSitelink() {
        return sitelink;
    }

    public void setSitelink(String sitelink) {
        this.sitelink = sitelink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnperformPart() {
        return unperformPart;
    }

    public void setUnperformPart(String unperformPart) {
        this.unperformPart = unperformPart;
    }
}

