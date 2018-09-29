package com.mo9.raptor.engine.service;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.enums.PayTypeEnum;
import com.mo9.raptor.exception.LoanEntryException;

import java.math.BigDecimal;
import java.util.List;

/**
 * 做入账处理
 * Created by xzhang on 2018/9/28.
 */
public interface BillService {

    /**
     * 订单的实际应还
     * @param loanOrder
     * @return
     */
    Item loanOrderRealItem (LoanOrderEntity loanOrder, PayTypeEnum payType, Integer postponeDays);

    /**
     * 订单的实际应还 - 减免
     * @param loanOrder
     * @return
     */
    Item orderShouldPayItem (LoanOrderEntity loanOrder, PayTypeEnum payType, Integer postponeDays);

    /**
     * 获得入账Item
     * @param payType
     * @param loanOrder
     * @return
     */
    Item entryItem (PayTypeEnum payType, PayOrderEntity payOrder, LoanOrderEntity loanOrder) throws LoanEntryException;

    /**
     * 入账处理
     * @param loanOrder
     * @param payType
     * @param days
     * @param realItem
     * @param entryItem
     * @return
     */
    LoanOrderEntity itemEntry(LoanOrderEntity loanOrder, PayTypeEnum payType, Integer days, Item realItem, Item entryItem) throws LoanEntryException;

    /**
     * 获取延期信息
     * @param loanOrderEntity
     * @return
     */
    List<JSONObject> getRenewInfo(LoanOrderEntity loanOrderEntity);
}
