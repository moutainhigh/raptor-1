package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mo9.raptor.utils.Md5Encrypt;
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

import static org.junit.Assert.*;

public class CouponControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(CouponControllerTest.class);

    @Test
    public void create() {
        // String address = "http://riskclone.mo9.com/raptorApi";
        String address = "http://localhost/raptorApi";
        String orderRear = "/coupon/create";
        String orderUrl = address + orderRear;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Account-Code", "123");
        headers.add("client-id", "503");
        headers.add("content-type", "application/json; charset=UTF-8");

        Map<String, String> params = new HashMap<String, String>();
        params.put("number", "377");
        params.put("bundleId", "TTYQ-236515888038285312");
        params.put("creator", "123");
        params.put("reason", "YOUHUI");

        String sign = Md5Encrypt.sign(params, "0123456789");
        params.put("sign", sign);
        HttpEntity<String> requestEntity = new HttpEntity<String>(JSONObject.toJSONString(params), headers);
        ResponseEntity<String> result = new RestTemplate().exchange(orderUrl, HttpMethod.POST, requestEntity, String.class);
        logger.info("返回结果:"+ JSONObject.toJSONString(result.getBody(), SerializerFeature.PrettyFormat));


    }

    @Test
    public void update() {
    }
}