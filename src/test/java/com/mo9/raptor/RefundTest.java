package com.mo9.raptor;

import com.mo9.raptor.utils.Md5Encrypt;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 退款测试类
 * Created by xzhang on 2018/10/8.
 */
@EnableAspectJAutoProxy
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RaptorApplicationTest.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RefundTest {

    private static final Logger logger = LoggerFactory.getLogger(RefundTest.class);

    @Autowired
    private HttpClientApi httpClientApi;

    /**
     * 先玩后付地址
     */
    @Value("${gateway.url}")
    private String gatewayUrl ;

    private static final String KEY = "werocxofsdjnfksdf892349729lkfnnmgn/x,.zx=9=-MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJGLeWVIS3wo0U2h8lzWjiq5RJJDi14hzsbxxwedhqje123";

    @Test
    public void refund () {

        ////订单号
        String invoice = "TTYQ-232081008260956160";
        //银行卡号
        String cardNo = "6217000130005611016";
        //姓名
        String usrName = "吴宇森";
        //银行名称
        String openBank = "建设银行";
        //身份证号
        String idCard = "130105199307161819";
        //金额元
        String transAmt = "237";
        //手机号
        String mobile = "13733319395";
        //订单号
        String attach = invoice;
        Map<String, String> payParams = new HashMap<String, String>();
        payParams.put("bizSys", "KFTK");  //渠道
        payParams.put("invoice",  invoice); //订单号
        payParams.put("notifyUrl", ""); //回调地址
        payParams.put("cardNo", cardNo);
        payParams.put("usrName", usrName);
        payParams.put("openBank", openBank);
        payParams.put("idCard", idCard);
        payParams.put("transAmt", transAmt);
        payParams.put("attach", attach);
        payParams.put("mobile", mobile);
        payParams.put("purpose", "天天有钱退款");
        String sign = Md5Encrypt.sign(payParams, KEY);
        payParams.put("sign", sign);

        // 线上
        // String url = "https://www.mo9.com/gateway/proxypay/pay.mhtml";
        // 克隆
        String url = "https://new.mo9.com/gateway/proxypay/pay.mhtml";

        try {
            String result = httpClientApi.doGet(url, payParams);
            logger.info("退款申请发起代付请求返回响应结果,订单号={},response={}", invoice, result);
        } catch (Exception e) {
            logger.error("请求先玩后付退款报错: ", e);
        }
    }
}
