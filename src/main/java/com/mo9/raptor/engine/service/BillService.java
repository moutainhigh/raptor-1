package com.mo9.raptor.engine.service;

import com.mo9.raptor.bean.vo.RenewVo;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.enums.PayTypeEnum;
import com.mo9.raptor.exception.LoanEntryException;
import com.mo9.raptor.exception.NumberModeException;

import java.math.BigDecimal;
import java.util.List;

/**
 * 做入账处理
 * Created by xzhang on 2018/9/28.
 */
public interface BillService {

    /**
     * 延期订单的应还
     * @param loanOrder
     * @return
     */
    Item realItem(LoanOrderEntity loanOrder, PayTypeEnum payType, Integer postponeDays);

    /**
     * 还清订单的应还
     * @param loanOrder
     * @return
     */
    Item payoffRealItem(LoanOrderEntity loanOrder);

    /**
     * 延期订单的实际应还 - 减免
     * @param loanOrder
     * @return
     */
    Item shouldPayItem(LoanOrderEntity loanOrder, PayTypeEnum payType, Integer postponeDays);

    /**
     * 还清订单的实际应还 - 减免
     * @param loanOrder
     * @return
     */
    Item payoffShouldPayItem(LoanOrderEntity loanOrder);

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
    List<RenewVo> getRenewInfo(LoanOrderEntity loanOrderEntity) throws NumberModeException;

    /**
     * 最少应还
     * @param loanOrder
     * @return
     */
    BigDecimal minRepay (LoanOrderEntity loanOrder);
}
