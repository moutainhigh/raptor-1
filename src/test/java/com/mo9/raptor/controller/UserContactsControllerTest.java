package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.RaptorApplicationTest;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.httpclient.bean.HttpResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 */
@EnableAspectJAutoProxy
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RaptorApplicationTest.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserContactsControllerTest {

    private static  final String localHostUrl = "http://localhost/raptorApi/";

    @Autowired
    private HttpClientApi httpClientApi;

    Map<String, String> headers = new HashMap<>();
    @Before
    public void before(){
        headers.put("Account-Code", "AA20A480E526D644D13D9AC5593D268E");
        headers.put("Client-Id", "503");
    }

    @Test
    public void testSubmitMobileContacts() throws IOException {
        //你好
        JSONObject json = new JSONObject();
        json.put("data", "111");
        HttpResult httpResult = httpClientApi.doPostJson(localHostUrl + "/user/submit_mobile_contacts", json.toJSONString(), headers);
        System.out.println(httpResult.getCode());
        System.out.println(httpResult.getData());
    }
}
