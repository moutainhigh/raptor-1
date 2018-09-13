package com.mo9.raptor.engine.structure;


import com.mo9.raptor.engine.strategy.weight.Weight;
import com.mo9.raptor.engine.structure.field.Field;
import com.mo9.raptor.engine.structure.item.Item;
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

    public SchemeEntryMap pay (String payOrderId, BigDecimal payNumber,
                               Weight[][] strategyWeightMap, Weight[][] couponWeightMap,
                               BigDecimal dailyPenaltyRate, long date)
            throws InvalidSchemeFieldException, MergeException {

        SchemeEntryMap schemeEntryMap = new SchemeEntryMap ();

        for (FieldTypeEnum fieldType: FieldTypeEnum.values()) {
            if (fieldType.getSequence() < 0 || fieldType.getSequence() >= FieldTypeEnum.ALL.getSequence()) {
                continue;
            }
            for (ItemTypeEnum itemType: ItemTypeEnum.values()) {
                if (itemType.getSequence() < 0 || itemType.getSequence() >= ItemTypeEnum.ALL.getSequence()) {
                    continue;
                }
                if (itemType != ItemTypeEnum.PREPAY) {

                    Weight strategyWeight = strategyWeightMap[itemType.getSequence()][fieldType.getSequence()];
                    Weight couponWeight = couponWeightMap[itemType.getSequence()][fieldType.getSequence()];
                    SchemeEntryMap entryMap = this.payItemField(payOrderId, payNumber, itemType, fieldType, strategyWeight, couponWeight, dailyPenaltyRate, date);
                    schemeEntryMap = schemeEntryMap.merge(entryMap);

                    if (payNumber != null && entryMap != null) {
                        payNumber = payNumber.subtract(entryMap.fromPay());
                    }
                }
            }
        }

        /** 处理提前还款 */
        Item prepayItem = this.get(LoanLimitation.PREPAY_ITEM);

        if (prepayItem == null || prepayItem.size() == 0) {
            return schemeEntryMap;
        }

        Item prepayPayLoanItem = new Item();
        prepayPayLoanItem.setItemType(ItemTypeEnum.PREPAY);
        prepayPayLoanItem.setSequence(LoanLimitation.PREPAY_ITEM);
        Item prepayCouponLoanItem = new Item();
        prepayCouponLoanItem.setItemType(ItemTypeEnum.PREPAY);
        prepayCouponLoanItem.setSequence(LoanLimitation.PREPAY_ITEM);
        Item prepayPayStrategyItem = new Item();
        prepayPayStrategyItem.setItemType(ItemTypeEnum.PREPAY);
        prepayPayStrategyItem.setSequence(LoanLimitation.PREPAY_ITEM);
        Item prepayCouponStrategyItem = new Item();
        prepayCouponStrategyItem.setItemType(ItemTypeEnum.PREPAY);
        prepayCouponStrategyItem.setSequence(LoanLimitation.PREPAY_ITEM);

        for (Entry<FieldTypeEnum, Field> entry : prepayItem.entrySet()) {
            Weight strategyWeight = strategyWeightMap[ItemTypeEnum.PREPAY.getSequence()][entry.getKey().getSequence()];
            Weight couponWeight = couponWeightMap[ItemTypeEnum.PREPAY.getSequence()][entry.getKey().getSequence()];

            /** 先计算足额支付下的明细 */
            FieldEntryMap fieldEntryMap = entry.getValue().pay(payOrderId, null, strategyWeight, couponWeight);
            Field field = fieldEntryMap.get(EntryEnum.PAY_LOAN);
            if (field != null) {
                prepayPayLoanItem.put(entry.getKey(), field);
            }
            field = fieldEntryMap.get(EntryEnum.COUPON_LOAN);
            if (field != null) {
                prepayCouponLoanItem.put(entry.getKey(), field);
            }
            field = fieldEntryMap.get(EntryEnum.PAY_STRATEGY);
            if (field != null) {
                prepayPayStrategyItem.put(entry.getKey(), field);
            }
            field = fieldEntryMap.get(EntryEnum.COUPON_STRATEGY);
            if (field != null) {
                prepayCouponStrategyItem.put(entry.getKey(), field);
            }
        }

        BigDecimal shouldPay = prepayPayLoanItem.sum().add(prepayPayStrategyItem.sum());

        /** 若不足以足额支付，进行修正 */
        if (shouldPay.compareTo(BigDecimal.ZERO) > 0 && payNumber != null && payNumber.compareTo(shouldPay) < 0) {
            this.schemePrepay(prepayPayLoanItem, prepayCouponLoanItem, prepayPayStrategyItem,prepayCouponStrategyItem, payNumber, strategyWeightMap, couponWeightMap);
        }
        Scheme payLoanScheme = schemeEntryMap.get(EntryEnum.PAY_LOAN);
        if (payLoanScheme == null) {
            payLoanScheme = new Scheme();
            schemeEntryMap.put(EntryEnum.PAY_LOAN, payLoanScheme);
        }
        payLoanScheme.put(LoanLimitation.PREPAY_ITEM, prepayPayLoanItem);

        Scheme payStrategyScheme = schemeEntryMap.get(EntryEnum.PAY_STRATEGY);
        if (payStrategyScheme == null) {
            payStrategyScheme = new Scheme();
            schemeEntryMap.put(EntryEnum.PAY_STRATEGY, payStrategyScheme);
        }
        payStrategyScheme.put(LoanLimitation.PREPAY_ITEM, prepayPayStrategyItem);

        Scheme couponLoanScheme = schemeEntryMap.get(EntryEnum.COUPON_LOAN);
        if (couponLoanScheme == null) {
            couponLoanScheme = new Scheme();
            schemeEntryMap.put(EntryEnum.COUPON_LOAN, couponLoanScheme);
        }
        couponLoanScheme.put(LoanLimitation.PREPAY_ITEM, prepayCouponLoanItem);

        Scheme couponStrategyScheme = schemeEntryMap.get(EntryEnum.COUPON_STRATEGY);
        if (couponStrategyScheme == null) {
            couponStrategyScheme = new Scheme();
            schemeEntryMap.put(EntryEnum.COUPON_STRATEGY, couponStrategyScheme);
        }
        couponStrategyScheme.put(LoanLimitation.PREPAY_ITEM, prepayCouponStrategyItem);

        for (Scheme scheme: schemeEntryMap.values()) {
            scheme.setPenaltyOverpay(this.penaltyOverpay);
            scheme.setPenaltyBoundDate(this.penaltyBoundDate);
            scheme.setRepayGraceDays(this.repayGraceDays);
        }

        return schemeEntryMap;
    }

    public SchemeEntryMap payItemField (String payOrderId, BigDecimal payNumber,
                                        ItemTypeEnum itemType, FieldTypeEnum fieldType,
                                        Weight strategyWeight, Weight couponWeight,
                                        BigDecimal dailyPenaltyRate, long date) throws InvalidSchemeFieldException {

        SchemeEntryMap schemeEntryMap = new SchemeEntryMap ();

        /** 0-剔除不需要处理的项 : 当期和后期，不存在罚息款项，不需要处理；提前还款相关项需特殊处理 */
        if ((fieldType == FieldTypeEnum.PENALTY && itemType != ItemTypeEnum.PREVIOUS) || itemType == ItemTypeEnum.PREPAY) {
            return schemeEntryMap;
        }

        /** 1-获取与策略同一粒度的款项总额 */
        BigDecimal sum = this.itemFieldSum(itemType, fieldType);
        if (sum.compareTo(BigDecimal.ZERO) <= 0) {
            return schemeEntryMap;
        }

        /** 2-与策略同粒度下，进行加权计算 */
        Field field = new Field();
        field.setNumber(sum);
        FieldEntryMap fieldEntryMap = field.pay(payOrderId, payNumber, strategyWeight, couponWeight);

        BigDecimal payLoan = fieldEntryMap.get(EntryEnum.PAY_LOAN).getNumber();
        BigDecimal couponLoan = fieldEntryMap.get(EntryEnum.COUPON_LOAN).getNumber();
        BigDecimal entryLoan = payLoan.add(couponLoan);

        /** 3-根据整体的支付和优惠，细化到分期粒度 */
        if (fieldType == FieldTypeEnum.PENALTY && itemType == ItemTypeEnum.PREVIOUS) {
            this.schemePenalty(entryLoan, dailyPenaltyRate, date);

            schemeEntryMap.get(EntryEnum.PAY_LOAN).setPenaltyBoundDate(this.penaltyBoundDate);
            schemeEntryMap.get(EntryEnum.PAY_LOAN).setPenaltyOverpay(this.penaltyOverpay);
            schemeEntryMap.get(EntryEnum.PAY_LOAN).setRepayGraceDays(this.repayGraceDays);
            schemeEntryMap.get(EntryEnum.PAY_STRATEGY).setPenaltyBoundDate(this.penaltyBoundDate);
            schemeEntryMap.get(EntryEnum.PAY_STRATEGY).setPenaltyOverpay(this.penaltyOverpay);
            schemeEntryMap.get(EntryEnum.PAY_STRATEGY).setRepayGraceDays(this.repayGraceDays);
            schemeEntryMap.get(EntryEnum.COUPON_LOAN).setPenaltyBoundDate(this.penaltyBoundDate);
            schemeEntryMap.get(EntryEnum.COUPON_LOAN).setPenaltyOverpay(this.penaltyOverpay);
            schemeEntryMap.get(EntryEnum.COUPON_LOAN).setRepayGraceDays(this.repayGraceDays);
            schemeEntryMap.get(EntryEnum.COUPON_STRATEGY).setPenaltyBoundDate(this.penaltyBoundDate);
            schemeEntryMap.get(EntryEnum.COUPON_STRATEGY).setPenaltyOverpay(this.penaltyOverpay);
            schemeEntryMap.get(EntryEnum.COUPON_STRATEGY).setRepayGraceDays(this.repayGraceDays);

        } else {
            this.schemeField(entryLoan, itemType, fieldType);
        }

        /** 4-从分期粒度，计算权重明细 */
        for (Item item: this.values()) {
            if (item.getItemType() == itemType) {

                Field itemField = item.get(fieldType);
                if (itemField == null) {
                    continue;
                }

                /** 入账到订单的金额，该金额包含优惠，所以通过正向计算出相应的优惠权重值 */
                BigDecimal fieldNumber = itemField.getNumber();
                BigDecimal fieldCouponLoan = couponWeight.forward(fieldNumber);
                BigDecimal fieldPayLoan = fieldNumber.subtract(fieldCouponLoan);
                BigDecimal fieldPayStrategy = strategyWeight.inverse(fieldPayLoan);
                BigDecimal fieldCouponStrategy = strategyWeight.inverse(fieldCouponLoan);

                /** 保存到明细表 */
                schemeEntryMap.get(EntryEnum.PAY_LOAN).createItemField(item.getSequence(), itemType, item.getRepayDate(),
                        fieldType, fieldPayLoan, SourceEnum.PAY_ORDER, payOrderId,
                        itemField.getDestination(), itemField.getDestinationId());
                schemeEntryMap.get(EntryEnum.COUPON_LOAN).createItemField(item.getSequence(), itemType, item.getRepayDate(),
                        fieldType, fieldCouponLoan, SourceEnum.COUPON, couponWeight.getId(),
                        itemField.getDestination(), itemField.getDestinationId());
                schemeEntryMap.get(EntryEnum.PAY_STRATEGY).createItemField(item.getSequence(), itemType, item.getRepayDate(),
                        fieldType, fieldPayStrategy, SourceEnum.PAY_ORDER, payOrderId,
                        DestinationEnum.PAY_STRATEGY, strategyWeight.getId());
                schemeEntryMap.get(EntryEnum.COUPON_STRATEGY).createItemField(item.getSequence(), itemType, item.getRepayDate(),
                        fieldType, fieldCouponStrategy, SourceEnum.COUPON, couponWeight.getId(),
                        DestinationEnum.PAY_STRATEGY, strategyWeight.getId());
            }
        }

        return schemeEntryMap;
    }

    public void createItemField (int itemSequence, ItemTypeEnum itemType, long repayDate,
                                 FieldTypeEnum fieldType, BigDecimal fieldNumber,
                                 SourceEnum source, String sourceId,
                                 DestinationEnum destination, String destinationId) {

        Item item = new Item();
        item.setSequence(itemSequence);
        item.setItemType(itemType);
        item.setRepayDate(repayDate);

        Field field = new Field();
        field.setFieldType(fieldType);
        field.setSource(source);
        field.setSourceId(sourceId);
        field.setDestination(destination);
        field.setDestinationId(destinationId);
        field.setNumber(fieldNumber);

        item.put(fieldType, field);
        this.put(itemSequence, item);
    }

    @Override
    public Scheme clone () {

        Scheme scheme = new Scheme();

        scheme.repayGraceDays = this.repayGraceDays;
        scheme.penaltyBoundDate = this.penaltyBoundDate;
        scheme.penaltyOverpay = this.penaltyOverpay;

        for (Item item: this.values()) {
            scheme.put(item.getSequence(), item.clone());
        }

        return scheme;
    }

    public Scheme merge (Scheme scheme) throws MergeException {

        Scheme clone = this.clone();

        if (scheme == null || scheme.size() == 0) {
            return clone;
        }

        if (scheme.repayGraceDays != null && clone.repayGraceDays != null && !scheme.repayGraceDays.equals(clone.repayGraceDays)) {
            throw new MergeException("Scheme合并运算不合法！repayGraceDays变量冲突：" + scheme.repayGraceDays + "-" + clone.repayGraceDays);
        }
        if (scheme.penaltyBoundDate != null && clone.penaltyBoundDate != null && !scheme.penaltyBoundDate.equals(clone.penaltyBoundDate)) {
            throw new MergeException("Scheme合并运算不合法！penaltyBoundDate变量冲突：" + scheme.penaltyBoundDate + "-" + clone.penaltyBoundDate);
        }
        if (scheme.penaltyOverpay != null && clone.penaltyOverpay != null && !scheme.penaltyOverpay.equals(clone.penaltyOverpay)) {
            throw new MergeException("Scheme合并运算不合法！penaltyOverpay变量冲突：" + scheme.penaltyOverpay + "-" + clone.penaltyOverpay);
        }

        if (scheme.repayGraceDays != null) {
            clone.repayGraceDays = scheme.repayGraceDays;
        }
        if (scheme.penaltyBoundDate != null) {
            clone.penaltyBoundDate = scheme.penaltyBoundDate;
        }
        if (scheme.penaltyOverpay != null) {
            clone.penaltyOverpay = scheme.penaltyOverpay;
        }

        for (Item item: scheme.values()) {
            Item cloneItem = clone.get(item.getSequence());
            if (cloneItem == null) {
                clone.put(item.getSequence(), item.clone());
            } else {
                clone.put(item.getSequence(), cloneItem.merge(item));
            }
        }

        return clone;
    }

    public Scheme add (Scheme scheme) {
        Scheme clone = this.clone();

        if (scheme == null || scheme.size() == 0) {
            return clone;
        }

        for (Item item: scheme.values()) {
            Item cloneItem = clone.get(item.getSequence());
            if (cloneItem == null) {
                clone.put(item.getSequence(), item.clone());
            } else {
                clone.put(item.getSequence(), cloneItem.add(item));
            }
        }

        return clone;

    }

    public Scheme subtract (Scheme scheme) {
        Scheme clone = this.clone();

        if (scheme == null || scheme.size() == 0) {
            return clone;
        }

        for (Item item: scheme.values()) {
            Item cloneItem = clone.get(item.getSequence());
            if (cloneItem == null) {
                clone.put(item.getSequence(), item.opposite());
            } else {
                clone.put(item.getSequence(), cloneItem.subtract(item));
            }
        }

        return clone;
    }

    public BigDecimal sum () {
        BigDecimal sum = BigDecimal.ZERO;
        for (Item item: this.values()) {
            sum = sum.add(item.sum());
        }
        return sum;
    }

    /** 应还金额-包含逾期和到期应还 */
    public BigDecimal shouldPay() {

        BigDecimal sum = BigDecimal.ZERO;
        for (Entry<Integer, Item> entry : this.entrySet()) {
            Item item = entry.getValue();
            if (item.getItemType() != ItemTypeEnum.PREPAY && item.getItemType() != ItemTypeEnum.FUTURE) {
                sum = sum.add(item.sum());
            }
        }

        return sum;
    }

    public BigDecimal itemFieldSum (ItemTypeEnum itemType, FieldTypeEnum fieldType) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Item item: this.values()) {
            if (item.getItemType() == itemType) {
                sum = sum.add(item.getFieldNumber(fieldType));
            }
        }
        return sum;
    }

    public BigDecimal fieldSum(FieldTypeEnum fieldType) {

        BigDecimal fieldSum = BigDecimal.ZERO;
        for (Entry<Integer, Item> entry : this.entrySet()) {
            fieldSum = fieldSum.add(entry.getValue().getFieldNumber(fieldType));
        }
        return fieldSum;
    }

    public Scheme fieldRemove(FieldTypeEnum fieldType) {
        for (Entry<Integer, Item> entry : this.entrySet()) {
            entry.getValue().remove(fieldType);
        }
        return this;
    }


    public BigDecimal schemePenalty (BigDecimal payNumber, BigDecimal dailyPenaltyRate, long date) {

        if (payNumber.compareTo(BigDecimal.ZERO) <= 0) {
            this.fieldRemove(FieldTypeEnum.PENALTY);
            return payNumber;
        }

        BigDecimal penalty = this.fieldSum(FieldTypeEnum.PENALTY);

        if (penalty.compareTo(BigDecimal.ZERO) <= 0) {
            return payNumber;
        }

        /** 支付金额不小于总罚息时，不需变更罚息明细 */
        if (payNumber.compareTo(penalty) >= 0) {
            this.penaltyBoundDate = -1L;
            this.penaltyOverpay = BigDecimal.ZERO;

            return payNumber.subtract(penalty);
        } else {

            /** 支付金额小于总罚息时，重置罚息明细，根据支付金额，重新计算明细 */
            this.fieldRemove(FieldTypeEnum.PENALTY);

            long graceRepayDate;
            long graceRepayMills = this.repayGraceDays * LoanLimitation.DAY_MILLIS;
            BigDecimal principal = BigDecimal.ZERO;
            /** 罚息结算起始日 */
            long schemeBeginDate;
            /** 罚息结算终止日 */
            long schemeEndDate;
            Item item;
            int sequence = 1;
            List<Item> schemeItems = new ArrayList<Item>();

            while (true) {

                if (payNumber.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }

                item = this.get(sequence);
                /** 跳过已还清的期数 */
                if (item == null) {
                    sequence ++;
                    continue;
                }

                /** 当，该期的宽限还款日期，小于等于当前的罚息边界日期时，累计该期本金*/
                graceRepayDate = item.getRepayDate() + graceRepayMills;
                if (graceRepayDate <= this.penaltyBoundDate) {
                    schemeItems.add(item);
                    principal = principal.add(item.getFieldNumber(FieldTypeEnum.PRINCIPAL));
                    sequence ++;
                    continue;
                }

                schemeBeginDate = this.penaltyBoundDate;
                schemeEndDate = graceRepayDate < date ? graceRepayDate : date;
                BigDecimal dailyPenalty = principal.multiply(dailyPenaltyRate);
                int schemeDays = TimeUtils.dateDiff(schemeBeginDate, schemeEndDate);
                BigDecimal schemePenalty = dailyPenalty.multiply(new BigDecimal(schemeDays));
                payNumber = payNumber.add(this.penaltyOverpay);

                if (payNumber.compareTo(schemePenalty) >= 0) {
                    this.penaltyBoundDate = schemeEndDate;
                    this.penaltyOverpay = BigDecimal.ZERO;
                    payNumber = payNumber.subtract(schemePenalty);
                } else {
                    schemeDays = payNumber.divide(dailyPenalty, BigDecimal.ROUND_DOWN).intValue();
                    schemePenalty = dailyPenalty.multiply(new BigDecimal(schemeDays));
                    this.penaltyBoundDate = this.penaltyBoundDate + schemeDays * LoanLimitation.DAY_MILLIS;
                    this.penaltyOverpay = payNumber.subtract(schemePenalty);
                    payNumber = BigDecimal.ZERO;
                }
                /** 分配罚息到每一期 */
                for (Item schemeItem: schemeItems) {
                    /** 该期分配到的罚息 */
                    BigDecimal itemSchemePenalty =  schemeItem.getFieldNumber(FieldTypeEnum.PRINCIPAL)
                            .multiply(dailyPenaltyRate).multiply(new BigDecimal(schemeDays));
                    item.setFieldNumber(FieldTypeEnum.PENALTY, item.getFieldNumber(FieldTypeEnum.PENALTY).add(itemSchemePenalty));
                }
            }
        }

        return payNumber;
    }

    /** 只能处理往期及当期的还款明细规划（不包括罚息） */
    public BigDecimal schemeField (BigDecimal payNumber, ItemTypeEnum itemType, FieldTypeEnum fieldType) throws InvalidSchemeFieldException {

        switch (fieldType) {
            case ALL_CHARGE: case INTEREST: case PRINCIPAL: {
                for (Entry<Integer, Item> schemeEntry: this.entrySet()) {

                    Item item = schemeEntry.getValue();
                    if (item.getItemType() != itemType) {
                        continue;
                    }

                    BigDecimal fieldNumber = item.getFieldNumber(fieldType);
                    if (payNumber.compareTo(BigDecimal.ZERO) == 0 || fieldNumber.compareTo(BigDecimal.ZERO) == 0) {
                        item.remove(fieldType);
                        continue;
                    }

                    if (payNumber.compareTo(fieldNumber) < 0) {
                        item.setFieldNumber(fieldType, payNumber);
                        payNumber = BigDecimal.ZERO;
                    } else {
                        payNumber = payNumber.subtract(fieldNumber);
                    }
                }
            } break;
            default: {
                throw new InvalidSchemeFieldException("不支持该域的还款规划，域：" + fieldType);
            }
        }

        return payNumber;
    }

    /** 将FUTURE类型分期, 计入提前还款项插入MAP内，并移除该项 */
    public Scheme calculatePrepayItem(int passInterestDays, BigDecimal dayInterestRate) throws MergeException {

        Scheme scheme = this.clone();
        Item prepayItem = new Item();
        Iterator<Item> itemIterator = this.values().iterator();

        /** 合并未到期分期 */
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();
            if (item.getItemType() == ItemTypeEnum.FUTURE) {
                prepayItem = prepayItem.add(item);
                scheme.remove(item.getSequence());
            }
        }

        /** 不存在未到期分期则不做处理 */
        if (prepayItem.sum().compareTo(BigDecimal.ZERO) <= 0) {
            return scheme;
        }

        /** 存在未到期分期，则配置提前还款分期的基本信息 */
        prepayItem.setItemType(ItemTypeEnum.PREPAY);
        prepayItem.setSequence(LoanLimitation.PREPAY_ITEM);
        prepayItem.setRepayDate(System.currentTimeMillis());

        /** 配置该期本金应还利息 */
        BigDecimal prepayInterest = prepayItem.getFieldNumber(FieldTypeEnum.PRINCIPAL)
                .multiply(dayInterestRate)
                .multiply(new BigDecimal(passInterestDays))
                .setScale(LoanLimitation.RESULT_SCALE, BigDecimal.ROUND_UP);
        prepayItem.setFieldNumber(FieldTypeEnum.INTEREST, prepayInterest);

        scheme.put(LoanLimitation.PREPAY_ITEM, prepayItem);

        return scheme;
    }

    public BigDecimal schemePrepay(Item prepayPayLoanItem, Item prepayCouponLoanItem, Item prepayPayStrategyItem, Item prepayCouponStrategyItem,
                                   BigDecimal payNumber, Weight[][] strategyWeightMap, Weight[][] couponWeightMap) {
        /** 提前还款全部应还 */
        Item should = this.get(LoanLimitation.PREPAY_ITEM).add(prepayPayStrategyItem).add(prepayCouponStrategyItem);
        /** 最多优惠 */
        Item coupon = prepayCouponLoanItem.add(prepayCouponStrategyItem);

        BigDecimal shouldPrincipal = should.getFieldNumber(FieldTypeEnum.PRINCIPAL);
        BigDecimal shouldInterest = should.getFieldNumber(FieldTypeEnum.INTEREST);
        BigDecimal shouldCharge = should.getFieldNumber(FieldTypeEnum.ALL_CHARGE);

        BigDecimal couponPrepayPrincipal = coupon.getFieldNumber(FieldTypeEnum.PRINCIPAL);
        BigDecimal couponPrepayInterest = coupon.getFieldNumber(FieldTypeEnum.INTEREST);
        BigDecimal couponPrepayCharge = coupon.getFieldNumber(FieldTypeEnum.ALL_CHARGE);

        /** 按照订单提前还款的各款项之间的比例，计算出，当将所有还款金额加上本金优惠项值作为本金支付时，将产生的各款项值 */
        BigDecimal maxPrepayPrincipal = payNumber.add(couponPrepayPrincipal);
        BigDecimal maxPrepayInterest = maxPrepayPrincipal.multiply(shouldInterest).divide(shouldPrincipal, LoanLimitation.RESULT_SCALE, BigDecimal.ROUND_UP);
        BigDecimal maxPrepayCharge = maxPrepayPrincipal.multiply(shouldCharge).divide(shouldPrincipal, LoanLimitation.RESULT_SCALE, BigDecimal.ROUND_UP);

        BigDecimal payPrepayPrincipal;
        BigDecimal payPrepayInterest;
        BigDecimal payPrepayCharge;

        BigDecimal realCouponInterest;
        BigDecimal realCouponCharge;

        BigDecimal entryPrepayPrincipal;
        BigDecimal entryPrepayInterest;
        BigDecimal entryPrepayCharge;

        Weight strategyPrincipal = null;
        Weight strategyInterest = null;
        Weight strategyCharge = null;
        if (strategyWeightMap != null) {
            strategyPrincipal = strategyWeightMap[ItemTypeEnum.PREPAY.getSequence()][FieldTypeEnum.PRINCIPAL.getSequence()];
            strategyInterest = strategyWeightMap[ItemTypeEnum.PREPAY.getSequence()][FieldTypeEnum.INTEREST.getSequence()];
            strategyCharge = strategyWeightMap[ItemTypeEnum.PREPAY.getSequence()][FieldTypeEnum.ALL_CHARGE.getSequence()];
        }
        if (strategyPrincipal == null) {
            strategyPrincipal = new Weight();
            strategyPrincipal.setWeightMode(WeightMode.PERCENT);
            strategyPrincipal.setWeightValue(BigDecimal.ZERO);
        }
        if (strategyInterest == null) {
            strategyInterest = new Weight();
            strategyInterest.setWeightMode(WeightMode.PERCENT);
            strategyInterest.setWeightValue(BigDecimal.ZERO);
        }
        if (strategyCharge == null) {
            strategyCharge = new Weight();
            strategyCharge.setWeightMode(WeightMode.PERCENT);
            strategyCharge.setWeightValue(BigDecimal.ZERO);
        }

        /** 比较数值间大小 */
        if (maxPrepayInterest.compareTo(couponPrepayInterest) <= 0 && maxPrepayCharge.compareTo(couponPrepayCharge) <= 0) {

            BigDecimal payStrategyPrincipal = strategyPrincipal.forward(payNumber);
            BigDecimal payLoanPrincipal = payNumber.subtract(payStrategyPrincipal);

            BigDecimal couponStrategyPrincipal = strategyPrincipal.forward(couponPrepayPrincipal);
            BigDecimal couponLoanPrincipal = couponPrepayPrincipal.subtract(couponStrategyPrincipal);
            BigDecimal couponStrategyInterest = strategyInterest.forward(maxPrepayInterest);
            BigDecimal couponLoanInterest = maxPrepayInterest.subtract(couponStrategyInterest);
            BigDecimal couponStrategyCharge = strategyCharge.forward(maxPrepayCharge);
            BigDecimal couponLoanCharge = maxPrepayCharge.subtract(couponStrategyCharge);


            prepayPayLoanItem.remove(FieldTypeEnum.INTEREST);
            prepayPayLoanItem.remove(FieldTypeEnum.ALL_CHARGE);
            prepayPayLoanItem.setFieldNumber(FieldTypeEnum.PRINCIPAL, payLoanPrincipal);

            prepayPayStrategyItem.remove(FieldTypeEnum.INTEREST);
            prepayPayStrategyItem.remove(FieldTypeEnum.ALL_CHARGE);
            prepayPayStrategyItem.setFieldNumber(FieldTypeEnum.PRINCIPAL, payStrategyPrincipal);

            prepayCouponLoanItem.setFieldNumber(FieldTypeEnum.ALL_CHARGE, couponLoanCharge);
            prepayCouponLoanItem.setFieldNumber(FieldTypeEnum.INTEREST, couponLoanInterest);
            prepayCouponLoanItem.setFieldNumber(FieldTypeEnum.PRINCIPAL, couponLoanPrincipal);

            prepayCouponStrategyItem.setFieldNumber(FieldTypeEnum.ALL_CHARGE, couponStrategyCharge);
            prepayCouponStrategyItem.setFieldNumber(FieldTypeEnum.INTEREST, couponStrategyInterest);
            prepayCouponStrategyItem.setFieldNumber(FieldTypeEnum.PRINCIPAL, couponStrategyPrincipal);

            payNumber = BigDecimal.ZERO;
        } else if (maxPrepayInterest.compareTo(couponPrepayInterest) > 0 && maxPrepayCharge.compareTo(couponPrepayCharge) <= 0) {

            payPrepayPrincipal = couponPrepayInterest.add(payNumber).multiply(shouldPrincipal)
                    .subtract(shouldInterest.multiply(couponPrepayPrincipal))
                    .divide(shouldPrincipal.add(shouldInterest), LoanLimitation.RESULT_SCALE, BigDecimal.ROUND_DOWN);
            payPrepayInterest = payNumber.subtract(payPrepayPrincipal);
            realCouponCharge = payPrepayPrincipal.add(couponPrepayPrincipal).multiply(shouldCharge).divide(shouldPrincipal, LoanLimitation.RESULT_SCALE, BigDecimal.ROUND_UP);

            BigDecimal payStrategyPrincipal = strategyPrincipal.forward(payPrepayPrincipal);
            BigDecimal payLoanPrincipal = payPrepayPrincipal.subtract(payStrategyPrincipal);
            BigDecimal payStrategyInterest= strategyInterest.forward(payPrepayInterest);
            BigDecimal payLoanInterest = payPrepayInterest.subtract(payStrategyInterest);

            BigDecimal couponStrategyPrincipal = strategyPrincipal.forward(couponPrepayPrincipal);
            BigDecimal couponLoanPrincipal = couponPrepayPrincipal.subtract(couponStrategyPrincipal);
            BigDecimal couponStrategyInterest = strategyInterest.forward(couponPrepayInterest);
            BigDecimal couponLoanInterest = couponPrepayInterest.subtract(couponStrategyInterest);
            BigDecimal couponStrategyCharge = strategyCharge.forward(realCouponCharge);
            BigDecimal couponLoanCharge = realCouponCharge.subtract(couponStrategyCharge);

            prepayPayLoanItem.remove(FieldTypeEnum.ALL_CHARGE);
            prepayPayLoanItem.setFieldNumber(FieldTypeEnum.INTEREST,payLoanInterest);
            prepayPayLoanItem.setFieldNumber(FieldTypeEnum.PRINCIPAL,payLoanPrincipal);

            prepayPayStrategyItem.remove(FieldTypeEnum.ALL_CHARGE);
            prepayPayStrategyItem.setFieldNumber(FieldTypeEnum.INTEREST,payStrategyInterest);
            prepayPayStrategyItem.setFieldNumber(FieldTypeEnum.PRINCIPAL,payStrategyPrincipal);

            prepayCouponLoanItem.setFieldNumber(FieldTypeEnum.ALL_CHARGE,couponLoanCharge);
            prepayCouponLoanItem.setFieldNumber(FieldTypeEnum.INTEREST,couponLoanInterest);
            prepayCouponLoanItem.setFieldNumber(FieldTypeEnum.PRINCIPAL,couponLoanPrincipal);

            prepayCouponStrategyItem.setFieldNumber(FieldTypeEnum.ALL_CHARGE,couponStrategyCharge);
            prepayCouponStrategyItem.setFieldNumber(FieldTypeEnum.INTEREST,couponStrategyInterest);
            prepayCouponStrategyItem.setFieldNumber(FieldTypeEnum.PRINCIPAL,couponStrategyPrincipal);

            payNumber = BigDecimal.ZERO;
        } else if (maxPrepayInterest.compareTo(couponPrepayInterest) <= 0 && maxPrepayCharge.compareTo(couponPrepayCharge) > 0) {
            payPrepayPrincipal = couponPrepayCharge.add(payNumber).multiply(shouldPrincipal)
                    .subtract(shouldCharge.multiply(couponPrepayPrincipal))
                    .divide(shouldPrincipal.add(shouldCharge), LoanLimitation.RESULT_SCALE, BigDecimal.ROUND_DOWN);
            payPrepayCharge = payNumber.subtract(payPrepayPrincipal);
            realCouponInterest = payPrepayPrincipal.add(couponPrepayPrincipal).multiply(shouldInterest).divide(shouldPrincipal, LoanLimitation.RESULT_SCALE, BigDecimal.ROUND_UP);

            BigDecimal payStrategyPrincipal = strategyPrincipal.forward(payPrepayPrincipal);
            BigDecimal payLoanPrincipal = payPrepayPrincipal.subtract(payStrategyPrincipal);
            BigDecimal payStrategyCharge= strategyCharge.forward(payPrepayCharge);
            BigDecimal payLoanCharge = payPrepayCharge.subtract(payStrategyCharge);

            BigDecimal couponStrategyPrincipal = strategyPrincipal.forward(couponPrepayPrincipal);
            BigDecimal couponLoanPrincipal = couponPrepayPrincipal.subtract(couponStrategyPrincipal);
            BigDecimal couponStrategyInterest = strategyInterest.forward(realCouponInterest);
            BigDecimal couponLoanInterest = realCouponInterest.subtract(couponStrategyInterest);
            BigDecimal couponStrategyCharge = strategyCharge.forward(couponPrepayCharge);
            BigDecimal couponLoanCharge = couponPrepayCharge.subtract(couponStrategyCharge);

            prepayPayLoanItem.setFieldNumber(FieldTypeEnum.ALL_CHARGE,payLoanCharge);
            prepayPayLoanItem.remove(FieldTypeEnum.INTEREST);
            prepayPayLoanItem.setFieldNumber(FieldTypeEnum.PRINCIPAL,payLoanPrincipal);

            prepayPayStrategyItem.setFieldNumber(FieldTypeEnum.ALL_CHARGE,payStrategyCharge);
            prepayPayStrategyItem.remove(FieldTypeEnum.INTEREST);
            prepayPayStrategyItem.setFieldNumber(FieldTypeEnum.PRINCIPAL,payStrategyPrincipal);

            prepayCouponLoanItem.setFieldNumber(FieldTypeEnum.ALL_CHARGE, couponLoanCharge);
            prepayCouponLoanItem.setFieldNumber(FieldTypeEnum.INTEREST,couponLoanInterest);
            prepayCouponLoanItem.setFieldNumber(FieldTypeEnum.PRINCIPAL,couponLoanPrincipal);

            prepayCouponStrategyItem.setFieldNumber(FieldTypeEnum.ALL_CHARGE,couponStrategyCharge);
            prepayCouponStrategyItem.setFieldNumber(FieldTypeEnum.INTEREST,couponStrategyInterest);
            prepayCouponStrategyItem.setFieldNumber(FieldTypeEnum.PRINCIPAL,couponStrategyPrincipal);

            payNumber = BigDecimal.ZERO;
        } else {
            BigDecimal entryNumber = coupon.sum().add(payNumber);
            BigDecimal orderNumber = should.sum();
            entryPrepayPrincipal = shouldPrincipal.multiply(entryNumber).divide(orderNumber, LoanLimitation.RESULT_SCALE, BigDecimal.ROUND_UP);
            if (entryPrepayPrincipal.compareTo(shouldPrincipal) > 0) {
                logger.error("还款超额，最多可支付本金:[{}]，预算可支付本金：[{}]，将按最多支付处理！",
                        shouldPrincipal.subtract(couponPrepayPrincipal), entryPrepayPrincipal.subtract(couponPrepayPrincipal));
                entryPrepayPrincipal = shouldPrincipal;
            }
            entryPrepayInterest = shouldInterest.multiply(entryPrepayPrincipal).divide(shouldPrincipal, LoanLimitation.RESULT_SCALE, BigDecimal.ROUND_UP);
            entryPrepayCharge = shouldCharge.multiply(entryPrepayPrincipal).divide(shouldPrincipal, LoanLimitation.RESULT_SCALE, BigDecimal.ROUND_UP);

            payPrepayInterest = entryPrepayInterest.subtract(couponPrepayInterest);
            payPrepayCharge = entryPrepayCharge.subtract(couponPrepayCharge);
            payPrepayPrincipal = payNumber.subtract(payPrepayInterest).subtract(payPrepayCharge);

            BigDecimal payStrategyPrincipal = strategyPrincipal.forward(payPrepayPrincipal);
            BigDecimal payLoanPrincipal = payPrepayPrincipal.subtract(payStrategyPrincipal);
            BigDecimal payStrategyInterest= strategyInterest.forward(payPrepayInterest);
            BigDecimal payLoanInterest = payPrepayInterest.subtract(payStrategyInterest);
            BigDecimal payStrategyCharge= strategyCharge.forward(payPrepayCharge);
            BigDecimal payLoanCharge = payPrepayCharge.subtract(payStrategyCharge);

            BigDecimal couponStrategyPrincipal = strategyPrincipal.forward(couponPrepayPrincipal);
            BigDecimal couponLoanPrincipal = couponPrepayPrincipal.subtract(couponStrategyPrincipal);
            BigDecimal couponStrategyInterest = strategyInterest.forward(couponPrepayInterest);
            BigDecimal couponLoanInterest = couponPrepayInterest.subtract(couponStrategyInterest);
            BigDecimal couponStrategyCharge = strategyCharge.forward(couponPrepayCharge);
            BigDecimal couponLoanCharge = couponPrepayCharge.subtract(couponStrategyCharge);

            prepayPayLoanItem.setFieldNumber(FieldTypeEnum.ALL_CHARGE,payLoanCharge);
            prepayPayLoanItem.setFieldNumber(FieldTypeEnum.INTEREST,payLoanInterest);
            prepayPayLoanItem.setFieldNumber(FieldTypeEnum.PRINCIPAL,payLoanPrincipal);

            prepayPayStrategyItem.setFieldNumber(FieldTypeEnum.ALL_CHARGE,payStrategyCharge);
            prepayPayStrategyItem.setFieldNumber(FieldTypeEnum.INTEREST,payStrategyInterest);
            prepayPayStrategyItem.setFieldNumber(FieldTypeEnum.PRINCIPAL,payStrategyPrincipal);

            prepayCouponLoanItem.setFieldNumber(FieldTypeEnum.ALL_CHARGE,couponLoanCharge);
            prepayCouponLoanItem.setFieldNumber(FieldTypeEnum.INTEREST,couponLoanInterest);
            prepayCouponLoanItem.setFieldNumber(FieldTypeEnum.PRINCIPAL,couponLoanPrincipal);

            prepayCouponStrategyItem.setFieldNumber(FieldTypeEnum.ALL_CHARGE,couponStrategyCharge);
            prepayCouponStrategyItem.setFieldNumber(FieldTypeEnum.INTEREST,couponStrategyInterest);
            prepayCouponStrategyItem.setFieldNumber(FieldTypeEnum.PRINCIPAL,couponStrategyPrincipal);

            payNumber = payNumber.subtract(should.sum());
        }

        return payNumber;

    }

    /**
     * 获取逾期天数
     * @return  逾期天数
     */
    public Integer getOverdueDays () {
        Long overdueDays = 0L;
        for (Entry<Integer, Item> itemEntry : this.entrySet()) {
            Item item = itemEntry.getValue();
            if (item.getItemType() == ItemTypeEnum.PREPAY || item.getItemType() == ItemTypeEnum.FUTURE) {
                continue;
            }
            Long repayDay = item.getRepayDate();
            Long dateTime = TimeUtils.extractDateTime(System.currentTimeMillis());
            Long diffDays = (dateTime - repayDay) / 1000 / 3600 / 24;
            if (diffDays > overdueDays) {
                overdueDays = diffDays;
            }
        }
        return overdueDays.intValue();
    }

    /**
     * 获取当前订单或当前还款的还款类型
     * @return  还款类型
     */
    public PayTypeEnum getRepaymentType () {
        PayTypeEnum payTypeEnum = PayTypeEnum.REPAY_IN_ADVANCE;
        for (Entry<Integer, Item> entry : this.entrySet()) {
            Item item = entry.getValue();
            if (ItemTypeEnum.PREVIOUS.equals(item.getItemType())) {
                // 逾期, 直接返回
                return PayTypeEnum.REPAY_OVERDUE;
            }
            if (ItemTypeEnum.PERIOD.equals(item.getItemType())) {
                payTypeEnum = PayTypeEnum.REPAY_AS_PLAN;
            }
        }
        return payTypeEnum;
    }






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
