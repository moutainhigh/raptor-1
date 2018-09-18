package com.mo9.raptor.engine.calculator;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
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
        if (StatusEnum.BEFORE_LENDING.contains(StatusEnum.valueOf(loanOrder.getStatus()))) {
            return originalItem;
        }

        Field interestField = new Field();
        interestField.setNumber(loanOrder.getInterestValue());
        interestField.setFieldType(FieldTypeEnum.INTEREST);
        originalItem.put(FieldTypeEnum.INTEREST, interestField);

        Field principalField = new Field();
        principalField.setFieldType(FieldTypeEnum.PRINCIPAL);
        // 管他放了多少钱, 一律按放款金额收钱
        principalField.setNumber(loanOrder.getLoanNumber());
        originalItem.put(FieldTypeEnum.PRINCIPAL, principalField);

        Field chargeField = new Field();
        chargeField.setFieldType(FieldTypeEnum.ALL_CHARGE);
        chargeField.setNumber(loanOrder.getPostponeUnitCharge());
        originalItem.put(FieldTypeEnum.ALL_CHARGE, chargeField);

        originalItem.setSequence(1);
        originalItem.setRepayDate(loanOrder.getLendTime() + loanOrder.getLoanTerm() * EngineStaticValue.DAY_MILLIS);
        return originalItem;
    }

    /**
     * 实时账单
     */
    @Override
    public Item realItem(Long date, LoanOrderEntity loanOrder) {

        Item item = originItem(loanOrder);
        if (item.size() == 0) {
            return item;
        }
        Long repaymentDate = loanOrder.getRepaymentDate();
        // 重新设置还款日
        item.setRepayDate(repaymentDate);
        date = TimeUtils.extractDateTime(date);
        Field penaltyField = new Field();
        penaltyField.setFieldType(FieldTypeEnum.PENALTY);
        if (date > repaymentDate) {
            // 计算逾期费
            Long overDueDate = (date - repaymentDate) / EngineStaticValue.DAY_MILLIS;
            BigDecimal penalty = loanOrder.getPenaltyValue().multiply(new BigDecimal(overDueDate));
            penaltyField.setNumber(penalty);
            item.setItemType(ItemTypeEnum.PREVIOUS);
        } else if (date.equals(repaymentDate)) {
            penaltyField.setNumber(BigDecimal.ZERO);
            item.setItemType(ItemTypeEnum.PERIOD);
        } else {
            penaltyField.setNumber(BigDecimal.ZERO);
            item.setItemType(ItemTypeEnum.PREPAY);
        }
        item.put(FieldTypeEnum.PENALTY, penaltyField);
        return item;
    }

    @Override
    public Item entryItem (Long date, String payType, BigDecimal paid, LoanOrderEntity loanOrder) throws LoanEntryException {
        BigDecimal originalPaid = paid.multiply(BigDecimal.ONE);
        Item entryItem = new Item();
        Item realItem = this.realItem(date, loanOrder);
        entryItem.setRepayDate(realItem.getRepayDate());
        entryItem.setItemType(realItem.getItemType());
        if (payType.equals(PayTypeEnum.REPAY_POSTPONE.name())) {
            Field penaltyField = realItem.get(FieldTypeEnum.PENALTY);
            if (penaltyField != null) {
                entryItem.put(FieldTypeEnum.PENALTY, penaltyField.clone());
                paid = paid.subtract(penaltyField.getNumber());
            }

            BigDecimal unitCharge = loanOrder.getPostponeUnitCharge();
            Field interestField = new Field();
            Field chargeField = new Field();
            while(paid.compareTo(BigDecimal.ZERO) > 0) {
                if (paid.compareTo(unitCharge) >= 0) {
                    chargeField.setNumber(chargeField.getNumber().add(unitCharge));
                    paid = paid.subtract(unitCharge);
                } else {
                    throw new LoanEntryException("订单延期还款" + originalPaid.toPlainString() + "无法入账的金额" + paid.toPlainString());
                }
                if (paid.compareTo(loanOrder.getInterestValue()) >= 0) {
                    interestField.setNumber(interestField.getNumber().add(loanOrder.getInterestValue()));
                    paid = paid.subtract(loanOrder.getInterestValue());
                } else {
                    throw new LoanEntryException("订单延期还款" + originalPaid.toPlainString() + "无法入账的金额" + paid.toPlainString());
                }
            }
            entryItem.put(FieldTypeEnum.INTEREST, interestField);
            entryItem.put(FieldTypeEnum.ALL_CHARGE, chargeField);

        } else {
            for (FieldTypeEnum fieldType: FieldTypeEnum.values()) {
                if (paid.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }
                if (fieldType.getSequence() < 0 || fieldType.getSequence() >= FieldTypeEnum.ALL.getSequence()) {
                    continue;
                }

                Field field = realItem.get(fieldType);
                if (field == null) {
                    continue;
                }
                BigDecimal number = field.getNumber();
                if (number.compareTo(paid) >= 0) {
                    // 还款金额分配结束
                    Field cloneField = field.clone();
                    cloneField.setNumber(paid);
                    entryItem.put(fieldType, cloneField);
                    paid = BigDecimal.ZERO;
                } else {
                    // 继续分配
                    Field cloneField = field.clone();
                    entryItem.put(fieldType, cloneField);
                    paid = paid.subtract(number);
                }
            }
            if (BigDecimal.ZERO.compareTo(paid) != 0) {
                throw new LoanEntryException("订单延期还款" + originalPaid.toPlainString() + "无法入账的金额" + paid.toPlainString());
            }
        }
        return entryItem;
    }

    @Override
    public Boolean checkValidRepayAmount(Long date, String payType, BigDecimal paid, LoanOrderEntity loanOrder) throws LoanEntryException {
        Item entryItem = this.entryItem(date, payType, paid, loanOrder);
        BigDecimal sum = entryItem.sum();
        if (sum.compareTo(paid) == 0) {
            return true;
        }

        Item realItem = this.realItem(date, loanOrder);
        BigDecimal principal = realItem.getFieldNumber(FieldTypeEnum.PRINCIPAL);
        BigDecimal penalty = realItem.getFieldNumber(FieldTypeEnum.PENALTY);
        int paidAmount = entryItem.sum().intValue();
        int baseAmount = realItem.sum().subtract(principal).subtract(penalty).add(loanOrder.getPostponeUnitCharge()).intValue();
        if (paidAmount % baseAmount == 0) {
            return true;
        }
        return false;
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
            BigDecimal principal = realItem.getFieldNumber(FieldTypeEnum.PRINCIPAL);
            BigDecimal penalty = realItem.getFieldNumber(FieldTypeEnum.PENALTY);
            BigDecimal entryPenalty = entryItem.getFieldNumber(FieldTypeEnum.PENALTY);
            int paidAmount = entryItem.sum().subtract(entryItem.getFieldNumber(FieldTypeEnum.PENALTY)).intValue();
            int baseAmount = realItem.sum().subtract(principal).subtract(penalty).intValue();
            if (paidAmount / baseAmount != days / loanOrder.getLoanTerm()) {
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
                loanOrder.setStatus(StatusEnum.PAYOFF.name());
            } else {
                logger.error("订单[{}]应还[{}], 实际还款[{}], 无法入账!", loanOrder.getOrderId(), realItemSum, entryItemSum);
                throw new LoanEntryException("订单" + loanOrder.getOrderId() + "应还" + realItemSum + ", 实际还款" + entryItemSum + ", 无法入账!");
            }
        }
        return loanOrder;
    }


    @Override
    public List<JSONObject> getRenew (LoanOrderEntity loanOrder) {
        if (!StatusEnum.LENT.name().equals(loanOrder.getStatus())) {
            return null;
        }
        Item item = this.realItem(System.currentTimeMillis(), loanOrder);
        List<JSONObject> renew = new ArrayList<JSONObject>();
        for (int i = 1; i <= 2; i++) {
            JSONObject unit = new JSONObject();
            unit.put("period", loanOrder.getLoanTerm() * i);
            if (i == 1) {
                // 第一次加上罚息
                unit.put("amount", item.sum().subtract(item.getFieldNumber(FieldTypeEnum.PRINCIPAL)).multiply(new BigDecimal(i)));
            } else {
                unit.put("amount", item.sum().subtract(item.getFieldNumber(FieldTypeEnum.PRINCIPAL)).subtract(item.getFieldNumber(FieldTypeEnum.PENALTY)).multiply(new BigDecimal(i)));
            }
            renew.add(unit);
        }
        return renew;
    }
}