package com.mo9.raptor.bean.res;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.exception.BaseException;

import java.util.List;

public class LoanOrderRes {

    private String orderId;

    private String actuallyGet;

    private String repayAmount;

    private Long repayTime;

    private String state;

    private String abateAmount;

    private String receiveBankCard;

    private List<JSONObject> renew;

    private String agreementUrl;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRepayAmount() {
        return repayAmount;
    }

    public void setRepayAmount(String repayAmount) {
        this.repayAmount = repayAmount;
    }

    public Long getRepayTime() {
        return repayTime;
    }

    public void setRepayTime(Long repayTime) {
        this.repayTime = repayTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAbateAmount() {
        return abateAmount;
    }

    public void setAbateAmount(String abateAmount) {
        this.abateAmount = abateAmount;
    }

    public String getReceiveBankCard() {
        return receiveBankCard;
    }

    public void setReceiveBankCard(String receiveBankCard) {
        this.receiveBankCard = receiveBankCard;
    }

    public List<JSONObject> getRenew() {
        return renew;
    }

    public void setRenew(List<JSONObject> renew) {
        this.renew = renew;
    }

    public String getActuallyGet() {
        return actuallyGet;
    }

    public void setActuallyGet(String actuallyGet) {
        this.actuallyGet = actuallyGet;
    }

    public String getAgreementUrl() {
        return agreementUrl;
    }

    public void setAgreementUrl(String agreementUrl) {
        this.agreementUrl = agreementUrl;
    }


    public enum StateEnum {
        PENDING(1, "排队中"),
        AUDITING(2, "审核中"),
        PASSED(9, "审核通过"),
        REJECTED(6, "已拒绝"),
        CANCELLED(5, "已取消"),
        LENDING(3, "放款中"),
        LENT(4, "已放款"),
        FAILED(7, "放款失败"),
        PAYOFF(8, "已还清"),

        ;

        private Integer code;

        private String desc;

        StateEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public  static Integer getCode (String status) throws BaseException {
            for (StateEnum stateEnum : StateEnum.values()) {
                if (stateEnum.name().equals(status)) {
                    return stateEnum.code;
                }
            }
            throw new BaseException("不合法的状态" + status);
        }
    }

}
