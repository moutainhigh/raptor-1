package com.mo9.raptor.bean.req;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Created by xzhang on 2018/9/13.
 */
public class CashRenewalReq extends CashRepayReq {

    /**
     * 延期天数
     */
    @Min(7)
    @Max(14)
    private Integer period;

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }
}
