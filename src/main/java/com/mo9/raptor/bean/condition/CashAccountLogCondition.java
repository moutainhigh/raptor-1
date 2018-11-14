package com.mo9.raptor.bean.condition;

import com.mo9.raptor.enums.BusinessTypeEnum;

import java.util.Date;
import java.util.List;

/**
 * Created by xtgu on 2018/11/11.
 */
public class CashAccountLogCondition {

    /**
     * 用户标识
     */
    private String userCode ;

    /**
     * 开始时间
     */
    private Date fromDate ;

    /**
     * 结束时间
     */
    private Date toDate ;

    /**
     * 每页条数
     */
    private Integer pageSize ;

    /**
     * 页数
     */
    private Integer pageNumber ;

    /**
     * 充值类型
     */
    private List<BusinessTypeEnum> inType ;

    /**
     * 出账类型
     */
    private List<BusinessTypeEnum> outType ;


    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public List<BusinessTypeEnum> getInType() {
        return inType;
    }

    public void setInType(List<BusinessTypeEnum> inType) {
        this.inType = inType;
    }

    public List<BusinessTypeEnum> getOutType() {
        return outType;
    }

    public void setOutType(List<BusinessTypeEnum> outType) {
        this.outType = outType;
    }
}
