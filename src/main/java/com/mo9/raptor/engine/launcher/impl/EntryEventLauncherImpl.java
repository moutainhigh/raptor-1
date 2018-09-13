package com.mo9.raptor.engine.launcher.impl;

import com.mo9.libracredit.engine.calculator.LoanCalculatorFactory;
import com.mo9.libracredit.engine.calculator.loan.ILoanCalculator;
import com.mo9.libracredit.engine.entity.LoanOrderEntity;
import com.mo9.libracredit.engine.entity.PayOrderEntity;
import com.mo9.libracredit.engine.enums.FieldTypeEnum;
import com.mo9.libracredit.engine.enums.PayTypeEnum;
import com.mo9.libracredit.engine.enums.StatusEnum;
import com.mo9.libracredit.engine.event.entry.EntryEvent;
import com.mo9.libracredit.engine.event.order.loan.LiquidateWaveBrokeEvent;
import com.mo9.libracredit.engine.event.order.loan.SchemeEntryEvent;
import com.mo9.libracredit.engine.event.order.pay.EntryResponseEvent;
import com.mo9.libracredit.engine.exception.*;
import com.mo9.libracredit.engine.launcher.IEventLauncher;
import com.mo9.libracredit.engine.service.*;
import com.mo9.libracredit.engine.simulator.ClockFactory;
import com.mo9.libracredit.engine.structure.*;
import com.mo9.libracredit.engine.structure.condition.ConditionValue;
import com.mo9.libracredit.engine.structure.condition.Mode;
import com.mo9.libracredit.engine.structure.condition.Scope;
import com.mo9.libracredit.engine.structure.condition.Situation;
import com.mo9.libracredit.lock.Lock;
import com.mo9.libracredit.lock.RedisService;
import com.mo9.libracredit.service.PayOrderDetailService;
import com.mo9.libracredit.util.IDWorker;
import com.mo9.libracredit.util.upload.LockStaticValues;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component("entryEventLauncher")
public class EntryEventLauncherImpl implements IEventLauncher<EntryEvent> {

    private static final Logger logger = LoggerFactory.getLogger(EntryEventLauncherImpl.class);

    @Autowired
    private ILoanOrderService loanOrderService;

    @Autowired
    private IPayOrderService payOrderService;

    @Autowired
    private IEventLauncher loanOrderEventLauncher;

    @Autowired
    private IEventLauncher payOrderEventLauncher;

    @Autowired
    private PayOrderDetailService payOrderDetailService;

    @Autowired
    private LoanCalculatorFactory loanCalculatorFactory;

    @Autowired
    private RedisService redisService;

    @Autowired
    private IDWorker idWorker;

    @Autowired
    private IBillService billService;

