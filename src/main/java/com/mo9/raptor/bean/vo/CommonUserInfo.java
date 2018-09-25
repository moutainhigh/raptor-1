package com.mo9.raptor.bean.vo;

/**
 * Created by xtgu on 2018/9/25.
 * 系统定时器查询使用用户信息类
 * @author xtgu
 */
public class CommonUserInfo {
    /**
     * 用户总数
     */
    private Integer userNumber ;
    /**
     * 今日登陆用户数
     */
    private Integer userLoginNumber ;
    /**
     * 身份证认证总数
     */
    private Integer userCardNumber ;
    /**
     * 通话记录认证总数
     */
    private Integer userPhoneNumber ;
    /**
     * 通讯录认证总数
     */
    private Integer userCallHistoryNumber ;
    /**
     * 银行卡认证总数
     */
    private Integer userBankNumber ;

    public Integer getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(Integer userNumber) {
        this.userNumber = userNumber;
    }

    public Integer getUserLoginNumber() {
        return userLoginNumber;
    }

    public void setUserLoginNumber(Integer userLoginNumber) {
        this.userLoginNumber = userLoginNumber;
    }

    public Integer getUserCardNumber() {
        return userCardNumber;
    }

    public void setUserCardNumber(Integer userCardNumber) {
        this.userCardNumber = userCardNumber;
    }

    public Integer getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(Integer userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public Integer getUserCallHistoryNumber() {
        return userCallHistoryNumber;
    }

    public void setUserCallHistoryNumber(Integer userCallHistoryNumber) {
        this.userCallHistoryNumber = userCallHistoryNumber;
    }

    public Integer getUserBankNumber() {
        return userBankNumber;
    }

    public void setUserBankNumber(Integer userBankNumber) {
        this.userBankNumber = userBankNumber;
    }
}
