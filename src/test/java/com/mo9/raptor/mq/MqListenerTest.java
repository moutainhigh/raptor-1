package com.mo9.raptor.mq;

import com.alibaba.fastjson.JSONObject;
import com.mo9.mqclient.MqMessage;
import com.mo9.raptor.RaptorApplicationTest;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.mq.listen.LoanMo9mqListener;
import com.mo9.raptor.utils.IDWorker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xzhang on 2018/9/15.
 */
@EnableAspectJAutoProxy
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RaptorApplicationTest.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MqListenerTest {

    @Autowired
    private ILoanOrderService loanOrderService;

    @Autowired
    private IPayOrderService payOrderService;

    @Autowired
    private LoanMo9mqListener loanMo9mqListener;

    @Autowired
    private IDWorker idWorker;

    /**
     * 模拟放款mq
     */
    @Test
    public void mockLend () {

        LoanOrderEntity order = loanOrderService.getLastIncompleteOrder("123");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("orderId", order.getOrderId());
        //params.put("lendAmount", order.getLoanNumber().subtract(order.getChargeValue()));
        params.put("lendAmount", 100);
        params.put("status", "1");
        params.put("lendSettleTime", System.currentTimeMillis());
        params.put("channelResponse", JSONObject.toJSONString(params));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("remark", params);

        MqMessage message = new MqMessage("TOPIC", "MQ_RAPTOR_LOAN_TAG", jsonObject.toJSONString());
        loanMo9mqListener.consume(message, null);
    }

    /**
     * 模拟还款mq
     */
    @Test
    public void mockRepay () {

        List<String> statuses = new ArrayList<String>();
        statuses.add(StatusEnum.DEDUCTING.name());
        PayOrderEntity payOrderEntity = payOrderService.listByUserAndStatus("123", statuses).get(0);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("status", "success");
        params.put("channel", "先玩后付");
        params.put("amount", payOrderEntity.getApplyNumber());
        //params.put("amount", 100);
        params.put("dealcode", idWorker.nextId());
        params.put("channelDealcode", idWorker.nextId());
        params.put("orderId", payOrderEntity.getOrderId());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("remark", params);

        MqMessage message = new MqMessage("TOPIC", "MQ_RAPTOR_PAYOFF_TAG", jsonObject.toJSONString());
        loanMo9mqListener.consume(message, null);
    }


}
