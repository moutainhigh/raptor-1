package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mo9.mqclient.MqMessage;
import com.mo9.raptor.RaptorApplicationTest;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.repository.LoanOrderRepository;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.mq.listen.LoanMo9mqListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@EnableAspectJAutoProxy
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RaptorApplicationTest.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoanOrderControllerTestFull {

    private static final Logger logger = LoggerFactory.getLogger(LoanOrderControllerTestFull.class);


    @Autowired
    private ILoanOrderService loanOrderService;

    @Autowired
    private LoanMo9mqListener loanMo9mqListener;

    /**
     * 批量下单
     */
    @Test
    public void batchAdd() {
        int i = 10;
        try {
            String address = "http://localhost/raptorApi";
            String orderRear = "/order/add";
            String orderUrl = address + orderRear;
            for (int i1 = 1; i1 <= i; i1++) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Account-Code", i1 + "");
                headers.add("client-id", "503");
                headers.add("content-type", "application/json; charset=UTF-8");

                Map<String, String> params = new HashMap<String, String>();
                params.put("capital", "1000");
                params.put("period", "14");
                HttpEntity<String> requestEntity = new HttpEntity<String>(JSONObject.toJSONString(params), headers);
                ResponseEntity<BaseResponse> result = new RestTemplate().exchange(orderUrl, HttpMethod.POST, requestEntity, BaseResponse.class);
                BaseResponse body = result.getBody();
                logger.info("返回结果:"+ JSONObject.toJSONString(body, SerializerFeature.PrettyFormat));
            }
        } catch (Exception e) {
            logger.error("错误", e);
        }
    }

    /**
     * 批量放款
     */
    @Test
    public void batchMockLend () {

        for (int i1 = 1; i1 <= 20; i1++) {
            LoanOrderEntity order = loanOrderService.getLastIncompleteOrder(i1 + "", StatusEnum.PROCESSING);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("orderId", order.getOrderId());
            params.put("lendAmount", order.getLoanNumber().subtract(order.getChargeValue()));
            //params.put("lendAmount", 100);
            params.put("status", "1");
            params.put("lendSettleTime", System.currentTimeMillis());
            params.put("channelResponse", JSONObject.toJSONString(params));

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("remark", params);

            MqMessage message = new MqMessage("TOPIC", "MQ_RAPTOR_LOAN_TAG", jsonObject.toJSONString());
            loanMo9mqListener.consume(message, null);
        }
    }



}