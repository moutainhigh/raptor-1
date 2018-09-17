package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.RaptorApplicationTest;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.httpclient.bean.HttpResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@EnableAspectJAutoProxy
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RaptorApplicationTest.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LoanOrderControllerTest {
    @Autowired
    private HttpClientApi httpClientApi;

    private static final String baseUrl = "http://localhost/raptorApi/order/";

    /** 借贷订单下单 */
    @Test
    public void add () {
        try {
            String path = "add";

            JSONObject json = new JSONObject();
            json.put("capital", 1000);
            json.put("period", 7);

            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Account-Code","TEST-WU");

            HttpResult resJson = httpClientApi.doPostJson(baseUrl+path, json.toJSONString(), headers);
            System.out.println(resJson.getCode());
            System.out.println(resJson.getData());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getLastIncomplete () {

        try {
            String path = "get_last_incomplete";

            String resJson = httpClientApi.doGet(baseUrl+path);
            System.out.println(resJson);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
