package com.mo9.raptor;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.controller.LoanOrderControllerTest;
import com.mo9.raptor.utils.Md5Encrypt;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.httpclient.bean.HttpResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xzhang on 2018/9/30.
 */
@EnableAspectJAutoProxy
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RaptorApplicationTest.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class offlineRepayTest {

    private static final Logger logger = LoggerFactory.getLogger(offlineRepayTest.class);


    @Autowired
    private HttpClientApi httpClientApi;

    @Test
    public void offlineRepay() {
        String orderId = "TTYQ-234692470460518400";
        String userCode = "123";
        String type = "REPAY";
        //String type = "POSTPONE";
        BigDecimal amount = new BigDecimal("1000");
        String creator = "张轩";
        String reliefReason = "11";

        Map<String, String> signParams = new HashMap<String, String>();
        signParams.put("orderId", orderId);
        signParams.put("userCode", userCode);
        signParams.put("type", type);
        signParams.put("amount", amount.toPlainString());
        signParams.put("creator", creator);
        signParams.put("reliefReason", reliefReason);
        String resultSign = Md5Encrypt.sign(signParams, "TWlBfbVtgmJb6tlYeWuTl2N26xtKT5SX");
        signParams.put("sign", resultSign);

        String url = "http://localhost/raptorApi/offline/repay";
        //String url = "https://www.mo9.com/raptorApi/test/offline_repay";
        try {
            HttpResult httpResult = httpClientApi.doPostJson(url, JSONObject.toJSONString(signParams));
            logger.info(httpResult.getData());
        } catch (Exception e) {
            logger.error("错误 ", e);
        }
    }
}
