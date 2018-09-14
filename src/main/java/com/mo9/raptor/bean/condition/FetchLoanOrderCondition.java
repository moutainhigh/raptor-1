package com.mo9.raptor.bean.condition;

import com.mo9.raptor.engine.enums.StatusEnum;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xzhang on 2018/7/17.
 */
public class FetchLoanOrderCondition {

    /**
     * 一个类型可能包含多个实际类型, 在后台做转换
     */
    private List<Status> state;

    /**
     * 起始时间
     */
    private Long fromTime;

    /**
     * 到期时间
     */
    private Long toTime;

    /**
     * 用户
     */
    private String userCode;

    @NotNull(message = "pageNumber不能为空")
    private Integer pageNumber;

    @NotNull(message = "pageSize不能为空")
    private Integer pageSize;

    public List<Status> getState() {
        return state;
    }

    public void setState(List<Status> state) {
        this.state = state;
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

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public List<StatusEnum> getLoanOrderState() {
        if (this.state != null && this.state.size() > 0) {
            List<StatusEnum> statusEnums = new ArrayList<StatusEnum>();
            for (Status status : state) {
                if (status.equals(Status.UNPAID)) {
                    statusEnums.addAll(StatusEnum.BEFORE_LENDING);
                }
                if (status.equals(Status.PENDING)) {
                    statusEnums.add(StatusEnum.PENDING);
                }
                if (status.equals(Status.AUDITING)) {
                    statusEnums.add(StatusEnum.AUDITING);
                }
                if (status.equals(Status.PASSED)) {
                    statusEnums.add(StatusEnum.PASSED);
                }
                if (status.equals(Status.REJECTED)) {
                    statusEnums.add(StatusEnum.REJECTED);
                }
                if (status.equals(Status.EXPIRED)) {
                    statusEnums.add(StatusEnum.EXPIRED);
                }
                if (status.equals(Status.LENDING)) {
                    statusEnums.add(StatusEnum.LENDING);
                }
                if (status.equals(Status.LENT)) {
                    statusEnums.add(StatusEnum.LENT);
                }
                if (status.equals(Status.FAILED)) {
                    statusEnums.add(StatusEnum.FAILED);
                }
                if (status.equals(Status.PAYOFF)) {
                    statusEnums.add(StatusEnum.PAYOFF);
                }
            }
            return statusEnums;
        } else {
            return null;
        }
    }

    public enum Status {
        UNPAID,
        PENDING,
        AUDITING,
        PASSED,
        REJECTED,
        EXPIRED,
        CANCELLED,
        LENDING,
        LENT,
        FAILED,
        PAYOFF,
        ;
    }
}
