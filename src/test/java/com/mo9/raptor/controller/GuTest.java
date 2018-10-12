package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mo9.raptor.RaptorApplicationTest;
import com.mo9.raptor.bean.req.BankReq;
import com.mo9.raptor.bean.res.LoanOrderLendRes;
import com.mo9.raptor.engine.utils.TimeUtils;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.service.BankService;
import com.mo9.raptor.service.CommonService;
import com.mo9.raptor.service.DingTalkService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.GatewayUtils;
import com.mo9.raptor.utils.Md5Encrypt;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.httpclient.bean.HttpResult;
import com.mo9.raptor.utils.log.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by xtgu on 2018/9/13.
 * @author xtgu
 */
@EnableAspectJAutoProxy
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RaptorApplicationTest.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GuTest {

    private static Logger logger = Log.get();

    @Autowired
    private HttpClientApi httpClientApi ;

    @Value("${loan.name.en}")
    private String loanNameEn;
    /**
     * 放款
     */
    @Test
    public void loan (){
        /**
         * 放款渠道配置 : 先玩后付 FAST_LOAN_CHANNEL  -- LOAN_CHANNEL 数据字典
         */

        int dd = 1 ;
        for (int i = 1; i <= dd; i++) {
            try {
                String notifyUrl = "https://riskclone.mo9.com/riskportal/limit/order/paymentCallBack.a";
                //	String url = "http://localhost/gateway/proxypay/pay.mhtml";
                String url = "https://new.mo9.com/gateway/proxypay/pay.mhtml";
                //String url =  "http://localhost:8081/gateway/proxypay/pay.mhtml";
                //String url = "http://guxt.local.mo9.com/gateway/proxypay/queryOrderStatus.mhtml";
                String key = "werocxofsdjnfksdf892349729lkfnnmgn/x,.zx=9=-MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJGLeWVIS3wo0U2h8lzWjiq5RJJDi14hzsbxxwedhqje123";
                Map<String, String> payParams = new HashMap<String, String>();
                payParams.put("bizSys", loanNameEn);
                Random random = new Random();
                payParams.put("invoice",  "990354"+ random.nextInt(9)+ random.nextInt(9)+ random.nextInt(9)+random.nextInt(9)+ random.nextInt(9)+ random.nextInt(9));
                payParams.put("notifyUrl", notifyUrl);
                payParams.put("cardNo","6228482938103729839");
                payParams.put("usrName", "李伟");
                payParams.put("idCard", "411221199312062149");
                payParams.put("mobile", "13560084836");
                payParams.put("openBank", "建设银行");
                payParams.put("prov", "未知");
                payParams.put("city", "未知");
                payParams.put("subBank", "建设银行");
                payParams.put("transAmt", "0.01");
                payParams.put("attach", "1490685960032");
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("loan_term", "14");
                jsonParams.put("property", "男");
                payParams.put("purpose", "FAST放款");
                payParams.put("extraParameter", jsonParams.toJSONString());
                String sign = Md5Encrypt.sign(payParams, key);
                payParams.put("sign", sign);
                String resJson = httpClientApi.doGet(url, payParams);
                //HttpResult resJson1 = httpClientApi.doGet(url, JSON.toJSONString(payParams));
                System.err.println(resJson);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    @Autowired
    private BankService bankService ;
    @Autowired
    private UserService userService ;
    /**
     * 银行卡相关 - 接口
     */
    @Test
    public void bank (){
        String bankNo = "6217001210032584250" ;
        String cardId = "320684199109100052" ;
        String userName = "顾晓桐" ;
        String mobile = "13916393513" ;

        BankReq bankReq = new BankReq();
        bankReq.setCardStartCount(1);
        bankReq.setCardSuccessCount(1);
        bankReq.setCardFailCount(1);
        bankReq.setCard(bankNo);
        bankReq.setBankName("xxxxx");
        bankReq.setCardMobile(mobile);
        UserEntity userEntity = userService.findByUserCodeAndDeleted("1122",false);
        // bankService.verify( bankReq , userEntity, userCertifyInfoEntity);
    }

    /**
     * 银行卡相关 - web
     */
    @Test
    public void bankWeb (){

        try {
            String bankNo = "6217001210032584250" ;
            String cardId = "320684199109100052" ;
            String userName = "顾晓桐" ;
            String mobile = "13916393513" ;
            JSONObject json = new JSONObject() ;
            json.put("card" , bankNo);
            json.put("" , cardId);
            json.put("cardName" , userName);
            json.put("cardMobile" , mobile);
            String url = "http://localhost/raptorApi/auth/modify_bank_card_info";
            HttpResult resJson = httpClientApi.doPostJson(url, json.toJSONString());
            System.out.println(111);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private GatewayUtils gatewayUtils ;
    /**
     * 查询先玩后付订单状态 - 本地订单号查询 invoice
     */
    @Test
    public void getOrderMsg (){
        LoanOrderLendRes orderMsg = gatewayUtils.getOrderMsg("990354214320");
        System.out.println(JSONObject.toJSONString(orderMsg, SerializerFeature.PrettyFormat));
    }

    /**
     * mq重发
     */
    @Test
    public void test2(){
        Map<String, String> payParams = new HashMap<String, String>();
        payParams.put("mo9mq", "20170918_mo9mq_gu");
        String sign = Md5Encrypt.sign(payParams, "gateway_mo9mq_repair_task");
        payParams.put("sign", sign) ;
        String url = "https://new.mo9.com/gateway/mo9mq/repairMq.mhtml";
        try {
            httpClientApi.doGet(url, payParams);
        } catch (Exception e) {
        }
    }

    /**
     * 查询产品信息
     */
    @Test
    public void orderQuotaList(){
        //https://riskclone.mo9.com/raptorApi/loan_quota/list
        //https://riskclone.mo9.com/raptorApi/system/switch
        //https://riskclone.mo9.com/raptorApi/system/common_task
    }

    /**
     * 查询产品信息
     */
    @Autowired
    private CommonService commonService ;

    @Autowired
    private DingTalkService dingTalkService ;

    @Test
    public void common(){
        /*Map<String , Integer> commonUserInfo = commonService.findUserInfo("ssss") ;
        Map<String , Integer> loanInfo = commonService.findLoanInfo("ssss") ;
        Map<String , Integer> repayInfo = commonService.findRepayInfo("ssss");

        *//*dingTalkService.sendText(" 用户总数 :  " + commonUserInfo.get("userNumber") + "\n 今日登陆用户数 : " + commonUserInfo.get("userLoginNumber")
                + "\n身份证认证总数 : " + commonUserInfo.get("userCardNumber") + "\n通话记录认证总数 : " + commonUserInfo.get("userPhoneNumber")
                + "\n通讯录认证总数 : " + commonUserInfo.get("userCallHistoryNumber") + "\n银行卡认证总数 : " + commonUserInfo.get("userBankNumber")
                + "\n今日放款限额 : " + loanInfo.get("maxAmount") + "\n今日放款总数 : " + loanInfo.get("loanNumber")
                + "\n今日放款总金额 : " + loanInfo.get("loanAmount") + "\n今日还款总数 : " + repayInfo.get("repayNumber")
                + "\n今日还款金额 : " + repayInfo.get("repayAmount") + "\n今日延期总数 : " + repayInfo.get("postponeNumber")
                + "\n今日延期金额 : " + repayInfo.get("postponeAmount") + "\n逾期单量 : " + repayInfo.get("overdueNumber"));*//*


        Log.error(logger , new RuntimeException("xxx") , "测试");*/

        Long date = TimeUtils.extractDateTime(System.currentTimeMillis());

        Long time = TimeUtils.extractDateTime(System.currentTimeMillis())/1000 ;
        logger.info("系统定时器开启");
        Map<String , Integer> commonUserInfo = commonService.findUserInfo("ssss") ;
        Map<String , Integer> loanInfo = commonService.findLoanInfo("ssss") ;
        Map<String , Integer> repayInfo = commonService.findRepayInfo(time);

        System.out.println("22222");
        /*Calendar calendar = Calendar.getInstance();
        System.out.println("顾晓桐" + calendar.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        System.out.println("顾晓桐" + calendar.getTimeInMillis());
        System.out.println("顾晓桐" + sdf.format(calendar.getTime()));
        System*/

        /*System.out.println("顾晓桐" + date);
        System.out.println("顾晓桐" + System.currentTimeMillis());
        System.out.println("顾晓桐" + t1);
        System.out.println("顾晓桐" + t2);
        System.out.println("顾晓桐" + t3);
        System.out.println(sdf.format(new Date()));*/
    }



}
