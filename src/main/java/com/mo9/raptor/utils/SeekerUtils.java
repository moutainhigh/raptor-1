package com.mo9.raptor.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.io.netty.handler.timeout.ReadTimeoutException;
import com.mo9.raptor.bean.req.BankSeekerReq;
import com.mo9.raptor.bean.res.LoanOrderLendRes;
import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.entity.BankEntity;
import com.mo9.raptor.entity.CardBinInfoEntity;
import com.mo9.raptor.entity.PayOrderLogEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.service.BankService;
import com.mo9.raptor.service.CardBinInfoService;
import com.mo9.raptor.service.PayOrderLogService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.httpclient.bean.HttpResult;
import com.mo9.raptor.utils.log.Log;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Value("${raptor.sockpuppet}")
    private String sockpuppet;

    /**
     * signKey
     */
    @Value("${seeker.sign.key}")
    private String signKey ;

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
            /*订单附加信息*/
            JSONObject addition = new JSONObject() ;
            /*是否允许新加银行卡*/
            Boolean allowAddCard = true ;
            /*金额 元*/
            BigDecimal amount = payOrderLog.getRepayAmount() ;
            /*业务单号*/
            String businessCode = payOrderLog.getPayOrderId() ;
            /*币种*/
            String currency = "CNY";

            /*交易目的*/
            String purpose = "猛禽支付" ;
            /*返回地址, 即支付完成之后调用的返回地址*/
            String returnUrl = "http://www.tiantianyouqian.com/repay_finish" ;

            List<BankEntity> bankEntityList = bankService.findByUserCode(payOrderLog.getUserCode()) ;
            /*银行卡列表, 包含默认银行卡 list*/
            List<BankSeekerReq> bankCards = setBankReq(bankEntityList);
            /*默认银行卡*/
            BankSeekerReq defaultCard = new BankSeekerReq() ;
            if(bankEntityList != null && bankEntityList.size() > 0){
                defaultCard = setBankReq(bankEntityList.get(0));
            }

            JSONObject params = new JSONObject();
            params.put("addition", addition.toJSONString());
            params.put("allowAddCard", allowAddCard);
            params.put("amount", amount);
            params.put("bankCards", bankCards);
            params.put("businessCode", businessCode);
            params.put("currency", currency);
            params.put("defaultCard", defaultCard);
            params.put("purpose", purpose);
            params.put("returnUrl", returnUrl);

            Map<String,String> headers = this.setHeadMap(params.toJSONString());
            String url = this.seekerUrl + "/api/seeker/v1/remit/create_remit_h5";
            logger.info("请求参数" + params.toJSONString());
            HttpResult result = httpClientApi.doPostJson(url , params.toJSONString() , headers) ;
            if(result != null ){
                logger.info("请求支付中心还款返回数据 code = " + result.getCode() + " data = " + result.getData());
                if( result.getCode() == 200){
                    String resJson = result.getData();
                    JSONObject jsonObject = JSONObject.parseObject(resJson);
                    String code = jsonObject.getString("code");
                    String dealCode = null;
                    if ("0".equals(code)) {
                        logger.info("还款订单[{}]还款请求发送成功, 返回为[{}]", payOrderLog.getPayOrderId(), resJson);
                        dealCode = jsonObject.getJSONObject("data").getString("orderCode");
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
                }else{
                    resCodeEnum = ResCodeEnum.EXCEPTION_CODE;
                }
            }else{
                resCodeEnum = ResCodeEnum.EXCEPTION_CODE;
                logger.info("请求支付中心失败 还款返回数据 null") ;
            }

        } catch (Exception e) {
            Log.error(logger , e , "还款订单[{}]还款报错", payOrderLog.getPayOrderId());
            resCodeEnum = ResCodeEnum.EXCEPTION_CODE;
        }
        return resCodeEnum;
    }

    /**
     * 封装请求银行卡参数
     * @param bankEntityList
     */
    private List<BankSeekerReq> setBankReq(List<BankEntity> bankEntityList) {
        if(bankEntityList == null || bankEntityList.size() == 0){
            return new ArrayList<BankSeekerReq>() ;
        }
        List<BankSeekerReq> returnList = new ArrayList<BankSeekerReq>() ;
        for(BankEntity bankEntity : bankEntityList){
            BankSeekerReq bankSeekerReq = this.setBankReq(bankEntity);
            returnList.add(bankSeekerReq) ;
        }

        return returnList ;
    }

    /**
     * 封装请求银行卡参数
     * @param bankEntity
     */
    private BankSeekerReq setBankReq(BankEntity bankEntity) {
        if(bankEntity == null){
            return null ;
        }
        BankSeekerReq bankSeekerReq = new BankSeekerReq() ;
        bankSeekerReq.setBankCardNo(bankEntity.getBankNo());
        bankSeekerReq.setBankMobile(bankEntity.getMobile());
        bankSeekerReq.setIdCard(bankEntity.getCardId());
        bankSeekerReq.setRealName(bankEntity.getUserName());
        bankSeekerReq.setBankOfDeposit(bankEntity.getBankName());
        return bankSeekerReq ;
    }

    /**
     * 封装头信息
     * @param paramJson
     * @return
     */
    private Map<String,String> setHeadMap(String paramJson){
        Map<String,String> headers = new HashMap<String,String>() ;
        Long time = System.currentTimeMillis() ;
        headers.put("Timestamp" , time.toString());
        headers.put("App-Code" , sockpuppet);
        headers.put("sign" , DigestUtils.md5Hex(paramJson + time + signKey));
        return headers ;
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
     * 触发支付中心mq
     * @return
     */
    public void gatewayMqPush(String gatewayDealcode){
        try {
            logger.info(gatewayDealcode + "触发定时器发送") ;
            JSONObject params = new JSONObject();
            params.put("orderCode", gatewayDealcode);
            Map<String,String> headers = this.setHeadMap(params.toJSONString());
            String url = this.seekerUrl + "/trade/fetch_remit_order?orderCode=" + gatewayDealcode;
            logger.info("请求参数" + params.toJSONString());
            String result = httpClientApi.doGetByHeader(url, headers);
            logger.info(gatewayDealcode + "查询还款返回数据 " + result);
        } catch (Exception e) {
            logger.error("触发支付中心mq异常"+ gatewayDealcode , e);
        }
    }


}
