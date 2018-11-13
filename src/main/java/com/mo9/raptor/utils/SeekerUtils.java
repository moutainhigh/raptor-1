package com.mo9.raptor.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.io.netty.handler.timeout.ReadTimeoutException;
import com.mo9.raptor.bean.res.LoanOrderLendRes;
import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.entity.CardBinInfoEntity;
import com.mo9.raptor.entity.PayOrderLogEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.service.CardBinInfoService;
import com.mo9.raptor.service.PayOrderLogService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xtgu on 2018/9/12.
 * @author xtgu
 * 先玩后付相关util
 */
@Component
public class SeekerUtils {

    private static Logger logger = Log.get();

    /**
     * 支付中心地址
     */
    @Value("${seeker.url}")
    private String seekerUrl ;

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

    @Resource
    private CardBinInfoService cardBinInfoService;

    @Value("${loan.name.en}")
    private String loanNameEn ;

    @Value("${loan.name.cn}")
    private String loanNameCn ;

    /**
     * 放款
     * @return
     */
    public ResCodeEnum loan(LendOrderEntity lendOrder){
        String method = "/proxypay/pay.mhtml" ;
        String key = "werocxofsdjnfksdf892349729lkfnnmgn/x,.zx=9=-MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJGLeWVIS3wo0U2h8lzWjiq5RJJDi14hzsbxxwedhqje123";
        Map<String, String> payParams = new HashMap<String, String>();
        payParams.put("bizSys", loanNameEn);
        //订单号
        payParams.put("invoice",  lendOrder.getApplyUniqueCode());
        payParams.put("notifyUrl", ""); //使用mq，则可以不传？
        payParams.put("cardNo",lendOrder.getBankCard()); // 银行卡
        payParams.put("usrName", lendOrder.getUserName()); //姓名
        payParams.put("idCard", lendOrder.getIdCard()); //身份证
        payParams.put("mobile", lendOrder.getBankMobile()); //手机号
        payParams.put("openBank", lendOrder.getBankName()); // 银行名称
        payParams.put("transAmt", lendOrder.getApplyNumber().toPlainString()); // 金额
        payParams.put("attach", lendOrder.getOrderId()); //同invoice
        payParams.put("purpose", "猛禽放款");

        String sign = Md5Encrypt.sign(payParams, key);
        payParams.put("sign", sign);
        try {
            String resJson = httpClientApi.doGet(seekerUrl + method, payParams);
            JSONObject jsonObject = JSONObject.parseObject(resJson);
            String status = jsonObject.getString("status");
            if ("failed".equals(status)) {
                logger.info("订单[{}]放款, 渠道返回同步失败, 返回信息  [{}]", lendOrder.getOrderId(), resJson);
            } else {
                logger.info("订单[{}]放款, 渠道返回同步返回信息  [{}]", lendOrder.getOrderId(), resJson);
            }
            String invoice = jsonObject.getString("invoice");
            lendOrder.setUpdateTime(System.currentTimeMillis());
            lendOrder.setChannelSyncResponse(resJson);
            lendOrder.setDealCode(invoice);
            lendOrderService.save(lendOrder);
        } catch (ReadTimeoutException e) {
            logger.error( "订单[{}]放款异常 - ", lendOrder.getOrderId() , e);
            return ResCodeEnum.EXCEPTION_CODE;
        }catch (SocketTimeoutException e) {
            logger.error( "订单[{}]放款异常 - ", lendOrder.getOrderId() , e);
            return ResCodeEnum.EXCEPTION_CODE;
        }catch (Exception e) {
            Log.error(logger , e , "订单[{}]放款异常 - ", lendOrder.getOrderId());
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

            /**
             * addition	string
             非必须
             订单附加信息
             allowAddCard	boolean
             必须
             是否允许新加银行卡
             allowChangeCard	boolean
             必须
             是否允许换卡
             amount	number
             必须
             收款金额(单位: 元)
             bankCards	object []
             必须
             银行卡列表, 包含默认银行卡
             item 类型: object

             businessCode	string
             必须
             业务单号
             currency	string
             必须
             币种, 默认CNY
             defaultCard	object
             必须
             默认银行卡
             备注: 默认银行卡

             purpose	string
             必须
             交易目的
             */

            String mysig = Md5Encrypt.sign(params, "643138394F10DA5E9647709A3FA8DD7F");
            params.put("sign", mysig);
            String gatewayUrl = this.seekerUrl + "/pay.shtml";
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
            Log.error(logger , e , "还款订单[{}]还款报错", payOrderLog.getPayOrderId());
            resCodeEnum = ResCodeEnum.EXCEPTION_CODE;
        }
        return resCodeEnum;
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
            String result = httpClientApi.doGet(seekerUrl + method , params) ;
            LoanOrderLendRes loanOrderLendRes = JSON.parseObject(result, LoanOrderLendRes.class);
            if ("success".equals(loanOrderLendRes.getStatus()) || "failed".equals(loanOrderLendRes.getStatus())) {
                return loanOrderLendRes;
            } else {
                logger.error("查询放款订单状态报错, 返回为: [{}]", result);
            }
        } catch (Exception e) {
            logger.error("查询先玩后付放款订单[{}]状态报错 ", orderNo, e);
        }
        return null;
    }



    /**
     * 触发先玩后付mq
     * @return
     */
    public void gatewayMqPush(String gatewayDealcode){
        try {
            logger.info(gatewayDealcode + "触发定时器发送") ;
            Map<String,String> params = new HashMap<String,String>(10);
            params.put("m", "topupRemoteChecking");
            params.put("dealcode", gatewayDealcode);
            String gatewayUrl = this.seekerUrl + "/pay.shtml";
            String reeult = httpClientApi.doGet(gatewayUrl, params);
            logger.info(gatewayDealcode + "查询还款返回数据 " + reeult);
        } catch (Exception e) {
            logger.error("触发先玩后付mq异常"+ gatewayDealcode , e);
        }
    }


}
