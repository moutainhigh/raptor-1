package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.RaptorApplicationTest;
import com.mo9.raptor.service.BankService;
import com.mo9.raptor.utils.GatewayUtils;
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
 * Created by ycheng on 2018/9/16.
 *
 * @author ycheng
 */
@EnableAspectJAutoProxy
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RaptorApplicationTest.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @Autowired
    private HttpClientApi httpClientApi;

    @Autowired
    private BankService bankService;

    @Autowired
    private GatewayUtils gatewayUtils;

    private static final String localUrl = "http://192.168.14.114:8010/raptorApi/";

    private static final String localHostUrl = "http://localhost/raptorApi/";

    private static final String cloneHostUrl = "https://riskclone.mo9.com/raptorApi/";

    Map<String, String> headers = new HashMap<>();

    @Before
    public void before() {
        headers.put("Account-Code", "AA20A480E526D644D13D9AC559392926");
        headers.put("Access-Token", "42f26facf5df4424b33f10fc34f1f290");
        headers.put("Client-Id", "503");
    }

    /**
     * 发送登录短信验证码
     */
    @Test
    public void sendCode() {

        try {
            String mobile = "13213173513";
            JSONObject json = new JSONObject();
            json.put("mobile", mobile);
            String url = "auth/send_login_code";
            HttpResult resJson = httpClientApi.doPostJson(localHostUrl + url, json.toJSONString());
            System.out.println(resJson.getCode());
            System.out.println(resJson.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户验证码登录
     */
    @Test
    public void signIn() {

        try {
            String mobile = "13213173513";
            JSONObject json = new JSONObject();
            json.put("mobile", mobile);
            json.put("code", "019350");
            String url = "user/login_by_code";
            HttpResult resJson = httpClientApi.doPostJson(localHostUrl + url, json.toJSONString());
            System.out.println(resJson.getCode());
            System.out.println(resJson.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取账户审核状态
     */
    @Test
    public void auditStatus() {
        try {
            String url = "user/get_audit_status";
            //Map<String, String> header = new HashMap<>();
            //header.put("Account-Code","asdfkalsdkff");
            String resJson = httpClientApi.doGetByHeader(cloneHostUrl + url, headers);
            System.err.println(resJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     修改账户身份认证信息
     */
    /**
     提交手机通讯录
     */
    /**
     * 修改账户银行卡信息
     */
    @Test
    public void modifyBankCard() {

        try {
            JSONObject json = new JSONObject();
            // json.put("cardName", "程暘");
            json.put("bankName", "招商银行");
            json.put("cardMobile", "13564546025");
            json.put("card", "6226090216324281");
            json.put("cardStartCount", 2); //银行卡扫描开始计数
            json.put("cardSuccessCount", 1); //银行卡扫描成功计数
            json.put("cardFailCount", 1); //银行卡扫描失败计数

            String url = "user/modify_bank_card_info";
            HttpResult resJson = httpClientApi.doPostJson(cloneHostUrl + url, json.toJSONString(), headers);
            System.out.println(resJson.getCode());
            System.out.println(resJson.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登出
     */
    @Test
    public void testLogout() throws IOException {
        HttpResult httpResult = httpClientApi.doPostJson(cloneHostUrl + "/user/logout", null, headers);
        System.out.println(httpResult.getCode());
        System.out.println(httpResult.getData());
    }

    /**
     * 提交认证信息
     *
     * @throws IOException
     */
    @Test
    public void testModifyCertifyInfo() throws IOException {
        JSONObject json = new JSONObject();
        json.put("realName", "程暘");
        json.put("idCard", "310115199011182510");
        json.put("issuingOrgan", "宇宙中心黄浦区公安局");
        json.put("validityStartPeriod", "2010-1-1");
        json.put("validityEndPeriod", "2010-1-1");
        json.put("type", 0);
        json.put("accountFrontImg", "https://1111.com");
        json.put("accountBackImg", "https://1111.com");
        json.put("accountOcr", "https://1111.com");
        json.put("ocrRealName", "程暘");
        json.put("ocrIdCard", "310115199011182510");
        json.put("ocrIssueAt", "银月城派出所");
        json.put("ocrDurationStartTime", "2010-1-1");
        json.put("ocrDurationEndTime", "2010-1-1");
        json.put("ocrGender", 0);
        json.put("ocrNationality", "血精灵");
        json.put("ocrBirthday", "1992-05-01");
        json.put("ocrIdCardAddress", "艾泽拉斯银月城");
        json.put("frontStartCount", 5); //身份证正面扫描开始计数【新】
        json.put("frontSuccessCount", 1);//身份证正面扫描成功计数【新】
        json.put("frontFailCount", 4); //身份证正面扫描失败计数【新】
        json.put("backStartCount", 3); //身份证背面扫描开始计数【新】
        json.put("backSuccessCount", 1); //身份证背面扫描成功计数【新】
        json.put("backFailCount", 2); //身份证背面扫描失败计数【新】
        json.put("livenessStartCount", 3); //活体扫描开始计数【新】
        json.put("livenessSuccessCount", 1);  //活体扫描成功计数【新】
        json.put("livenessFailCount", 1);//活体扫描失败计数【新】

        HttpResult httpResult = httpClientApi.doPostJson(cloneHostUrl + "/user/modify_certify_info", json.toJSONString(), headers);
        System.out.println(httpResult.getCode());
        System.out.println(httpResult.getData());
    }


    /**
     * 告知服务通话记录已上传
     */
    @Test
    public void phoneRecordUploaded() {

        try {
            JSONObject json = new JSONObject();

            String url = "user/phone_record_uploaded";
            String resJson  = httpClientApi.doGetByHeader(cloneHostUrl + url, headers);
            System.out.println(resJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
