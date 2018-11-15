package com.mo9.raptor.bean.req;

/**
 * Created by xtgu on 2018/11/15.
 * @author xtgu
 */
public class BankSeekerReq {

    /**
     *银行卡号
     */
    private String bankCardNo;
    /**
     *银行预留手机号
     */
    private String bankMobile;
    /**
     *开户行
     */
    private String bankOfDeposit;
    /**
     *身份证号
     */
    private String idCard;
    /**
     * 真实姓名
     */
    private String realName	;

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }

    public String getBankMobile() {
        return bankMobile;
    }

    public void setBankMobile(String bankMobile) {
        this.bankMobile = bankMobile;
    }

    public String getBankOfDeposit() {
        return bankOfDeposit;
    }

    public void setBankOfDeposit(String bankOfDeposit) {
        this.bankOfDeposit = bankOfDeposit;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