    @Override
    public void launch (EntryEvent event) throws Exception {

        if (event == null || StringUtils.isBlank(event.getBatchId()) ) {
            throw new InvalidEventException("不是合法事件，事件=" + event);
        }

        List<PayOrderEntity>  payOrders;
        LoanOrderEntity loanOrder;
        /** 还款订单防止重入已由状态机保证，此处只需防止用户互斥行为发生*/
        String lockName = event.getBatchId() + LockStaticValues.USER_BATCH_ENTRY;
        Lock lock = new Lock(lockName, idWorker.nextId()+"");
        try {
            /** 非阻塞锁，不等待竞争者释放锁，因为状态机事件，一般是互斥，当两个非互斥事件发生时，业务层需要主动捕捉LockException，重新发送事件 */
            if (redisService.lock(lock.getName(), lock.getValue(), 5000, TimeUnit.MILLISECONDS)) {

                /** 异常检查 */
                payOrders = payOrderService.listByBatchId(event.getBatchId());
                if (payOrders == null || payOrders.size() == 0) {
                    throw new InvalidEventException("不存在该批次还款订单，还款批次ID:" + event.getBatchId());
                }

                for (PayOrderEntity payOrder: payOrders) {
                    loanOrder = loanOrderService.getByOrderId(payOrder.getLoanOrderId());
                    if (loanOrder == null) {
                        throw new InvalidEventException("不存在目标借款订单，还款批次ID:" + event.getBatchId() + "，还款订单ID：" + payOrder.getOrderId() + "，借款订单ID：" + payOrder.getLoanOrderId());
                    }

                    /** 实际还款金额 */
                    BigDecimal payNumber = payOrder.getAnchorNumber().subtract(payOrder.getEntryNumber());

                    /** 配置约束条件 */
                    Situation situation = new Situation();
                    situation.put(Scope.PAY_CURRENCY, new ConditionValue(Mode.VALUE, payOrder.getPayCurrency()));
                    situation.put(Scope.LOAN_CURRENCY, new ConditionValue(Mode.VALUE, loanOrder.getLoanCurrency()));

                    /** 生成订单原始账单明细 */
                    ILoanCalculator loanCalculator = loanCalculatorFactory.load(loanOrder);
                    Scheme realScheme = loanCalculator.realScheme(ClockFactory.clockTime(loanOrder.getOwnerId()), loanOrder);

                    /** 计算入账明细 */
                    SchemeEntryMap schemeMap = billService.schemeMap(event.getUserCode(), loanOrder.getOrderId(), realScheme,
                            payOrder.getOrderId(), payNumber, situation, loanOrder.getDailyPenaltyRate(), ClockFactory.clockTime(event.getUserCode()));

                    Scheme entryScheme = schemeMap.get(EntryEnum.PAY_LOAN).add(schemeMap.get(EntryEnum.COUPON_LOAN));
                    /** 借款订单销账 */
                    loanOrderEventLauncher.launch(new SchemeEntryEvent(loanOrder.getOrderId(), payOrder.getType(), entryScheme));

                    /** 还款订单入账 */
                    payOrderEventLauncher.launch(new EntryResponseEvent(payOrder.getOrderId(), schemeMap.get(EntryEnum.PAY_LOAN).add(schemeMap.get(EntryEnum.PAY_STRATEGY)).sum()));

                    /** 拆分服务费明细 */
                    for (Scheme scheme : schemeMap.values()) {
                        for (Map.Entry<Integer, Item> entry: scheme.entrySet()) {
                            Item item = entry.getValue();
                            Field chargeField = item.get(FieldTypeEnum.ALL_CHARGE);
                            if (chargeField != null && chargeField.getNumber() != null) {
                                Map<FieldTypeEnum, BigDecimal> chargeMap = loanOrder.getChargeMap(chargeField.getNumber());
                                for (Map.Entry<FieldTypeEnum, BigDecimal> chargeEntry: chargeMap.entrySet()) {
                                    Field newField = chargeField.clone();
                                    newField.setNumber(chargeEntry.getValue());
                                    item.put(chargeEntry.getKey(), newField);
                                }
                            }
                        }
                    }

                    /** 明细保存 */
                    payOrderDetailService.saveScheme(payOrder, realScheme, schemeMap.values());
                }

                // 波动清偿后置操作
                PayTypeEnum payTypeEnum = event.getPayType();
                if (payTypeEnum.equals(PayTypeEnum.LIQUIDATE_BY_WAVE_AND_BROKE)) {
                    // 获取用户所有借款中订单, 置为失败
                    List<LoanOrderEntity> loanOrderEntities = loanOrderService.listUserOrderByStatuses(event.getUserCode(), StatusEnum.LENT_LIST);
                    if (loanOrderEntities != null && loanOrderEntities.size() > 0) {
                        for (LoanOrderEntity loanOrderEntity : loanOrderEntities) {
                            loanOrderEventLauncher.launch(new LiquidateWaveBrokeEvent(loanOrderEntity.getOrderId(), payTypeEnum.name()));
                        }
                    }
                }
            } else {
                throw new LockException("还款事件：" + event.toString() +"，请求锁时竞争失败");
            }

            /** 通知风控清偿完成 */
            payOrderService.notifyRepaySuccess(event.getUserCode(), event.getBatchId(), event.getPayType());
        } finally {
            redisService.release(lock);
        }
    }
}
