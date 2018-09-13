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

import java.util.HashMap;
import java.util.Map;

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
                return ResCodeEnum.BANK_VERIFY_EXCPTION ;
            }
            JSONObject data = JSON.parseObject(result) ;
            if(data.get("status") == null){
                //渠道验证超时
                return ResCodeEnum.BANK_VERIFY_EXCPTION ;
            }
            Boolean status = data.getBoolean("status") ;
            if(status){
                return ResCodeEnum.SUCCESS ;
            }else{
                return ResCodeEnum.BANK_VERIFY_ERROR ;
            }
        } catch (Exception e) {
            logger.error(bankNo + " - " + cardId + " - " + userName + " - " + mobile + " 银行卡四要素验证 异常" , e);
            return ResCodeEnum.BANK_VERIFY_EXCPTION ;
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
