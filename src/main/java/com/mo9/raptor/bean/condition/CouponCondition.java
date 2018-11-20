package com.mo9.raptor.bean.condition;

import com.mo9.raptor.enums.BusinessTypeEnum;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by xtgu on 2018/11/11.
 */
public class CouponCondition {

    /**
     * 用户标识
     */
    private String userCode ;

    /**
     * 结束时间
     */
    private Long expiryDate ;

    /**
     * 每页条数
     */
    private Integer pageSize ;

    /**
     * 页数
     */
    private Integer pageNumber ;

    /**
     * 状态
     */
    private List<String> statusList ;

    /**
     * 优惠券使用限定金额
     */
    private BigDecimal limitAmount;

    /**
     * 优惠券使用类型
     */
    private List<String> useType;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
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

    public List<String> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<String> statusList) {
        this.statusList = statusList;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public List<String> getUseType() {
        return useType;
    }

    public void setUseType(List<String> useType) {
        this.useType = useType;
    }
}
