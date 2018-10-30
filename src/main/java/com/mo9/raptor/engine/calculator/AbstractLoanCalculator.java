package com.mo9.raptor.engine.calculator;

import com.mo9.raptor.bean.vo.RenewVo;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.structure.Unit;
import com.mo9.raptor.engine.structure.field.Field;
import com.mo9.raptor.engine.structure.field.FieldTypeEnum;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.engine.structure.item.ItemTypeEnum;
import com.mo9.raptor.engine.utils.EngineStaticValue;
import com.mo9.raptor.engine.utils.TimeUtils;
import com.mo9.raptor.enums.PayTypeEnum;
import com.mo9.raptor.exception.LoanEntryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 借贷订单计算器
 */
@Component
public abstract class AbstractLoanCalculator implements ILoanCalculator {

    private static final Logger logger = LoggerFactory.getLogger(AbstractLoanCalculator.class);

    /**
     * 初始账单
     */
    @Override
    public Item originItem(LoanOrderEntity loanOrder) {

        Item originalItem = new Item();
        if (!StatusEnum.LENT.name().equals(loanOrder.getStatus())) {
            return originalItem;
        }

        // 计算利息
        Unit interestUnit = new Unit(FieldTypeEnum.INTEREST);
        Field interestField = new Field();
        interestField.setNumber(loanOrder.getInterestValue());
        interestField.setFieldType(FieldTypeEnum.INTEREST);
        interestUnit.add(interestField);
        originalItem.put(FieldTypeEnum.INTEREST, interestUnit);

        // 计算本金
        Unit principalUnit = new Unit(FieldTypeEnum.PRINCIPAL);
        Field principalField = new Field();
        principalField.setFieldType(FieldTypeEnum.PRINCIPAL);
        principalField.setNumber(loanOrder.getLoanNumber().subtract(loanOrder.getChargeValue()));
        principalUnit.add(principalField);
        originalItem.put(FieldTypeEnum.PRINCIPAL, principalUnit);

        // 计算砍头息
        Unit cutChargeUnit = new Unit(FieldTypeEnum.CUT_CHARGE);
        Field cutChargeField = new Field();
        cutChargeField.setFieldType(FieldTypeEnum.CUT_CHARGE);
        cutChargeField.setNumber(loanOrder.getChargeValue());
        cutChargeUnit.add(cutChargeField);
        originalItem.put(FieldTypeEnum.CUT_CHARGE, cutChargeUnit);

        originalItem.setSequence(1);
        originalItem.setRepayDate(loanOrder.getLendTime() + loanOrder.getLoanTerm() * EngineStaticValue.DAY_MILLIS);
        return originalItem;
    }

