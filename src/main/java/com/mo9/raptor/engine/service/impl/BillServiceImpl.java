package com.mo9.raptor.engine.service.impl;

import com.mo9.raptor.bean.vo.RenewVo;
import com.mo9.raptor.engine.calculator.ILoanCalculator;
import com.mo9.raptor.engine.entity.CouponEntity;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
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
import com.mo9.raptor.exception.NumberModeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    public Item realItem(LoanOrderEntity loanOrder, PayTypeEnum payType, Integer postponeDays) throws NumberModeException {
        return loanCalculator.realItem(System.currentTimeMillis(), loanOrder, payType.name(), postponeDays);
    }

    @Override
    public Item payoffRealItem(LoanOrderEntity loanOrder) throws NumberModeException {
        return loanCalculator.realItem(System.currentTimeMillis(), loanOrder, PayTypeEnum.REPAY_AS_PLAN.name(), 0);
    }

    @Override
    public Item shouldPayItem(LoanOrderEntity loanOrder, PayTypeEnum payType, Integer postponeDays) throws NumberModeException {
        Item realItem = this.realItem(loanOrder, payType, postponeDays);
        CouponEntity couponEntity = couponService.getEffectiveBundledCoupon(loanOrder.getOrderId());
        BigDecimal couponAmount = BigDecimal.ZERO;
        if (couponEntity != null && couponEntity.getApplyAmount() != null) {
            BigDecimal relievableAmount = getRelievableAmount(realItem);
            couponAmount = couponEntity.getApplyAmount().compareTo(relievableAmount) > 0 ? relievableAmount : couponEntity.getApplyAmount();
        }

        Item entryItem = new Item();
        loanCalculator.entryItem(System.currentTimeMillis(), payType.name(), couponAmount, entryItem, realItem);
        return realItem;
    }

    @Override
    public Item payoffShouldPayItem(LoanOrderEntity loanOrder) throws NumberModeException {
        return shouldPayItem(loanOrder, PayTypeEnum.REPAY_AS_PLAN, 0);
    }

    @Override
    public Item entryItem(PayTypeEnum payType, PayOrderEntity payOrder, LoanOrderEntity loanOrder) throws LoanEntryException, NumberModeException {
        // 入账的Item
        Item entryItem = new Item();

        Item orderRealItem = this.realItem(loanOrder, payType, payOrder.getPostponeDays());
        BigDecimal shouldPay = orderRealItem.sum();

        CouponEntity couponEntity = couponService.getEffectiveBundledCoupon(loanOrder.getOrderId());
        BigDecimal couponAmount = BigDecimal.ZERO;
        if (couponEntity != null && couponEntity.getApplyAmount() != null) {
            BigDecimal relievableAmount = getRelievableAmount(orderRealItem);
            couponAmount = couponEntity.getApplyAmount().compareTo(relievableAmount) > 0 ? relievableAmount : couponEntity.getApplyAmount();
            entryItem = loanCalculator.entryItem(System.currentTimeMillis(), payType.name(), couponAmount, entryItem, orderRealItem);
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
        /**
         * 只有少于应还金额的时候会报错
         * 为支持部分还款, 还款金额比应还金额要少时不抛异常
         */
//        if (entryItem.sum().compareTo(shouldPay) != 0) {
//            throw new LoanEntryException("订单" + loanOrder.getOrderId() + payType.getExplanation() + payOrder.getPayNumber() + ", 优惠" + applyAmount + ", 与应还: " + shouldPay + "不匹配!");
//        }
        /**
         *  超额还款控制
         *  优惠券随便它入了多少
         *  这么改是为了实现   延期还款时减免砍头息
         */
        if (entryItem.sum(SourceTypeEnum.PAY_ORDER).compareTo(payOrder.getPayNumber()) < 0) {
            throw new LoanEntryException("订单" + loanOrder.getOrderId() + payType.getExplanation() + payOrder.getPayNumber() + ", 优惠" + couponAmount + ", 与可入账金额: " + entryItem.sum() + "不匹配!");
        }
        return entryItem;
    }

    @Override
    public LoanOrderEntity itemEntry(LoanOrderEntity loanOrder, PayTypeEnum payType, Integer days, Item realItem, Item entryItem) throws LoanEntryException {
        return loanCalculator.itemEntry(loanOrder, payType.name(), days, realItem, entryItem);
    }

    @Override
    public List<RenewVo> getRenewInfo(LoanOrderEntity loanOrder) throws NumberModeException {
        if (!StatusEnum.LENT.name().equals(loanOrder.getStatus())) {
            return null;
        }
        CouponEntity couponEntity = couponService.getEffectiveBundledCoupon(loanOrder.getOrderId());
        BigDecimal couponAmount = BigDecimal.ZERO;
        if (couponEntity != null && couponEntity.getApplyAmount() != null) {
            couponAmount = couponEntity.getApplyAmount();
        }
        List<RenewVo> renews = new ArrayList<RenewVo>();
        for (int i = 1; i <= 2; i++) {
            Item item = loanCalculator.realItem(System.currentTimeMillis(), loanOrder, PayTypeEnum.REPAY_POSTPONE.name(), loanOrder.getLoanTerm() * i);
            RenewVo vo = new RenewVo();
            vo.setPeriod(loanOrder.getLoanTerm() * i);

            if (couponAmount.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal relievableAmount = getRelievableAmount(item);
                if (relievableAmount.compareTo(couponAmount) >= 0) {
                    vo.setAmount(item.sum().subtract(couponAmount));
                } else {
                    vo.setAmount(item.sum().subtract(relievableAmount));
                }
            } else {
                vo.setAmount(item.sum());
            }

            renews.add(vo);
        }

        return renews;
    }

    @Override
    public BigDecimal minRepay(LoanOrderEntity loanOrder) {
        // return loanOrder.getLoanNumber();
        return loanOrder.getLentNumber();
    }

    /**
     * 获取可减免金额
     * @param item
     * @return
     */
    private BigDecimal getRelievableAmount (Item item) {
        BigDecimal relievableAmount = BigDecimal.ZERO;
        for (FieldTypeEnum fieldTypeEnum : FieldTypeEnum.RELIEVABLE) {
            relievableAmount = relievableAmount.add(item.getFieldNumber(fieldTypeEnum));
        }
        return relievableAmount;
    }

}
