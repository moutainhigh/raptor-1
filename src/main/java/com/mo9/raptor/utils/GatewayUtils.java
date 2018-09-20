package com.mo9.raptor.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.res.LoanOrderLendRes;
import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.entity.BankEntity;
import com.mo9.raptor.entity.PayOrderLogEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.service.BankService;
import com.mo9.raptor.service.PayOrderLogService;
import com.mo9.raptor.service.UserService;
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
    private UserService userService;

    @Autowired
    private IPayOrderService payOrderService;

    @Autowired
    private PayOrderLogService payOrderLogService;

    @Autowired
    private ILendOrderService lendOrderService;

    @Autowired
    private HttpClientApi httpClientApi ;

    @Autowired
    private BankService bankService ;

    /**
     * 放款
     * @return
     */
    public ResCodeEnum loan(LendOrderEntity lendOrder){
        String method = "/proxypay/pay.mhtml" ;
        String key = "werocxofsdjnfksdf892349729lkfnnmgn/x,.zx=9=-MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJGLeWVIS3wo0U2h8lzWjiq5RJJDi14hzsbxxwedhqje123";
        Map<String, String> payParams = new HashMap<String, String>();
        payParams.put("bizSys", "RAPTOR");
        //订单号
        payParams.put("invoice",  lendOrder.getApplyUniqueCode());
        payParams.put("notifyUrl", ""); //使用mq，则可以不传？
        payParams.put("cardNo",lendOrder.getBankCard()); // 银行卡
        payParams.put("usrName", lendOrder.getUserName()); //姓名
        payParams.put("idCard", lendOrder.getIdCard()); //身份证
        payParams.put("mobile", lendOrder.getBankMobile()); //手机号
        payParams.put("openBank", lendOrder.getBankName()); // 银行名称
        //payParams.put("prov", "未知"); // 默认
        //payParams.put("city", "未知"); // 默认
        //payParams.put("subBank", "建设银行");
        payParams.put("transAmt", lendOrder.getApplyNumber().toPlainString()); // 金额
        payParams.put("attach", lendOrder.getOrderId()); //同invoice
        /*JSONObject jsonParams = new JSONObject();
        jsonParams.put("loan_term", "14");
        jsonParams.put("property", "男");
        payParams.put("purpose", "FAST放款");//自定义中文
        payParams.put("extraParameter", jsonParams.toJSONString());*/
        payParams.put("purpose", "猛禽放款");

        String sign = Md5Encrypt.sign(payParams, key);
        payParams.put("sign", sign);
        try {
            //String gatewayUrl = "http://ycheng.local.mo9.com/gateway";
            String resJson = httpClientApi.doGet(gatewayUrl + method, payParams);
            JSONObject jsonObject = JSONObject.parseObject(resJson);
            String status = jsonObject.getString("status");
            if ("failed".equals(status)) {
                logger.info("订单[{}]放款, 渠道返回同步失败, 返回信息  [{}]", lendOrder.getOrderId(), resJson);
            } else {
                logger.info("订单[{}]放款, 渠道返回同步返回信息  [{}]", lendOrder.getOrderId(), resJson);
            }
            lendOrder.setUpdateTime(System.currentTimeMillis());
            lendOrder.setChannelSyncResponse(resJson);
            lendOrderService.save(lendOrder);
        } catch (Exception e) {
            logger.error("订单[{}]放款异常 - ", lendOrder.getOrderId(), e);
            return ResCodeEnum.EXCEPTION_CODE;
        }
        return ResCodeEnum.SUCCESS ;
    }

    /**
     * 还款
     * @return
     */
    public ResCodeEnum payoff(PayOrderLogEntity payOrderLog){
        ResCodeEnum resCodeEnum;
        try {
            Map<String,String> params = new HashMap<String,String>();
            params.put("m", "newPayGu");
            params.put("channel", payOrderLog.getChannel());
            params.put("subchannel", payOrderLog.getChannel());
            params.put("amount", payOrderLog.getRepayAmount().toString());
            UserEntity user = userService.findByUserCode(payOrderLog.getUserCode());
            params.put("mobile", user.getMobile());
            PayOrderEntity payOrderEntity = payOrderService.getByOrderId(payOrderLog.getPayOrderId());
            BankEntity bankEntity = bankService.findByBankNo(payOrderLog.getBankCard());
            //orderId : 订单号;
            params.put("remark", "FASTRAPTOR_" + payOrderEntity.getOrderId() + "_" + payOrderEntity.getLoanOrderId() + "_" + bankEntity.getBankName());

            params.put("userMobile", user.getMobile());
            params.put("bankmobile", payOrderLog.getBankMobile());
            params.put("userName",payOrderLog.getUserName());
            params.put("bankCard", payOrderLog.getBankCard());
            params.put("idCard", payOrderLog.getIdCard());
            params.put("orderDesc", "猛禽支付");

            String mysig = Md5Encrypt.sign(params, "643138394F10DA5E9647709A3FA8DD7F");
            params.put("sign", mysig);
            String gatewayUrl = this.gatewayUrl + "/pay.shtml";
            String resJson = httpClientApi.doGet(gatewayUrl, params);


            JSONObject jsonObject = JSONObject.parseObject(resJson);
            String code = jsonObject.getString("code");
            String dealCode = null;
            if ("0000".equals(code)) {
                logger.info("还款订单[{}]还款请求发送成功, 返回为[{}]", payOrderLog.getPayOrderId(), resJson);
                dealCode = jsonObject.getJSONObject("data").getString("dealcode");
                resCodeEnum = ResCodeEnum.SUCCESS;
            } else {
                logger.info("还款订单[{}]还款请求发送失败, 返回为[{}]", payOrderLog.getPayOrderId(), resJson);
                // return ResCodeEnum.SUCCESS;
                resCodeEnum = ResCodeEnum.EXCEPTION_CODE;
            }
            payOrderLog.setChannelSyncResponse(resJson);
            payOrderLog.setUpdateTime(System.currentTimeMillis());
            payOrderLog.setDealCode(dealCode);
            payOrderLogService.save(payOrderLog);
        } catch (Exception e) {
            logger.error("还款订单[{}]还款报错", payOrderLog.getPayOrderId(), e);
            resCodeEnum = ResCodeEnum.EXCEPTION_CODE;
        }
        return resCodeEnum;
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

    /**
     * 查询先玩后付订单状态 - 信息
     * @param orderNo -- 本地放款订单号
     */
    public LoanOrderLendRes getOrderMsg(String orderNo){
        String method = "/proxypay/queryOrderStatus.mhtml";
        Map<String,String> params = new HashMap<String,String>(10);
        params.put("invoice" , orderNo) ;
        try {
            String result = httpClientApi.doGet(gatewayUrl + method , params) ;
            LoanOrderLendRes loanOrderLendRes = JSON.parseObject(result, LoanOrderLendRes.class);
            if ("success".equals(loanOrderLendRes.getStatus())) {
                return loanOrderLendRes;
            } else {
                logger.error("查询放款订单状态报错, 返回为: [{}]", result);
            }
//            JSONObject data = JSON.parseObject(result) ;
//            String status = data.getString("status") ; //请求状态 success 请求成功 failed 请求失败
//            String description = data.getString("description") ;   //请求返回信息
//            String invoice = data.getString("invoice") ;  //订单号
//            String dealcode = data.getString("dealcode") ; //先玩后付订单号
//            String orderStatus = data.getString("orderStatus") ;  //订单状态
//            String amount = data.getString("amount") ; //金额(分)
//            String statusTime = data.getString("statusTime") ;  // 状态时间
//            String channel = data.getString("channel") ;  //渠道
//            String thirdDealcode = data.getString("thirdDealcode") ;  //第三方订单号
            //TODO 后续操作
        } catch (Exception e) {
            logger.error("查询先玩后付放款订单[{}]状态报错 ", orderNo, e);
        }
        return null;
    }

}
