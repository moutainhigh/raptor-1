package com.mo9.raptor.engine.structure;

import com.mo9.raptor.engine.exception.InvalidSchemeFieldException;
import com.mo9.raptor.engine.exception.MergeException;
import com.mo9.raptor.engine.strategy.weight.Weight;
import com.mo9.raptor.engine.strategy.weight.WeightMode;
import com.mo9.raptor.engine.structure.field.*;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.engine.structure.item.ItemTypeEnum;
import com.mo9.raptor.engine.utils.EngineStaticValue;
import com.mo9.raptor.engine.utils.TimeUtils;
import com.mo9.raptor.enums.PayTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Scheme extends HashMap<Integer, Item>  {

    private static final Logger logger = LoggerFactory.getLogger(Scheme.class);

    /** 还款宽限期 */
    private Integer repayGraceDays;

    /** 罚息边界日期 */
    private Long penaltyBoundDate;

    /** 罚息溢缴款 */
    private BigDecimal penaltyOverpay;

    public Integer getRepayGraceDays() {
        return repayGraceDays;
    }

    public void setRepayGraceDays(Integer repayGraceDays) {
        this.repayGraceDays = repayGraceDays;
    }

    public Long getPenaltyBoundDate() {
        return penaltyBoundDate;
    }

    public void setPenaltyBoundDate(Long penaltyBoundDate) {
        this.penaltyBoundDate = penaltyBoundDate;
    }

    public BigDecimal getPenaltyOverpay() {
        return penaltyOverpay;
    }

    public void setPenaltyOverpay(BigDecimal penaltyOverpay) {
        this.penaltyOverpay = penaltyOverpay;
    }
}
