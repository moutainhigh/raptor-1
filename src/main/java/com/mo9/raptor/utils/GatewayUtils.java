package com.mo9.raptor.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by xtgu on 2018/9/12.
 * @author xtgu
 * 先玩后付相关util
 */
@Component
public class GatewayUtils {

    private static final Logger logger = LoggerFactory.getLogger(GatewayUtils.class);

    /**
     * 先玩后付地址
     */
    @Value("${gateway.url}")
    private String gatewayUrl ;

    @Autowired
    private HttpClientApi httpClientApi ;

    /**
     * 放款
     * @return
     */
    public ResCodeEnum loan(){
        //TODO 参数需要填充
        String method = "/proxypay/pay.mhtml" ;
        String key = "werocxofsdjnfksdf892349729lkfnnmgn/x,.zx=9=-MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJGLeWVIS3wo0U2h8lzWjiq5RJJDi14hzsbxxwedhqje123";
        Map<String, String> payParams = new HashMap<String, String>();
        payParams.put("bizSys", "RAPTOR");
        Random random = new Random();
        //订单号
        payParams.put("invoice",  "990354"+ random.nextInt(9)+ random.nextInt(9)+ random.nextInt(9)+random.nextInt(9)+ random.nextInt(9)+ random.nextInt(9));
        payParams.put("notifyUrl", ""); // 用snc传递成功使用地址
        payParams.put("cardNo","6228482938103729839"); // 银行卡
        payParams.put("usrName", "李伟"); //姓名
        payParams.put("idCard", "411221199312062149"); //身份证
        payParams.put("mobile", "13560084836"); //手机号
        payParams.put("openBank", "建设银行"); // 银行名称
        payParams.put("prov", "未知"); // 默认
        payParams.put("city", "未知"); // 默认
        payParams.put("subBank", "建设银行");
        payParams.put("transAmt", "0.01"); // 金额
        payParams.put("attach", "1490685960032"); //同invoice
        JSONObject jsonParams = new JSONObject();
        jsonParams.put("loan_term", "14");
        jsonParams.put("property", "男");
        payParams.put("purpose", "FAST放款");//自定义中文
        payParams.put("extraParameter", jsonParams.toJSONString());
        String sign = Md5Encrypt.sign(payParams, key);
        payParams.put("sign", sign);
        try {
            String resJson = httpClientApi.doGet(gatewayUrl + method, payParams);
        } catch (Exception e) {
            logger.error("放款异常 - ");
        }
        //TODO
        return ResCodeEnum.SUCCESS ;
    }

    /**
     * 还款
     * @return
     */
    public ResCodeEnum payoff(){
        //TODO
        return ResCodeEnum.SUCCESS ;
    }

    /**
     * 检查银行卡四要素
     * @return
     */
    public ResCodeEnum verifyBank(String bankNo , String cardId , String userName , String mobile){
        String method = "/proxypay/verifyBank.mhtml" ;
        Map<String,String> params = new HashMap<String,String>(10);
        params.put("bankNo" , bankNo) ;
        params.put("cardId" , cardId) ;
        params.put("userName" , userName) ;
        params.put("mobile" , mobile) ;
        try {
            String result = httpClientApi.doGet(gatewayUrl + method , params) ;
            logger.info(bankNo + " - " + cardId + " - " + userName + " - " + mobile + " 银行卡四要素验证返回参数 " + result);
            if(result == null){
                return ResCodeEnum.BANK_VERIFY_EXCEPTION ;
            }
            JSONObject data = JSON.parseObject(result) ;
            if(data.get("status") == null){
                //渠道验证超时
                return ResCodeEnum.BANK_VERIFY_EXCEPTION ;
            }
            Boolean status = data.getBoolean("status") ;
            if(status){
                return ResCodeEnum.SUCCESS ;
            }else{
                return ResCodeEnum.BANK_VERIFY_ERROR ;
            }
        } catch (Exception e) {
            logger.error(bankNo + " - " + cardId + " - " + userName + " - " + mobile + " 银行卡四要素验证 异常" , e);
            return ResCodeEnum.BANK_VERIFY_EXCEPTION ;
        }
    }

    /**
     * 同步先玩后付用户 - 手机号
     * @param mobile
     * @return
     */
    public ResCodeEnum syncUserByMobile(String mobile){
        //TODO
        return ResCodeEnum.SUCCESS ;
    }


}
