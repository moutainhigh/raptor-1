package com.mo9.raptor.engine.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.engine.calculator.ILoanCalculator;
import com.mo9.raptor.engine.entity.CouponEntity;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.service.BillService;
import com.mo9.raptor.engine.service.CouponService;
import com.mo9.raptor.engine.structure.Unit;
import com.mo9.raptor.engine.structure.field.DestinationTypeEnum;
import com.mo9.raptor.engine.structure.field.Field;
import com.mo9.raptor.engine.structure.field.FieldTypeEnum;
import com.mo9.raptor.engine.structure.field.SourceTypeEnum;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.enums.PayTypeEnum;
import com.mo9.raptor.exception.LoanEntryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by xzhang on 2018/9/28.
 */
@Service("billServiceImpl")
public class BillServiceImpl implements BillService {

    @Autowired
    private ILoanCalculator loanCalculator;

    @Autowired
    private CouponService couponService;


    @Override
    public Item loanOrderRealItem(LoanOrderEntity loanOrder, PayTypeEnum payType, Integer postponeDays) {
        return loanCalculator.realItem(System.currentTimeMillis(), loanOrder, payType.name(), postponeDays);
    }

    @Override
    public Item orderShouldPayItem(LoanOrderEntity loanOrder, PayTypeEnum payType, Integer postponeDays) {
        Item realItem = this.loanOrderRealItem(loanOrder, payType, postponeDays);
        CouponEntity couponEntity = couponService.getEffectiveBundledCoupon(loanOrder.getOrderId());
        BigDecimal applyAmount = BigDecimal.ZERO;
        if (couponEntity != null && couponEntity.getApplyAmount() != null) {
            applyAmount = couponEntity.getApplyAmount();
        }
        Item entryItem = new Item();
        loanCalculator.entryItem(System.currentTimeMillis(), payType.name(), applyAmount, entryItem, realItem);
        return realItem;
    }

    @Override
    public Item entryItem(PayTypeEnum payType, PayOrderEntity payOrder, LoanOrderEntity loanOrder) throws LoanEntryException {
        // 入账的Item
        Item entryItem = new Item();
        String ownerId = loanOrder.getOwnerId();

        Item orderRealItem = this.loanOrderRealItem(loanOrder, payType, payOrder.getPostponeDays());
        BigDecimal shouldPay = orderRealItem.sum();

        CouponEntity couponEntity = couponService.getEffectiveBundledCoupon(loanOrder.getOrderId());
        BigDecimal applyAmount = BigDecimal.ZERO;
        if (couponEntity != null && couponEntity.getApplyAmount() != null) {
            applyAmount = couponEntity.getApplyAmount();
            entryItem = loanCalculator.entryItem(System.currentTimeMillis(), payType.name(), couponEntity.getApplyAmount(), entryItem, orderRealItem);
            for (Map.Entry<FieldTypeEnum, Unit> entry : entryItem.entrySet()) {
                Unit unit = entry.getValue();
                for (Field field : unit) {
                    // 补充数据
                    field.setDestinationId(loanOrder.getOrderId());
                    field.setDestinationType(DestinationTypeEnum.LOAN_ORDER);
                    field.setSourceId(couponEntity.getCouponId());
                    field.setSourceType(SourceTypeEnum.COUPON);
                }
            }
        }

        entryItem = loanCalculator.entryItem(System.currentTimeMillis(), payType.name(), payOrder.getPayNumber(), entryItem, orderRealItem);
        for (Map.Entry<FieldTypeEnum, Unit> entry : entryItem.entrySet()) {
            Unit unit = entry.getValue();
            for (Field field : unit) {
                if (field.getSourceType() == null) {
                    field.setDestinationId(loanOrder.getOrderId());
                    field.setDestinationType(DestinationTypeEnum.LOAN_ORDER);
                    field.setSourceId(payOrder.getOrderId());
                    field.setSourceType(SourceTypeEnum.PAY_ORDER);
                }
            }
        }
        if (applyAmount.add(payOrder.getPayNumber()).compareTo(shouldPay) != 0) {
            throw new LoanEntryException("订单" + payType.getExplanation() + payOrder.getPayNumber() + ", 优惠" + couponEntity.getApplyAmount() + ", 与应还: " + shouldPay + "不匹配!");
        }
        return entryItem;
    }

    @Override
    public LoanOrderEntity itemEntry(LoanOrderEntity loanOrder, PayTypeEnum payType, Integer days, Item realItem, Item entryItem) throws LoanEntryException {
        return loanCalculator.itemEntry(loanOrder, payType.name(), days, realItem, entryItem);
    }

    @Override
    public List<JSONObject> getRenewInfo(LoanOrderEntity loanOrderEntity) {
        return loanCalculator.getRenew(loanOrderEntity);
    }
}
