package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mo9.raptor.bean.BaseResponse;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class PayOrderControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(PayOrderControllerTest.class);

    @Test
    public void repay() {
        try {
            String address = "http://localhost/raptorApi";
            String orderRear = "/cash/repay";
            String orderUrl = address + orderRear;
            HttpHeaders headers = new HttpHeaders();
            headers.add("Account-Code", "123");
            headers.add("client-id", "503");
            headers.add("content-type", "application/json; charset=UTF-8");

            Map<String, String> params = new HashMap<String, String>();
            params.put("channelType", "1");
            params.put("bankCard", "112312312");
            params.put("bankMobile", "13212143421");
            params.put("userName", "xxx");
            params.put("idCard", "21423421321421312");
            params.put("orderId", "21421321421421321");
            HttpEntity<String> requestEntity = new HttpEntity<String>(JSONObject.toJSONString(params), headers);
            ResponseEntity<BaseResponse> result = new RestTemplate().exchange(orderUrl, HttpMethod.POST, requestEntity, BaseResponse.class);
            BaseResponse body = result.getBody();
            logger.info("返回结果:"+ JSONObject.toJSONString(body, SerializerFeature.PrettyFormat));
        } catch (Exception e) {
            logger.error("错误", e);
        }
    }

    @Test
    public void renewal() {
        try {
            String address = "http://localhost/raptorApi";
            String orderRear = "/cash/renewal";
            String orderUrl = address + orderRear;
            HttpHeaders headers = new HttpHeaders();
            headers.add("Account-Code", "AA20A480E526D644D13D9AC5593D268E");
            headers.add("client-id", "503");
            headers.add("content-type", "application/json; charset=UTF-8");

            Map<String, String> params = new HashMap<String, String>();
            params.put("channelType", "1");
            params.put("bankCard", "112312312");
            params.put("bankMobile", "13212143421");
            params.put("userName", "xxx");
            params.put("idCard", "21423421321421312");
            params.put("period", "7");
            params.put("orderId", "SMALL-WHITE-MOUSE-226026477836177408");
            HttpEntity<String> requestEntity = new HttpEntity<String>(JSONObject.toJSONString(params), headers);
            ResponseEntity<BaseResponse> result = new RestTemplate().exchange(orderUrl, HttpMethod.POST, requestEntity, BaseResponse.class);
            BaseResponse body = result.getBody();
            logger.info("返回结果:"+ JSONObject.toJSONString(body, SerializerFeature.PrettyFormat));
        } catch (Exception e) {
            logger.error("错误", e);
        }
    }

    @Test
    public void getRepayChannels() {
        try {
            String address = "http://localhost/raptorApi";
            String orderRear = "/cash/get_repay_channels";
            String orderUrl = address + orderRear;
            HttpHeaders headers = new HttpHeaders();
            headers.add("Account-Code", "123");
            headers.add("client-id", "503");
            headers.add("content-type", "application/json; charset=UTF-8");

            HttpEntity<String> requestEntity = new HttpEntity<String>(null, headers);
            ResponseEntity<BaseResponse> result = new RestTemplate().exchange(orderUrl, HttpMethod.GET, requestEntity, BaseResponse.class);
            BaseResponse body = result.getBody();
            logger.info("返回结果:"+ JSONObject.toJSONString(body, SerializerFeature.PrettyFormat));
        } catch (Exception e) {
            logger.error("错误", e);
        }
    }
}