    @Override
    public Item entryItem (Long date, String payType, BigDecimal paid, Item entryItem, Item realItem) {
        entryItem.setRepayDate(realItem.getRepayDate());
        entryItem.setItemType(realItem.getItemType());
        for (FieldTypeEnum fieldType: FieldTypeEnum.values()) {
            if (paid.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            if (fieldType.getSequence() < 0 || fieldType.getSequence() >= FieldTypeEnum.ALL.getSequence()) {
                continue;
            }

            Unit realUnit = realItem.get(fieldType);
            if (realUnit == null || realUnit.size() == 0) {
                continue;
            }
            Field realField = realUnit.get(0);
            if (BigDecimal.ZERO.compareTo(realField.getNumber()) >= 0) {
                continue;
            }
            Field cloneField = realField.clone();
            Unit entryUnit = entryItem.get(fieldType);
            if (entryUnit == null) {
                entryUnit = new Unit(fieldType);
                entryItem.put(fieldType, entryUnit);
            }
            entryUnit.add(cloneField);
            BigDecimal number = realField.getNumber();
            if (number.compareTo(paid) >= 0) {
                // 还款金额分配结束
                cloneField.setNumber(paid);
                entryItem.put(fieldType, entryUnit);
                realField.setNumber(realField.getNumber().subtract(paid));
                paid = BigDecimal.ZERO;
            } else {
                // 继续分配
                cloneField.setNumber(number);
                entryItem.put(fieldType, entryUnit);
                realField.setNumber(BigDecimal.ZERO);
                paid = paid.subtract(number);
            }
        }
        return entryItem;
    }

    /**
     * 实时账单
     */
    @Override
    public Item realItem(Long date, LoanOrderEntity loanOrder, String payType, Integer postponeDays) {

        Item item = originItem(loanOrder);
        if (item.size() == 0) {
            return item;
        }

        // 把时间设为当前时间
        Calendar userDate = Calendar.getInstance();
        userDate.setTimeInMillis(date);
        userDate = TimeUtils.extractDateTime(userDate);
        Calendar repaymentDate = Calendar.getInstance();
        repaymentDate.setTimeInMillis(loanOrder.getRepaymentDate());
        repaymentDate = TimeUtils.extractDateTime(repaymentDate);

        // 重新设置还款日
        item.setRepayDate(loanOrder.getRepaymentDate());
        Field penaltyField = new Field();
        penaltyField.setFieldType(FieldTypeEnum.PENALTY);
        if (userDate.after(repaymentDate))  {
            // 计算逾期费
            Long overDueDate = (userDate.getTimeInMillis() - repaymentDate.getTimeInMillis()) / EngineStaticValue.DAY_MILLIS;
            BigDecimal penalty = loanOrder.getPenaltyValue().multiply(new BigDecimal(overDueDate));
            penaltyField.setNumber(penalty);
            item.setItemType(ItemTypeEnum.PREVIOUS);
        } else if (userDate.equals(repaymentDate)) {
            penaltyField.setNumber(BigDecimal.ZERO);
            item.setItemType(ItemTypeEnum.PERIOD);
        } else {
            penaltyField.setNumber(BigDecimal.ZERO);
            item.setItemType(ItemTypeEnum.PREPAY);
        }
        Unit penaltyUnit = new Unit(FieldTypeEnum.PENALTY);
        penaltyUnit.add(penaltyField);
        item.put(FieldTypeEnum.PENALTY, penaltyUnit);
        if (payType.equals(PayTypeEnum.REPAY_POSTPONE.name())) {
            // 移除本金, 砍头息, 增加延期服务费
            item.remove(FieldTypeEnum.PRINCIPAL);
            item.remove(FieldTypeEnum.CUT_CHARGE);
            Field chargeField = new Field();
            chargeField.setFieldType(FieldTypeEnum.ALL_CHARGE);

            Integer multiplyPower = postponeDays / loanOrder.getLoanTerm();

            chargeField.setNumber(loanOrder.getPostponeUnitCharge().multiply(new BigDecimal(multiplyPower)));
            Unit chargeUnit = new Unit(FieldTypeEnum.ALL_CHARGE);
            chargeUnit.add(chargeField);
            item.put(FieldTypeEnum.ALL_CHARGE, chargeUnit);
            item.setItemType(ItemTypeEnum.POSTPONE);
            // 延期的基数
            item.setPostponeDays(postponeDays);
            // 利息也要翻倍
            Field interestField = item.get(FieldTypeEnum.INTEREST).get(0);
            interestField.setNumber(interestField.getNumber().multiply(new BigDecimal(multiplyPower)));
        }
        return item;
    }

    /**
     * 入账处理
     */
    @Override
    public LoanOrderEntity itemEntry(LoanOrderEntity loanOrder, String payType, Integer days, Item realItem, Item entryItem) throws LoanEntryException {
        // 直接入, 状态正确性由状态机保证

        BigDecimal realItemSum = realItem.sum();
        BigDecimal entryItemSum = entryItem.sum();
        if (payType.equals(PayTypeEnum.REPAY_POSTPONE.name())) {
            BigDecimal entryPenalty = entryItem.getFieldNumber(FieldTypeEnum.PENALTY);
            BigDecimal paidAmount = entryItem.sum();
            BigDecimal baseAmount = realItem.sum();
            if (paidAmount.compareTo(baseAmount) != 0) {
                throw new LoanEntryException("订单" + loanOrder.getOrderId() + "还款" + entryItemSum + ", 延期" + days + ", 不合法!");
            }
            if (entryPenalty.compareTo(BigDecimal.ZERO) > 0) {
                // 有罚息
                int penaltyPostponeDays = entryPenalty.divide(loanOrder.getPenaltyValue(), 0, BigDecimal.ROUND_DOWN).intValue();
                loanOrder.setRepaymentDate(loanOrder.getRepaymentDate() + (days + penaltyPostponeDays) * EngineStaticValue.DAY_MILLIS);
            } else {
                // 无罚息
                loanOrder.setRepaymentDate(loanOrder.getRepaymentDate() + days * EngineStaticValue.DAY_MILLIS);
            }
        } else {
            if (realItemSum.compareTo(entryItemSum) == 0) {
                // 直接还清
                loanOrder.setPayoffTime(System.currentTimeMillis());
                loanOrder.setStatus(StatusEnum.PAYOFF.name());
            } else {
                logger.error("订单[{}]应还[{}], 实际还款[{}], 无法入账!", loanOrder.getOrderId(), realItemSum, entryItemSum);
                throw new LoanEntryException("订单" + loanOrder.getOrderId() + "应还" + realItemSum + ", 实际还款" + entryItemSum + ", 无法入账!");
            }
        }
        return loanOrder;
    }

    @Override
    public List<RenewVo> getRenew (LoanOrderEntity loanOrder) {
        if (!StatusEnum.LENT.name().equals(loanOrder.getStatus())) {
            return null;
        }
        List<RenewVo> renew = new ArrayList<RenewVo>();
        for (int i = 1; i <= 2; i++) {
            Item item = this.realItem(System.currentTimeMillis(), loanOrder, PayTypeEnum.REPAY_POSTPONE.name(), loanOrder.getLoanTerm() * i);
            RenewVo vo = new RenewVo();
            vo.setPeriod(loanOrder.getLoanTerm() * i);
            vo.setAmount(item.sum());
            renew.add(vo);
        }
        return renew;
    }
}