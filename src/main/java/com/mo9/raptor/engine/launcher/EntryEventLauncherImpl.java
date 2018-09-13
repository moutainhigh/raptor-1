package com.mo9.raptor.engine.launcher;

import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.event.pay.EntryEvent;
import com.mo9.raptor.engine.event.pay.EntryResponseEvent;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.exception.LockException;
import com.mo9.raptor.lock.Lock;
import com.mo9.raptor.lock.LockStaticValues;
import com.mo9.raptor.lock.RedisService;
import com.mo9.raptor.service.IPayOrderService;
import com.mo9.raptor.utils.IDWorker;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component("entryEventLauncher")
public class EntryEventLauncherImpl implements IEventLauncher<EntryEvent> {

    private static final Logger logger = LoggerFactory.getLogger(EntryEventLauncherImpl.class);

    @Autowired
    private IPayOrderService payOrderService;

    @Autowired
    private IDWorker idWorker;

    @Autowired
    private RedisService redisService;

//    @Autowired
//    private IEventLauncher loanOrderEventLauncher;

    @Autowired
    private IEventLauncher payOrderEventLauncher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void launch (EntryEvent event) throws Exception {

        if (event == null || StringUtils.isBlank(event.getEntityUniqueId()) ) {
            throw new InvalidEventException("不是合法事件，事件=" + event);
        }
        String orderId = event.getEntityUniqueId();

        /** 还款订单防止重入已由状态机保证，此处只需防止用户互斥行为发生*/
        String lockName = orderId + LockStaticValues.ORDER_ENTRY;
        Lock lock = new Lock(lockName, idWorker.nextId()+"");
        try {
            /** 非阻塞锁，不等待竞争者释放锁，因为状态机事件，一般是互斥，当两个非互斥事件发生时，业务层需要主动捕捉LockException，重新发送事件 */
            if (redisService.lock(lock.getName(), lock.getValue(), 5000, TimeUnit.MILLISECONDS)) {

                /** 异常检查 */
                PayOrderEntity payOrder = payOrderService.getByOrderId(orderId);
                if (payOrder == null) {
                    throw new InvalidEventException("不存在该批次还款订单，还款ID:" + orderId);
                }

                /** 实际还款金额 */
                BigDecimal payNumber = payOrder.getPayNumber().subtract(payOrder.getEntryNumber());

//                LoanOrderEntity loanOrder = loanOrderService.getByOrderId(payOrder.getLoanOrderId());
//                if (loanOrder == null) {
//                    throw new InvalidEventException("不存在目标借款订单，还款批次ID:" + orderId + "，还款订单ID：" + payOrder.getOrderId() + "，借款订单ID：" + payOrder.getLoanOrderId());
//                }
//
//                /** 借款订单销账 */
//                loanOrderEventLauncher.launch(new SchemeEntryEvent(loanOrder.getOrderId(), payOrder.getType(), entryScheme));

                /** 还款订单入账 */
                payOrderEventLauncher.launch(new EntryResponseEvent(payOrder.getOrderId(), payNumber));

            } else {
                throw new LockException("还款事件：" + event.toString() +"，请求锁时竞争失败");
            }
        } finally {
            redisService.release(lock);
        }
    }
}
