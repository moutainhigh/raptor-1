package com.mo9.raptor.engine.calculator;

import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.exception.MergeException;
import com.mo9.raptor.engine.exception.UnSupportTimeDiffException;
import com.mo9.raptor.engine.structure.Scheme;
import com.mo9.raptor.engine.structure.field.Field;
import com.mo9.raptor.engine.structure.field.FieldTypeEnum;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.engine.utils.EngineStaticValue;
import com.mo9.raptor.engine.utils.TimeUtils;
import com.mo9.raptor.enums.PayTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 借贷订单计算器
 */
@Component
public abstract class AbstractLoanCalculator implements ILoanCalculator {

    private static final Logger logger = LoggerFactory.getLogger(AbstractLoanCalculator.class);


    @Value("${postpone.unit.charge}")
    private String postponeCharge;

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
        principalField.setNumber(loanOrder.getLentNumber());
        originalItem.put(FieldTypeEnum.PRINCIPAL, principalField);

        Field chargeField = new Field();
        chargeField.setFieldType(FieldTypeEnum.ALL_CHARGE);
        chargeField.setNumber(loanOrder.getChargeValue());
        originalItem.put(FieldTypeEnum.ALL_CHARGE, chargeField);

        originalItem.setSequence(1);
        originalItem.setRepayDate(loanOrder.getLentTime() + loanOrder.getLoanTerm() * EngineStaticValue.DAY_MILLIS);
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
        date = TimeUtils.extractDateTime(date);
        if (date >= repaymentDate) {
            // 计算逾期费
            Long overDueDate = (date - repaymentDate) / EngineStaticValue.DAY_MILLIS + 1;
            BigDecimal penalty = loanOrder.getPenaltyValue().multiply(new BigDecimal(overDueDate));
            Field penaltyField = new Field();
            penaltyField.setFieldType(FieldTypeEnum.PENALTY);
            penaltyField.setNumber(penalty);
            item.put(FieldTypeEnum.PENALTY, penaltyField);
        }
        return item;
    }

    @Override
    public Item entryItem (Long date, String payType, BigDecimal paid, LoanOrderEntity loanOrder) {
        Item realItem = this.realItem(date, loanOrder);

        Item entryItem = new Item();
        BigDecimal unitCharge = new BigDecimal(postponeCharge);
        if (payType.equals(PayTypeEnum.REPAY_POSTPONE.name())) {
            Field interestField = new Field();
            Field chargeField = new Field();
            while(paid.compareTo(BigDecimal.ZERO) > 0) {
                if (paid.compareTo(unitCharge) >= 0) {
                    chargeField.setNumber(chargeField.getNumber().add(unitCharge));
                    paid = paid.subtract(unitCharge);
                } else {
                    chargeField.setNumber(chargeField.getNumber().add(paid));
                    paid = BigDecimal.ZERO;
                }
                if (paid.compareTo(loanOrder.getInterestValue()) >= 0) {
                    interestField.setNumber(interestField.getNumber().add(loanOrder.getInterestValue()));
                    paid = paid.subtract(loanOrder.getInterestValue());
                } else {
                    interestField.setNumber(interestField.getNumber().add(paid));
                    paid = BigDecimal.ZERO;
                }
            }
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
                    cloneField.setNumber(number);
                    entryItem.put(fieldType, cloneField);
                } else {
                    // 继续分配
                    paid = paid.subtract(number);
                    Field cloneField = field.clone();
                    entryItem.put(fieldType, cloneField);
                }
            }
        }
        return entryItem;
    }

    @Override
    public Boolean checkValidRepayAmount(Long date, String payType, BigDecimal paid, LoanOrderEntity loanOrder) {
        Item entryItem = this.entryItem(date, payType, paid, loanOrder);
        BigDecimal sum = entryItem.sum();
        if (sum.compareTo(paid) == 0) {
            return true;
        }

        Item realItem = this.realItem(date, loanOrder);
        BigDecimal principal = realItem.getFieldNumber(FieldTypeEnum.PRINCIPAL);
        BigDecimal penalty = realItem.getFieldNumber(FieldTypeEnum.PENALTY);
        int paidAmount = entryItem.sum().intValue();
        int baseAmount = realItem.sum().subtract(principal).subtract(penalty).add(new BigDecimal(postponeCharge)).intValue();
        if (paidAmount % baseAmount == 0) {
            return true;
        }
        return false;
    }

    /**
     * 入账处理
     */
    @Override
    public LoanOrderEntity itemEntry(LoanOrderEntity loanOrder, String payType, Item realItem, Item entryItem) {
        // 直接入, 状态正确性由状态机保证

        BigDecimal realItemSum = realItem.sum();
        BigDecimal entryItemSum = entryItem.sum();
        if (payType.equals(PayTypeEnum.REPAY_POSTPONE.name())) {
            if (realItemSum.compareTo(entryItemSum) == 0) {
                // 直接还清
                loanOrder.setStatus(StatusEnum.PAYOFF.name());
            }
        } else {
            BigDecimal principal = realItem.getFieldNumber(FieldTypeEnum.PRINCIPAL);
            BigDecimal penalty = realItem.getFieldNumber(FieldTypeEnum.PENALTY);
            int paidAmount = entryItem.sum().intValue();
            int baseAmount = realItem.sum().subtract(principal).subtract(penalty).add(new BigDecimal(postponeCharge)).intValue();
            if (paidAmount % baseAmount == 0) {
                // 还的钱是倍数
                int times = paidAmount / baseAmount;
                loanOrder.setRepaymentDate(loanOrder.getRepaymentDate() + times * loanOrder.getLoanTerm() * EngineStaticValue.DAY_MILLIS);
            }
        }
        return loanOrder;
    }
}