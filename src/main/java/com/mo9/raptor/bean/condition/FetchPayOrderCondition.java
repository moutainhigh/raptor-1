package com.mo9.raptor.bean.condition;


import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.enums.PayTypeEnum;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xzhang on 2018/7/17.
 */
public class FetchPayOrderCondition {

    /**
     * 一个类型可能包含多个实际类型, 在后台做转换
     */
    private List<Type> types;

    /**
     * 一个状态可能包含多个实际状态, 在后台做转换
     */
    private List<Status> states;

    /**
     * 起始时间
     */
    private Long fromTime;

    /**
     * 到期时间
     */
    private Long toTime;

    /**
     * 借款订单号
     */
    private String loanOrderNumber;

    /**
     * 还款订单号
     */
    private List<String> repaymentOrderNumber;

    private String userCode;

    @NotNull(message = "pageNumber不能为空")
    private Integer pageNumber;

    @NotNull(message = "pageSize不能为空")
    private Integer pageSize;

    public List<Type> getTypes() {
        return types;
    }

    public void setTypes(List<Type> types) {
        this.types = types;
    }

    public List<Status> getStates() {
        return states;
    }

    public void setStates(List<Status> states) {
        this.states = states;
    }

    public Long getFromTime() {
        return fromTime;
    }

    public void setFromTime(Long fromTime) {
        this.fromTime = fromTime;
    }

    public Long getToTime() {
        return toTime;
    }

    public void setToTime(Long toTime) {
        this.toTime = toTime;
    }

    public String getLoanOrderNumber() {
        return loanOrderNumber;
    }

    public void setLoanOrderNumber(String loanOrderNumber) {
        this.loanOrderNumber = loanOrderNumber;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<String> getRepaymentOrderNumber() {
        return repaymentOrderNumber;
    }

    public void setRepaymentOrderNumber(List<String> repaymentOrderNumber) {
        this.repaymentOrderNumber = repaymentOrderNumber;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public List<PayTypeEnum> getPayTypes() {
        if (this.types != null && this.types.size() > 0) {
            List<PayTypeEnum> payTypes = new ArrayList<PayTypeEnum>();
            for (Type type : this.types) {
                if (Type.REPAY_IN_ADVANCE.equals(type)) {
                    payTypes.add(PayTypeEnum.REPAY_IN_ADVANCE);
                }
                if (Type.REPAY_AS_PLAN.equals(type)) {
                    payTypes.add(PayTypeEnum.REPAY_AS_PLAN);
                }
                if (Type.REPAY_OVERDUE.equals(type)) {
                    payTypes.add(PayTypeEnum.REPAY_OVERDUE);
                }
                if (Type.REPAY_POSTPONE.equals(type)) {
                    payTypes.add(PayTypeEnum.REPAY_POSTPONE);
                }
                if (Type.DEDUCT.equals(type)) {
                    payTypes.add(PayTypeEnum.DEDUCT);
                }
            }
            return payTypes;
        } else {
            return null;
        }
    }

    public List<StatusEnum> getPayState() {
        if (this.states != null && this.states.size() > 0) {
            List<StatusEnum> statusEnums = new ArrayList<StatusEnum>();
            for (FetchPayOrderCondition.Status status : states) {
                if (status.equals(FetchPayOrderCondition.Status.PENDING)) {
                    statusEnums.add(StatusEnum.PENDING);
                }
                if (status.equals(FetchPayOrderCondition.Status.AUDITING)) {
                    statusEnums.add(StatusEnum.AUDITING);
                }
                if (status.equals(FetchPayOrderCondition.Status.PASSED)) {
                    statusEnums.add(StatusEnum.PASSED);
                }
                if (status.equals(FetchPayOrderCondition.Status.REJECTED)) {
                    statusEnums.add(StatusEnum.REJECTED);
                }
                if (status.equals(FetchPayOrderCondition.Status.EXPIRED)) {
                    statusEnums.add(StatusEnum.EXPIRED);
                }
                if (status.equals(FetchPayOrderCondition.Status.CANCELLED)) {
                    statusEnums.add(StatusEnum.CANCELLED);
                }
                if (status.equals(FetchPayOrderCondition.Status.DEDUCTING)) {
                    statusEnums.add(StatusEnum.DEDUCTING);
                }
                if (status.equals(FetchPayOrderCondition.Status.DEDUCTED)) {
                    statusEnums.add(StatusEnum.DEDUCTED);
                }
                if (status.equals(FetchPayOrderCondition.Status.DEDUCT_FAILED)) {
                    statusEnums.add(StatusEnum.DEDUCT_FAILED);
                }
                if (status.equals(FetchPayOrderCondition.Status.ENTRY_DOING)) {
                    statusEnums.add(StatusEnum.ENTRY_DOING);
                }
                if (status.equals(FetchPayOrderCondition.Status.ENTRY_DONE)) {
                    statusEnums.add(StatusEnum.ENTRY_DONE);
                }
                if (status.equals(FetchPayOrderCondition.Status.ENTRY_FAILED)) {
                    statusEnums.add(StatusEnum.ENTRY_FAILED);
                }
            }
            return statusEnums;
        } else {
            return null;
        }
    }

    public enum Type {
        LIQUIDATE_BY_OVERDUE(),
        LIQUIDATE_BY_WAVE(),
        LIQUIDATE_BY_WAVE_AND_BROKE(),


        REPAY_IN_ADVANCE(),
        REPAY_AS_PLAN(),
        REPAY_OVERDUE(),

        ORDER_REPAY_IN_ADVANCE(),

        DEDUCT(), REPAY_POSTPONE,
    }

    public enum Status {
        PENDING,
        AUDITING,
        PASSED,
        REJECTED,
        EXPIRED,
        CANCELLED,
        FAILED,
        DEDUCTING,
        DEDUCTED,
        DEDUCT_FAILED,
        ENTRY_DOING,
        ENTRY_DONE,
        ENTRY_FAILED,
        ;
    }
}
