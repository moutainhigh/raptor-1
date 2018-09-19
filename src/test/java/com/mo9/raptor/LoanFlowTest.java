package com.mo9.raptor;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mo9.raptor.bean.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoanFlowTest {

    private static final Logger logger = LoggerFactory.getLogger(LoanFlowTest.class);

    public static void main(String[] args) {

        String address = "http://localhost:8081/raptorApi";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Account-Code", "AA20A480E526D644D13D9AC5593D268E");
        headers.add("client-id", "503");
        headers.add("content-type", "application/json; charset=UTF-8");

        try {

            /** 借贷订单下单 */
            String orderRear = "/order/add";
            String orderUrl = address + orderRear;

            Map<String, String> params = new HashMap<String, String>();
            params.put("capital", "1000");
            params.put("period", "7");
            HttpEntity<String> requestEntity = new HttpEntity<String>(JSONObject.toJSONString(params), headers);
            ResponseEntity<BaseResponse> result = new RestTemplate().exchange(orderUrl, HttpMethod.POST, requestEntity, BaseResponse.class);
            BaseResponse body = result.getBody();
            logger.info("返回结果:"+ JSONObject.toJSONString(body, SerializerFeature.PrettyFormat));


            /** 查询最近借贷订单 */
            orderRear = "/order/get_last_incomplete";
            orderUrl = address + orderRear;

            requestEntity = new HttpEntity<String>(null, headers);
            result = new RestTemplate().exchange(orderUrl, HttpMethod.GET, requestEntity, BaseResponse.class);
            body = result.getBody();
            logger.info("返回结果:"+ JSONObject.toJSONString(body, SerializerFeature.PrettyFormat));

            /** 发起支付 */
            String api = "/cash/repay";
            String url = address + api;
            params = new HashMap<String, String>();
            String loanOrderId = ((LinkedHashMap<String, String>) body.getData()).get("orderId");
            params.put("loanOrderId", loanOrderId);
            requestEntity = new HttpEntity<String>(JSONObject.toJSONString(params), headers);
            result = new RestTemplate().exchange(url, HttpMethod.POST, requestEntity, BaseResponse.class);
            body = result.getBody();
            logger.info("返回结果:"+ JSONObject.toJSONString(body, SerializerFeature.PrettyFormat));

        } catch (Exception e) {
            logger.error("错误", e);
        }
    }

}
