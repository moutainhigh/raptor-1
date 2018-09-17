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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class LoanOrderControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(LoanOrderControllerTest.class);

    @Test
    public void add() {
        try {
            String address = "http://localhost/raptorApi";
            String orderRear = "/order/add";
            String orderUrl = address + orderRear;
            HttpHeaders headers = new HttpHeaders();
            headers.add("Account-Code", "123");
            headers.add("client-id", "503");
            headers.add("content-type", "application/json; charset=UTF-8");

            Map<String, String> params = new HashMap<String, String>();
            params.put("capital", "1000");
            params.put("period", "7");
            HttpEntity<String> requestEntity = new HttpEntity<String>(JSONObject.toJSONString(params), headers);
            ResponseEntity<BaseResponse> result = new RestTemplate().exchange(orderUrl, HttpMethod.POST, requestEntity, BaseResponse.class);
            BaseResponse body = result.getBody();
            logger.info("返回结果:"+ JSONObject.toJSONString(body, SerializerFeature.PrettyFormat));
        } catch (Exception e) {
            logger.error("错误", e);
        }
    }

    @Test
    public void getLastIncomplete() {
        try {
            String address = "http://localhost/raptorApi";
            String orderRear = "/order/get_last_incomplete";
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

    private String buildGetUrl(String methodUri, Map<String, String> request) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append(methodUri);
        if (!request.isEmpty()) {
            sb.append("?");
            Set<Map.Entry<String, String>> entrySet = request.entrySet();
            Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(entry.getValue());
                if (iterator.hasNext()) {
                    sb.append("&");
                }
            }
        }
        return sb.toString();
    }
}