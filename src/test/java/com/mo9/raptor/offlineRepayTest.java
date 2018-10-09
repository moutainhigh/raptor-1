package com.mo9.raptor;

import com.mo9.raptor.controller.LoanOrderControllerTest;
import com.mo9.raptor.utils.Md5Encrypt;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
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
        String userCode = "123";
        String type = "REPAY";
        BigDecimal amount = new BigDecimal("750");
        String accessUserCode = "0E85007DC2B3852AD5EF198763049E83";

        Map<String, String> signParams = new HashMap<String, String>();
        signParams.put("userCode", userCode);
        signParams.put("type", type);
        signParams.put("amount", amount.toPlainString());
        signParams.put("accessUserCode", accessUserCode);
        String resultSign = Md5Encrypt.sign(signParams, "mo9123456");
        signParams.put("sign", resultSign);

        String url = "http://localhost/raptorApi/test/offline_repay";
        try {
            String result = httpClientApi.doGet(url, signParams);
            logger.info(result);
        } catch (Exception e) {
            logger.error("错误 ", e);
        }
    }
}
