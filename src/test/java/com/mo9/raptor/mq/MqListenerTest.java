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
     * 模拟放款mq
     */
    @Test
    public void mockLend2 () {

        String params = "{\"channelResponse\":\"<YLPMSGBEAN><VERSION>2.1<\\/VERSION><MSG_TYPE>100001<\\/MSG_TYPE><BATCH_NO>MO9PAYPDAFERAOCLNQEOOK<\\/BATCH_NO><USER_NAME>18616705830<\\/USER_NAME><TRANS_STATE>0000<\\/TRANS_STATE><MSG_SIGN><\\/MSG_SIGN><TRANS_DETAILS><TRANS_DETAIL><SN>2604441<\\/SN><BANK_CODE><\\/BANK_CODE><ACC_NO>6214832135845811<\\/ACC_NO><ACC_NAME>周成龙<\\/ACC_NAME><ACC_PROVINCE><\\/ACC_PROVINCE><ACC_CITY><\\/ACC_CITY><AMOUNT>0.01<\\/AMOUNT><MOBILE_NO><\\/MOBILE_NO><PAY_STATE>0000<\\/PAY_STATE><BANK_NO><\\/BANK_NO><BANK_NAME>招商银行<\\/BANK_NAME><ACC_TYPE>00<\\/ACC_TYPE><ACC_PROP>0<\\/ACC_PROP><ID_TYPE>0<\\/ID_TYPE><ID_NO>31011519880509063X<\\/ID_NO><CNY>CNY<\\/CNY><EXCHANGE_RATE><\\/EXCHANGE_RATE><SETT_AMOUNT><\\/SETT_AMOUNT><USER_LEVEL><\\/USER_LEVEL><SETT_DATE><\\/SETT_DATE><REMARK>交易成功<\\/REMARK><RESERVE><\\/RESERVE><RETURN_URL><\\/RETURN_URL><MER_ORDER_NO>PDAFERAOCLNQEOOK<\\/MER_ORDER_NO><MER_SEQ_NO><\\/MER_SEQ_NO><TRANS_DESC>猛禽放款<\\/TRANS_DESC><SUCCESS_DATE>2018-09-19 16:48:01<\\/SUCCESS_DATE><QUERY_NO_FLAG><\\/QUERY_NO_FLAG><SMS_CODE><\\/SMS_CODE><\\/TRANS_DETAIL><\\/TRANS_DETAILS><\\/YLPMSGBEAN>\",\"lendAmount\":0.01,\"lendChannelType\":\"\",\"status\":\"1\",\"lendSettleTime\":1537346881558,\"bankIdcardNo\":\"31011519880509063X\",\"bankMobile\":\"13120502501\",\"bankName\":\"招商银行\",\"eventType\":1301,\"lendChannel\":\"yilianpay\",\"operator\":\"gateway\",\"bankCardNo\":\"6214832135845811\",\"eventId\":\"PDAFERAOCLNQEOOK\",\"eventTime\":1537346881579,\"lendReqpTime\":1537346878699,\"lendId\":\"PDAFERAOCLNQEOOK\",\"bankUserName\":\"周成龙\",\"orderId\":\"SMALL-WHITE-MOUSE-227108215912009728\",\"bankBranch\":\"未知\"}";

        MqMessage message = new MqMessage("TOPIC", "MQ_RAPTOR_LOAN_TAG", params);
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
