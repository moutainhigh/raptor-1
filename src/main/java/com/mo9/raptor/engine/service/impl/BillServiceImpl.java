package com.mo9.raptor.engine.service.impl;

import com.mo9.raptor.bean.vo.RenewVo;
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
import com.mo9.raptor.entity.CashAccountEntity;
import com.mo9.raptor.enums.BusinessTypeEnum;
import com.mo9.raptor.enums.PayTypeEnum;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.exception.LoanEntryException;
import com.mo9.raptor.service.CashAccountService;
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

    @Autowired
    private CashAccountService cashAccountService;


    @Override
    public Item realItem(LoanOrderEntity loanOrder, PayTypeEnum payType, Integer postponeDays) {
        return loanCalculator.realItem(System.currentTimeMillis(), loanOrder, payType.name(), postponeDays);
    }

    @Override
    public Item payoffRealItem(LoanOrderEntity loanOrder) {
        return loanCalculator.realItem(System.currentTimeMillis(), loanOrder, PayTypeEnum.REPAY_AS_PLAN.name(), 0);
    }

    @Override
    public Item shouldPayItem(LoanOrderEntity loanOrder, PayTypeEnum payType, Integer postponeDays) {
        Item realItem = this.realItem(loanOrder, payType, postponeDays);
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
    public Item payoffShouldPayItem(LoanOrderEntity loanOrder) {
        return shouldPayItem(loanOrder, PayTypeEnum.REPAY_AS_PLAN, 0);
    }

    @Override
    public Item entryItem(PayTypeEnum payType, PayOrderEntity payOrder, LoanOrderEntity loanOrder) throws LoanEntryException {
        // 入账的Item
        Item entryItem = new Item();

        Item orderRealItem = this.realItem(loanOrder, payType, payOrder.getPostponeDays());
        BigDecimal shouldPay = orderRealItem.sum();

        //CouponEntity couponEntity = couponService.getEffectiveBundledCoupon(loanOrder.getOrderId());
        CouponEntity couponEntity = null ;
        if(payOrder.getCouponId() != null){
            couponEntity = couponService.getByCouponId(payOrder.getCouponId());
        }

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

        /****************  计算本金**********************/
        //获取用户现金钱包剩余金额
        CashAccountEntity cashAccountEntity = cashAccountService.findByUserCode(loanOrder.getOwnerId()) ;
        BigDecimal surplusAmount = BigDecimal.ZERO ;
        if(cashAccountEntity != null && cashAccountEntity.getBalance().compareTo(BigDecimal.ZERO) == 1){
            //剩余金额大于0
            BigDecimal cashAmount = cashAccountEntity.getBalance() ;
            surplusAmount = shouldPay.subtract(entryItem.sum()) ;

            if(surplusAmount.compareTo(BigDecimal.ZERO) == 1 && cashAmount.compareTo(surplusAmount) == 1){
                //剩余金额大于差额  大于0
                entryItem = loanCalculator.entryItem(System.currentTimeMillis(), payType.name(), surplusAmount, entryItem, orderRealItem);
                for (Map.Entry<FieldTypeEnum, Unit> entry : entryItem.entrySet()) {
                    Unit unit = entry.getValue();
                    for (Field field : unit) {
                        if (field.getSourceType() == null) {
                            field.setDestinationId(loanOrder.getOrderId());
                            field.setDestinationType(DestinationTypeEnum.LOAN_ORDER);
                            field.setSourceId(payOrder.getOrderId());
                            field.setSourceType(SourceTypeEnum.CASH_REPAY);
                        }
                    }
                }
            }
        }

        /**************************************/

        /**
         * 只有少于应还金额的时候会报错
         */
        if (entryItem.sum().compareTo(shouldPay) != 0) {
            throw new LoanEntryException("订单" + loanOrder.getOrderId() + payType.getExplanation() + payOrder.getPayNumber() + ", 优惠" + applyAmount + ", 与应还: " + shouldPay + "不匹配!");
        }
        /**
         *  超额还款控制
         */
        if (entryItem.sum().compareTo(payOrder.getPayNumber().add(applyAmount).add(surplusAmount)) != 0) {
            throw new LoanEntryException("订单" + loanOrder.getOrderId() + payType.getExplanation() + payOrder.getPayNumber() + ", 优惠" + applyAmount + " , 钱包扣除" + surplusAmount +  ", 与可入账金额: " + entryItem.sum() + "不匹配!");
        }

        /****************  扣本金**********************/
        if(surplusAmount.compareTo(BigDecimal.ZERO) == 1){
            //差额大于0 - 减去余额
            //根据还款类型区分账户出账类型
            String channel = payOrder.getChannel() ;
            String type = payOrder.getType() ;
            ResCodeEnum resCodeEnum = ResCodeEnum.SUCCESS ;
            if(channel.equals("manual_pay")){
                //线下入账
                if(PayTypeEnum.REPAY_POSTPONE.name().equals(type)){
                    //延期
                    resCodeEnum = cashAccountService.entry(payOrder.getOwnerId() , surplusAmount, payOrder.getOrderId() , BusinessTypeEnum.UNDERLINE_BALANCE_POSTPONE);
                }else{
                    //还款
                    resCodeEnum = cashAccountService.entry(payOrder.getOwnerId() , surplusAmount, payOrder.getOrderId() , BusinessTypeEnum.UNDERLINE_BALANCE_REPAY);
                }
            }else{
                //线上入账
                if(PayTypeEnum.REPAY_POSTPONE.name().equals(type)){
                    //延期
                    resCodeEnum = cashAccountService.entry(payOrder.getOwnerId() , surplusAmount, payOrder.getOrderId(), BusinessTypeEnum.ONLINE_BALANCE_POSTPONE);
                }else{
                    //还款
                    resCodeEnum = cashAccountService.entry(payOrder.getOwnerId() , surplusAmount, payOrder.getOrderId(), BusinessTypeEnum.ONLINE_BALANCE_REPAY);
                }
            }
            if(ResCodeEnum.SUCCESS != resCodeEnum && ResCodeEnum.CASH_ACCOUNT_BUSINESS_NO_IS_EXIST != resCodeEnum){
                //未成功 , 也不是已处理过的数据 , 抛异常
                throw new LoanEntryException("订单" + loanOrder.getOrderId() + payType.getExplanation() + payOrder.getPayNumber() + ", 现金钱包操作入账" + surplusAmount + ", 失败 状态 : " + resCodeEnum);
            }else{
                //写入还款订单真实现金钱包金额
                payOrder.setBalanceNumber(surplusAmount);
            }
        }
        /**************************************/

        return entryItem;
    }

    @Override
    public LoanOrderEntity itemEntry(LoanOrderEntity loanOrder, PayTypeEnum payType, Integer days, Item realItem, Item entryItem) throws LoanEntryException {
        return loanCalculator.itemEntry(loanOrder, payType.name(), days, realItem, entryItem);
    }

    @Override
    public List<RenewVo> getRenewInfo(LoanOrderEntity loanOrderEntity) {
        List<RenewVo> renews = loanCalculator.getRenew(loanOrderEntity);
        if (renews != null && renews.size() > 0) {
            CouponEntity couponEntity = couponService.getEffectiveBundledCoupon(loanOrderEntity.getOrderId());
            BigDecimal applyAmount = BigDecimal.ZERO;
            if (couponEntity != null && couponEntity.getApplyAmount() != null) {
                applyAmount = couponEntity.getApplyAmount();
            }

            // 减去减免金额
            for (RenewVo renew : renews) {
                renew.setAmount(renew.getAmount().subtract(applyAmount));
            }
        }
        return renews;
    }
}
