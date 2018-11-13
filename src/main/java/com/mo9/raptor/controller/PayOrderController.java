package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.mqclient.MqAction;
import com.mo9.mqclient.MqMessage;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.ReqHeaderParams;
import com.mo9.raptor.bean.req.LoanOrderRenewal;
import com.mo9.raptor.bean.req.LoanOrderRepay;
import com.mo9.raptor.bean.req.PayInfoCache;
import com.mo9.raptor.bean.res.ChannelDetailRes;
import com.mo9.raptor.bean.res.PayOderChannelRes;
import com.mo9.raptor.engine.entity.CouponEntity;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.BillService;
import com.mo9.raptor.engine.service.CouponService;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.entity.*;
import com.mo9.raptor.enums.*;
import com.mo9.raptor.mq.listen.LoanMo9mqListener;
import com.mo9.raptor.redis.RedisParams;
import com.mo9.raptor.redis.RedisServiceApi;
import com.mo9.raptor.service.*;
import com.mo9.raptor.utils.CommonValues;
import com.mo9.raptor.utils.IDWorker;
import com.mo9.raptor.utils.log.Log;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 还款
 * Created by xzhang on 2018/9/13.
 */
@Controller
@RequestMapping("/cash")
public class PayOrderController {

    private static Logger logger = Log.get();
    @Autowired
    private IDWorker idWorker;

    @Autowired
    private UserService userService;

    @Autowired
    private UserCertifyInfoService userCertifyInfoService;

    @Autowired
    private IPayOrderService payOrderService;

    @Autowired
    private PayOrderLogService payOrderLogService;

    @Autowired
    private ILoanOrderService loanOrderService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private BankService bankService;

    @Autowired
    private BillService billService;

    @Autowired
    private CashAccountService cashAccountService ;

    @Autowired
    private CouponService couponService ;

    @Resource
    private RedisServiceApi redisServiceApi;

    @Autowired
    private LoanMo9mqListener loanMo9mqListener;

    @Resource(name = "raptorRedis")
    private RedisTemplate raptorRedis;

    @Value("${raptor.sockpuppet}")
    private String sockpuppet;

    /**
     * 支付版本号
     */
    @Value("${raptor.pay.version}")
    private String raptorPayVersion;

    @GetMapping("/download")
    public String download () {
        return "cashier/download_guide";
    }

    /**
     * 发起支付
     * @return
     */
    @PostMapping("/repay")
    @ResponseBody
    public BaseResponse<JSONObject> repay(@Valid @RequestBody LoanOrderRepay req, HttpServletRequest request) {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        String loanOrderId = req.getLoanOrderId();
        String couponId = req.getCouponId();
        BigDecimal balance = req.getBalance();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        String clientId = request.getHeader(ReqHeaderParams.CLIENT_ID);
        String clientVersion = request.getHeader(ReqHeaderParams.CLIENT_VERSION);
        logger.info("repay方法开始,userCode:"+userCode+",loanOrderId:"+loanOrderId);

        try{
            // 用户没删就行, 拉黑也能还
            //UserEntity user = userService.findByUserCodeAndDeleted(userCode, false);
            UserCertifyInfoEntity userCertifyInfoEntity = userCertifyInfoService.findByUserCode(userCode);

            if (userCertifyInfoEntity == null) {
                return response.buildFailureResponse(ResCodeEnum.USER_NOT_EXIST);
            }

            // 获得订单
            LoanOrderEntity loanOrder = loanOrderService.getByOrderId(loanOrderId);
            if (loanOrder == null || !StatusEnum.LENT.name().equals(loanOrder.getStatus())) {
                return response.buildFailureResponse(ResCodeEnum.ILLEGAL_LOAN_ORDER_STATUE);
            }
            // 检查用户
            if (!loanOrder.getOwnerId().equals(userCode)) {
                return response.buildFailureResponse(ResCodeEnum.ILLEGAL_REPAYMENT);
            }

            Item shouldPayItem = billService.payoffShouldPayItem(loanOrder);
            BigDecimal shouldPayAmount = shouldPayItem.sum() ;

            JSONObject data = new JSONObject();

            String code = String.valueOf(idWorker.nextId());

            PayInfoCache payInfoCache = new PayInfoCache();
            payInfoCache.setUserCode(userCode);
            payInfoCache.setLoanOrderId(loanOrderId);
            payInfoCache.setPayType(shouldPayItem.getRepaymentType().name());
            payInfoCache.setPeriod(0);
            payInfoCache.setUserName(userCertifyInfoEntity.getRealName());
            payInfoCache.setIdCard(userCertifyInfoEntity.getIdCard());
            payInfoCache.setClientId(clientId);
            payInfoCache.setClientVersion(clientVersion);
            //判断优惠券是否存在
            if(couponId != null){
                CouponEntity couponEntity = couponService.getByCouponId(couponId);
                if(couponEntity.getStatus().equals(StatusEnum.PENDING.name()) && couponEntity.getExpireDate() > System.currentTimeMillis()){
                    payInfoCache.setCouponId(couponId);
                    shouldPayAmount = shouldPayAmount.subtract(couponEntity.getApplyAmount());
                }else{
                    //钱包余额不够
                    response.setCode(ResCodeEnum.COUPON_IS_EXPIRY.getCode());
                    response.setMessage(ResCodeEnum.COUPON_IS_EXPIRY.getMessage());
                    return response ;
                }
            }
            //判断钱包余额是否够
            if(balance != null && balance.compareTo(BigDecimal.ZERO) == 1){
                CashAccountEntity cashAccountEntity = cashAccountService.findByUserCode(userCode) ;
                if(cashAccountEntity ==null || cashAccountEntity.getBalance().compareTo(balance) == -1){
                    //钱包余额不够
                    response.setCode(ResCodeEnum.CASH_ACCOUNT_BALANCE_LACK.getCode());
                    response.setMessage(ResCodeEnum.CASH_ACCOUNT_BALANCE_LACK.getMessage());
                    return response ;
                }else{
                    payInfoCache.setBalance(balance);
                    shouldPayAmount = shouldPayAmount.subtract(cashAccountEntity.getBalance());
                }
            }
            payInfoCache.setPayNumber(shouldPayAmount.compareTo(BigDecimal.ZERO) == 1 ? shouldPayAmount : BigDecimal.ZERO);

            if(shouldPayAmount.compareTo(BigDecimal.ZERO) != 1){
                //余额足够 直接扣余额 不走第三方
                return repayForBalance(response , payInfoCache , request);
            }

            /***************** 2018-11-08 顾晓桐增加版本 -- 支付中心********************/
            if(raptorPayVersion.equals(CommonValues.SEEKER_PAY_VERSION)){
                //支付中心版本
                return seekerRepay(payInfoCache , response);
            }
            /*************************************/

            redisServiceApi.set(RedisParams.PAY_CODE + code, payInfoCache, RedisParams.EXPIRE_5M, raptorRedis);

            String url = request.getScheme()+ "://" + request.getServerName() + request.getContextPath() + "/cash/cashier?code=" + code;
            data.put("url", url);
            logger.info("repay方法结束,userCode:"+userCode+",loanOrderId:"+loanOrderId+",url:"+url);
            return response.buildSuccessResponse(data);
        }catch (Exception e){
            Log.error(logger, e,"发起支付出现异常userCode={}", userCode);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }

    }

    /**
     *直接使用余额支付
     * @param payInfoCache
     * @return
     */
    private BaseResponse<JSONObject> repayForBalance(BaseResponse<JSONObject> response , PayInfoCache payInfoCache , HttpServletRequest request) {

        String channel = "balance_pay" ;
        // 创建还款
        String payOrderId = sockpuppet + "-" + String.valueOf(idWorker.nextId());
        PayOrderEntity payOrder = new PayOrderEntity();
        payOrder.setOrderId(payOrderId);
        payOrder.setStatus(StatusEnum.DEDUCTING.name());
        payOrder.setOwnerId(payInfoCache.getUserCode());
        payOrder.setType(payInfoCache.getPayType());
        payOrder.setApplyNumber(payInfoCache.getPayNumber());
        payOrder.setPostponeDays(payInfoCache.getPeriod());
        payOrder.setLoanOrderId(payInfoCache.getLoanOrderId());
        payOrder.setDescription(System.currentTimeMillis() + ":用户线上余额还款" + payInfoCache.getPayNumber().toPlainString() + ", 直接创建扣款中还款订单");
        payOrder.setPayCurrency(CurrencyEnum.getDefaultCurrency().name());
        payOrder.setChannel(channel);
        payOrder.create();

        PayOrderLogEntity payOrderLog = new PayOrderLogEntity();
        payOrderLog.setIdCard(payInfoCache.getIdCard());
        payOrderLog.setUserName(payInfoCache.getUserName());
        payOrderLog.setRepayAmount(payInfoCache.getPayNumber());
        payOrderLog.setUserCode(payInfoCache.getUserCode());
        payOrderLog.setClientId(payInfoCache.getClientId());
        payOrderLog.setClientVersion(payInfoCache.getClientVersion());
        payOrderLog.setBankCard("余额还款未知");
        payOrderLog.setBankMobile("余额还款未知");
        payOrderLog.setOrderId(payInfoCache.getLoanOrderId());
        if (PayTypeEnum.REPAY_POSTPONE.name().equals(payInfoCache.getPayType())) {
            LoanOrderEntity loanOrder = loanOrderService.getByOrderId(payInfoCache.getLoanOrderId());
            payOrderLog.setFormerRepaymentDate(loanOrder.getRepaymentDate());
        }
        payOrderLog.setPayOrderId(payOrder.getOrderId());
        payOrderLog.setChannel(channel);
        payOrderLog.create();
        //保存 用户流水
        payOrderService.savePayOrderAndLog(payOrder, payOrderLog);

        // 模拟先玩后付mq还款通知
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("status", "success");
        params.put("channel", channel);
        params.put("amount", payOrder.getApplyNumber());
        params.put("dealcode", "余额还款未知");
        params.put("channelDealcode", "余额还款未知");
        params.put("orderId", payOrder.getOrderId());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("remark", params);

        JSONObject data = new JSONObject();
        MqMessage message = new MqMessage("TOPIC", "MQ_RAPTOR_PAYOFF_TAG", jsonObject.toJSONString());
        String url = request.getScheme()+ "://" + request.getServerName() + request.getContextPath() + "/cash/success?code=" + payOrderId;
        try {
            MqAction mqAction = loanMo9mqListener.consume(message, null);
            if(mqAction == MqAction.ReconsumeLater){
                payOrder.setStatus(StatusEnum.DEDUCT_FAILED.name());
                payOrderLog.setFailReason("余额还款失败");
                //保存 用户流水
                payOrderService.savePayOrderAndLog(payOrder, payOrderLog);
                url = request.getScheme()+ "://" + request.getServerName() + request.getContextPath() + "/cash/failed?code=" + payOrderId + "&message=余额还款失败";
            }
        } catch (Exception e) {
            Log.error(logger, e,"发起余额支付出现异常userCode={}", payInfoCache.getUserCode());
            url = request.getScheme()+ "://" + request.getServerName() + request.getContextPath() + "/cash/failed?code=" + payOrderId + "&message=余额还款异常";
        }

        data.put("url", url);
        return response.buildSuccessResponse(data);

    }

    /**
     * 支付中心版本支付
     * @param payInfoCache
     * @param response
     * @return
     */
    private BaseResponse<JSONObject> seekerRepay(PayInfoCache payInfoCache , BaseResponse<JSONObject> response) {

        try {
            //获取手机号
            BankEntity bank = bankService.findByUserCodeLastOne(payInfoCache.getUserCode()) ;

            String orderId = sockpuppet + "-" + String.valueOf(idWorker.nextId());
            PayOrderEntity payOrder = new PayOrderEntity();
            payOrder.setOrderId(orderId);
            payOrder.setStatus(StatusEnum.PENDING.name());

            String payType = payInfoCache.getPayType();
            payOrder.setOwnerId(payInfoCache.getUserCode());
            payOrder.setType(payType);
            payOrder.setApplyNumber(payInfoCache.getPayNumber());
            payOrder.setPostponeDays(payInfoCache.getPeriod());
            payOrder.setLoanOrderId(payInfoCache.getLoanOrderId());

            payOrder.setPayCurrency(CurrencyEnum.getDefaultCurrency().name());
            payOrder.setChannel("未知");
            payOrder.create();

            PayOrderLogEntity payOrderLog = new PayOrderLogEntity();
            payOrderLog.setIdCard(payInfoCache.getIdCard());
            payOrderLog.setUserName(payInfoCache.getUserName());
            payOrderLog.setRepayAmount(payInfoCache.getPayNumber());
            payOrderLog.setUserCode(payInfoCache.getUserCode());
            payOrderLog.setClientId(payInfoCache.getClientId());
            payOrderLog.setClientVersion(payInfoCache.getClientVersion());
            payOrderLog.setOrderId(payInfoCache.getLoanOrderId());
            if (PayTypeEnum.REPAY_POSTPONE.name().equals(payType)) {
                LoanOrderEntity loanOrder = loanOrderService.getByOrderId(payInfoCache.getLoanOrderId());
                payOrderLog.setFormerRepaymentDate(loanOrder.getRepaymentDate());
            }
            payOrderLog.setPayOrderId(payOrder.getOrderId());
            payOrderLog.setChannel("未知");
            payOrderLog.setBankCard(bank.getBankNo());
            payOrderLog.setBankMobile(bank.getMobile());

            payOrderLog.create();
            payOrderService.savePayOrderAndLogAndNotice(payOrder, payOrderLog);

            PayOderChannelRes res = getResBySeeker(orderId);

            JSONObject data = new JSONObject();

            data.put("entities", res);
            data.put("url", res.getResult());
            logger.info("repay方法结束,userCode:"+payInfoCache.getUserCode()+",loanOrderId:"+payInfoCache.getLoanOrderId()+",url:"+res.getResult());
            return response.buildSuccessResponse(data);
        } catch (Exception e) {
            Log.error(logger, e,"发起支付出现异常userCode={}", payInfoCache.getUserCode());
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
    }

    /**
     * 续期支付
     * @return
     */
    @PostMapping("/renewal")
    @ResponseBody
    public BaseResponse<JSONObject> renewal(@Valid @RequestBody LoanOrderRenewal req, HttpServletRequest request) {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        String loanOrderId = req.getLoanOrderId();
        String couponId = req.getCouponId();
        BigDecimal balance = req.getBalance();
        int period = req.getPeriod();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        String clientId = request.getHeader(ReqHeaderParams.CLIENT_ID);
        String clientVersion = request.getHeader(ReqHeaderParams.CLIENT_VERSION);
        try{
            // 用户没删就行, 拉黑也能还
            //UserEntity user = userService.findByUserCodeAndDeleted(userCode, false);
            UserCertifyInfoEntity userCertifyInfoEntity = userCertifyInfoService.findByUserCode(userCode);
            if (userCertifyInfoEntity == null) {
                return response.buildFailureResponse(ResCodeEnum.USER_NOT_EXIST);
            }

            // 延期天数暂时和借款订单脱离关系
            Boolean checkRenewableDays = RenewableDaysEnum.checkRenewableDays(period);
            if (!checkRenewableDays) {
                return response.buildFailureResponse(ResCodeEnum.INVALID_RENEWAL_DAYS);
            }

            LoanOrderEntity loanOrder = loanOrderService.getByOrderId(req.getLoanOrderId());
            if (loanOrder == null || !StatusEnum.LENT.name().equals(loanOrder.getStatus())) {
                return response.buildFailureResponse(ResCodeEnum.ILLEGAL_LOAN_ORDER_STATUE);
            }
            // 检查用户
            if (!loanOrder.getOwnerId().equals(userCode)) {
                return response.buildFailureResponse(ResCodeEnum.ILLEGAL_REPAYMENT);
            }

            Item shouldPayItem = billService.shouldPayItem(loanOrder, PayTypeEnum.REPAY_POSTPONE, period);
            BigDecimal applyAmount = shouldPayItem.sum();

            JSONObject data = new JSONObject();

            String code = String.valueOf(idWorker.nextId());

            PayInfoCache payInfoCache = new PayInfoCache();
            payInfoCache.setUserCode(userCode);
            payInfoCache.setLoanOrderId(loanOrderId);
            payInfoCache.setPayType(PayTypeEnum.REPAY_POSTPONE.name());
            payInfoCache.setPeriod(period);
            payInfoCache.setUserName(userCertifyInfoEntity.getRealName());
            payInfoCache.setIdCard(userCertifyInfoEntity.getIdCard());
            payInfoCache.setClientId(clientId);
            payInfoCache.setClientVersion(clientVersion);

            //判断优惠券是否存在
            if(couponId != null){
                CouponEntity couponEntity = couponService.getByCouponId(couponId);
                if(couponEntity.getStatus().equals(StatusEnum.PENDING.name()) && couponEntity.getExpireDate() > System.currentTimeMillis()){
                    payInfoCache.setCouponId(couponId);
                    applyAmount = applyAmount.subtract(couponEntity.getApplyAmount());
                }else{
                    //钱包余额不够
                    response.setCode(ResCodeEnum.COUPON_IS_EXPIRY.getCode());
                    response.setMessage(ResCodeEnum.COUPON_IS_EXPIRY.getMessage());
                    return response ;
                }
            }
            //判断钱包余额是否够
            if(balance != null && balance.compareTo(BigDecimal.ZERO) == 1){
                CashAccountEntity cashAccountEntity = cashAccountService.findByUserCode(userCode) ;
                if(cashAccountEntity ==null || cashAccountEntity.getBalance().compareTo(balance) == -1){
                    //钱包余额不够
                    response.setCode(ResCodeEnum.CASH_ACCOUNT_BALANCE_LACK.getCode());
                    response.setMessage(ResCodeEnum.CASH_ACCOUNT_BALANCE_LACK.getMessage());
                    return response ;
                }else{
                    payInfoCache.setBalance(balance);
                    applyAmount = applyAmount.subtract(cashAccountEntity.getBalance());
                }
            }

            payInfoCache.setPayNumber(applyAmount.compareTo(BigDecimal.ZERO) == 1 ? applyAmount : BigDecimal.ZERO);

            if(applyAmount.compareTo(BigDecimal.ZERO) != 1){
                //余额足够 直接扣余额 不走第三方
                return repayForBalance(response , payInfoCache , request);
            }

            /***************** 2018-11-08 顾晓桐增加版本 -- 支付中心********************/
            if(raptorPayVersion.equals(CommonValues.SEEKER_PAY_VERSION)){
                //支付中心版本
                return seekerRepay(payInfoCache , response);
            }
            /*************************************/

            redisServiceApi.set(RedisParams.PAY_CODE + code, payInfoCache, RedisParams.EXPIRE_5M, raptorRedis);

            String url = request.getScheme()+ "://" + request.getServerName() + request.getContextPath() + "/cash/cashier?code=" + code;
            data.put("url", url);

            return response.buildSuccessResponse(data);
        }catch (Exception e){
            Log.error(logger, e,"续期支付出现异常userCode={}", userCode);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }

    }

    @GetMapping("/cashier")
    public String cashier (Model model, @RequestParam String code, HttpServletRequest request) {

        try{
            PayInfoCache payInfoCache =  (PayInfoCache) redisServiceApi.get(RedisParams.PAY_CODE + code, raptorRedis);

            model.addAttribute("code", code);
            if (payInfoCache == null) {
                model.addAttribute("message", ResCodeEnum.PAY_INFO_EXPIRED.getCode());
                /** 返回支付过期页面 */
                return "cashier/feedback_fail";
            }

            /** 增加支付信息 */
            model.addAttribute("payInfo", payInfoCache);

            BankEntity bank = bankService.findByUserCodeLastOne(payInfoCache.getUserCode());
            if (bank == null) {
                model.addAttribute("message", ResCodeEnum.ERROR_BANK_CARD.getCode());
                return "cashier/feedback_fail";
            }
            /** 默认支付银行卡 */
            model.addAttribute("defaultBank", bank);

            /** 增加可用于扣款的银行卡列表 */
            List<BankEntity> banks = bankService.findByUserCode(payInfoCache.getUserCode());
            model.addAttribute("banks", banks);

            /** 增加代扣渠道列表 */
            List<ChannelEntity> channels = channelService.listByChannelType(ChannelTypeEnum.REPAY.name());
            model.addAttribute("channels", channels);

            return "cashier/pay";
        }catch (Exception e){
            Log.error(logger, e,"跳转支付页面出现异常");
            return null;
        }

    }

    @GetMapping("/card_cashier")
    public String cardCashier (Model model, @RequestParam String code, @RequestParam String channel, HttpServletRequest request) {

        try{
            PayInfoCache payInfoCache =  (PayInfoCache) redisServiceApi.get(RedisParams.PAY_CODE + code, raptorRedis);

            model.addAttribute("code", code);
            if (payInfoCache == null) {
                model.addAttribute("message", ResCodeEnum.PAY_INFO_EXPIRED.getCode());
                /** 返回支付过期页面 */
                return "cashier/feedback_fail";
            }

            model.addAttribute("channel", channel);

            return "cashier/card_adder";
        }catch (Exception e){
            Log.error(logger, e,"card_adder出现异常");
            return null;
        }

    }

    @PostMapping("/cashier/submit")
    @ResponseBody
    public BaseResponse<JSONObject> cashierSubmit (@RequestParam String code,
                                                   @RequestParam String channel,
                                                   @RequestParam String bankNo) {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        try{

            PayInfoCache payInfoCache =  (PayInfoCache) redisServiceApi.get(RedisParams.PAY_CODE + code, raptorRedis);
            if (payInfoCache == null) {
                return response.buildFailureResponse(ResCodeEnum.PAY_INFO_EXPIRED);
            }
            logger.info("/cashier/submit方法开始,userCode:"+payInfoCache.getUserCode());
            // 检查渠道
            ChannelEntity channelEntity = channelService.getChannelByType(channel, ChannelTypeEnum.REPAY.name());
            if (channelEntity == null) {
                return response.buildFailureResponse(ResCodeEnum.NO_REPAY_CHANNEL);
            }

            //获取手机号
            BankEntity bank = bankService.findByBankNo(bankNo);


            String orderId = sockpuppet + "-" + String.valueOf(idWorker.nextId());
            PayOrderEntity payOrder = new PayOrderEntity();
            payOrder.setOrderId(orderId);
            payOrder.setStatus(StatusEnum.PENDING.name());

            String payType = payInfoCache.getPayType();
            payOrder.setOwnerId(payInfoCache.getUserCode());
            payOrder.setType(payType);
            payOrder.setApplyNumber(payInfoCache.getPayNumber());
            payOrder.setPostponeDays(payInfoCache.getPeriod());
            payOrder.setLoanOrderId(payInfoCache.getLoanOrderId());

            payOrder.setPayCurrency(CurrencyEnum.getDefaultCurrency().name());
            payOrder.setChannel(channel);
            String couponId = payInfoCache.getCouponId();
            BigDecimal balance = payInfoCache.getBalance();
            //判断优惠券是否存在
            if(couponId != null){
                CouponEntity couponEntity = couponService.getByCouponId(couponId);
                if(couponEntity.getStatus().equals(StatusEnum.PENDING.name()) && couponEntity.getExpireDate() > System.currentTimeMillis()){
                    payOrder.setCouponId(couponId);
                }else{
                    //钱包余额不够
                    response.setCode(ResCodeEnum.COUPON_IS_EXPIRY.getCode());
                    response.setMessage(ResCodeEnum.COUPON_IS_EXPIRY.getMessage());
                    return response ;
                }
            }
            //判断钱包余额是否够
            if(balance != null && balance.compareTo(BigDecimal.ZERO) == 1){
                CashAccountEntity cashAccountEntity = cashAccountService.findByUserCode(payInfoCache.getUserCode()) ;
                if(cashAccountEntity ==null || cashAccountEntity.getBalance().compareTo(balance) == -1){
                    //钱包余额不够
                    response.setCode(ResCodeEnum.CASH_ACCOUNT_BALANCE_LACK.getCode());
                    response.setMessage(ResCodeEnum.CASH_ACCOUNT_BALANCE_LACK.getMessage());
                    return response ;
                }
            }

            payOrder.create();

            PayOrderLogEntity payOrderLog = new PayOrderLogEntity();
            payOrderLog.setIdCard(payInfoCache.getIdCard());
            payOrderLog.setUserName(payInfoCache.getUserName());
            payOrderLog.setRepayAmount(payInfoCache.getPayNumber());
            payOrderLog.setUserCode(payInfoCache.getUserCode());
            payOrderLog.setClientId(payInfoCache.getClientId());
            payOrderLog.setClientVersion(payInfoCache.getClientVersion());
            payOrderLog.setOrderId(payInfoCache.getLoanOrderId());
            if (PayTypeEnum.REPAY_POSTPONE.name().equals(payType)) {
                LoanOrderEntity loanOrder = loanOrderService.getByOrderId(payInfoCache.getLoanOrderId());
                payOrderLog.setFormerRepaymentDate(loanOrder.getRepaymentDate());
            }
            payOrderLog.setPayOrderId(payOrder.getOrderId());
            payOrderLog.setChannel(channel);
            payOrderLog.setBankCard(bankNo);
            payOrderLog.setBankMobile(bank.getMobile());

            payOrderLog.create();
            payOrderService.savePayOrderAndLogAndNotice(payOrder, payOrderLog);

            redisServiceApi.remove(RedisParams.PAY_CODE + code, raptorRedis);

            PayOderChannelRes res = getRes(orderId, channelEntity.getId());

            JSONObject data = new JSONObject();

            data.put("entities", res);
            logger.info("/cashier/submit方法结束,userCode:"+payInfoCache.getUserCode()+"url:"+res.toString());
            return response.buildSuccessResponse(data);
        }catch (Exception e){
            Log.error(logger, e,"cashierSubmit出现异常code={}", code);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }


    }

    @PostMapping("/card_cashier/submit")
    @ResponseBody
    public BaseResponse<JSONObject> cardCashierSubmit (@RequestParam String code,
                                   @RequestParam String channel,
                                   @RequestParam String userName,
                                   @RequestParam String idCard,
                                   @RequestParam String bankNo,
                                   @RequestParam String mobile) {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        try{
            PayInfoCache payInfoCache =  (PayInfoCache) redisServiceApi.get(RedisParams.PAY_CODE + code, raptorRedis);

            if (payInfoCache == null) {
                return response.buildFailureResponse(ResCodeEnum.PAY_INFO_EXPIRED);
            }

            // 检查渠道
            ChannelEntity channelEntity = channelService.getChannelByType(channel, ChannelTypeEnum.REPAY.name());
            if (channelEntity == null) {
                return response.buildFailureResponse(ResCodeEnum.NO_REPAY_CHANNEL);
            }

            if (!userName.equals(payInfoCache.getUserName()) || !idCard.equals(payInfoCache.getIdCard())) {
                return response.buildFailureResponse(ResCodeEnum.INVALID_REPAY_INFO);
            }

            String orderId = sockpuppet + "-" + String.valueOf(idWorker.nextId());
            PayOrderEntity payOrder = new PayOrderEntity();
            payOrder.setOrderId(orderId);
            payOrder.setStatus(StatusEnum.PENDING.name());

            String payType = payInfoCache.getPayType();
            payOrder.setOwnerId(payInfoCache.getUserCode());
            payOrder.setType(payType);
            payOrder.setApplyNumber(payInfoCache.getPayNumber());
            payOrder.setPostponeDays(payInfoCache.getPeriod());
            payOrder.setLoanOrderId(payInfoCache.getLoanOrderId());

            payOrder.setPayCurrency(CurrencyEnum.getDefaultCurrency().name());
            payOrder.setChannel(channel);
            payOrder.create();

            PayOrderLogEntity payOrderLog = new PayOrderLogEntity();
            payOrderLog.setIdCard(idCard);
            payOrderLog.setUserName(userName);
            payOrderLog.setRepayAmount(payInfoCache.getPayNumber());
            payOrderLog.setUserCode(payInfoCache.getUserCode());
            payOrderLog.setClientId(payInfoCache.getClientId());
            payOrderLog.setClientVersion(payInfoCache.getClientVersion());
            payOrderLog.setOrderId(payInfoCache.getLoanOrderId());
            if (PayTypeEnum.REPAY_POSTPONE.name().equals(payType)) {
                LoanOrderEntity loanOrder = loanOrderService.getByOrderId(payInfoCache.getLoanOrderId());
                payOrderLog.setFormerRepaymentDate(loanOrder.getRepaymentDate());
            }

            payOrderLog.setPayOrderId(payOrder.getOrderId());
            payOrderLog.setChannel(channel);
            payOrderLog.setBankCard(bankNo);
            payOrderLog.setBankMobile(mobile);

            payOrderLog.create();
            payOrderService.savePayOrderAndLogAndNotice(payOrder, payOrderLog);

            redisServiceApi.remove(RedisParams.PAY_CODE + code, raptorRedis);

            PayOderChannelRes res = getRes(orderId, channelEntity.getId());

            JSONObject data = new JSONObject();

            data.put("entities", res);

            return response.buildSuccessResponse(data);
        }catch (Exception e){
            Log.error(logger, e,"cardCashierSubmit出现异常code={}", code);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
    }

    @GetMapping("/failed")
    public String failed (Model model, @RequestParam String code, @RequestParam String message, HttpServletRequest request) {

        model.addAttribute("code", code);
        model.addAttribute("message", message);

        return "cashier/feedback_fail";
    }

    @GetMapping("/success")
    public String success (Model model, @RequestParam String code, @RequestParam String message, HttpServletRequest request) {

        model.addAttribute("code", code);
        return "cashier/feedback_success";
    }

    @GetMapping("/cashier/has_repaying")
    @ResponseBody
    public BaseResponse<Boolean> hasRepaying ( @RequestParam String code , HttpServletRequest request) {
        //获取userCode
        BaseResponse<Boolean> response = new BaseResponse<Boolean>();
        PayInfoCache payInfoCache =  (PayInfoCache) redisServiceApi.get(RedisParams.PAY_CODE + code, raptorRedis);
        if (payInfoCache == null) {
            return response.buildFailureResponse(ResCodeEnum.PAY_INFO_EXPIRED);
        }
        String userCode = payInfoCache.getUserCode() ;
        List<String> statusEnums = new ArrayList<String>() ;
        statusEnums.add(StatusEnum.DEDUCTING.name()) ;
        List<PayOrderEntity> payOrderEntities = payOrderService.listByUserAndStatus(userCode , statusEnums);
        if(payOrderEntities != null && payOrderEntities.size() > 0){
            logger.info("用户 " + userCode + " 查询是否存在扣款中订单 " + payOrderEntities.size() + " 个 ");
            response.setData(true);
        }else{
            logger.info("用户 " + userCode + " 不存在扣款中订单 " );
            response.setData(false);
        }
        return response;
    }

    /**
     * 获取渠道列表
     * @return
     */
    @GetMapping("/get_repay_channels")
    @ResponseBody
    public BaseResponse<JSONObject> getRepayChannels () {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        try{
            List<ChannelDetailRes> channels = new ArrayList<ChannelDetailRes>();
            List<ChannelEntity> channelEntities = channelService.listByChannelType(ChannelTypeEnum.REPAY.name());

            for (ChannelEntity channel : channelEntities) {
                ChannelDetailRes res = new ChannelDetailRes();
                res.setChannelName(channel.getChannelName());
                res.setChannelType(channel.getId());
                res.setUseType(channel.getUseType());
                channels.add(res);
            }
            JSONObject data = new JSONObject();
            data.put("entities", channels);
            return response.buildSuccessResponse(data);
        }catch (Exception e){
            Log.error(logger, e,"获取渠道列表出现异常");
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }


    }

    private PayOderChannelRes getRes(String payOrderId, Long channelId) {
        PayOderChannelRes res = new PayOderChannelRes();
        // 重新查询Log, 返回url
        PayOrderLogEntity savedOrderLog = payOrderLogService.getByPayOrderId(payOrderId);
        String channelSyncResponse = savedOrderLog.getChannelSyncResponse();
        if (StringUtils.isNotBlank(channelSyncResponse)) {
            JSONObject jsonObject = JSONObject.parseObject(channelSyncResponse);
            String code = jsonObject.getString("code");
            if ("0000".equals(code)) {
                JSONObject data = jsonObject.getJSONObject("data");
                String url = data.getString("result");
                res.setUseType(ChannelUseType.LINK.getDesc());
                res.setResult(url);
                res.setState(true);
                res.setChannelType(channelId);
            } else {
                res.setState(false);
            }
        } else {
            res.setState(false);
        }
        return res;
    }

    /**
     * 封装 支付中心版本 还款返回数据
     * @param payOrderId
     * @return
     */
    private PayOderChannelRes getResBySeeker(String payOrderId) {
        PayOderChannelRes res = new PayOderChannelRes();
        // 重新查询Log, 返回url
        PayOrderLogEntity savedOrderLog = payOrderLogService.getByPayOrderId(payOrderId);
        String channelSyncResponse = savedOrderLog.getChannelSyncResponse();
        if (StringUtils.isNotBlank(channelSyncResponse)) {
            JSONObject jsonObject = JSONObject.parseObject(channelSyncResponse);
            String code = jsonObject.getString("code");
            if ("0000".equals(code)) {
                JSONObject data = jsonObject.getJSONObject("data");
                String url = data.getString("payUrl");
                res.setUseType(ChannelUseType.LINK.getDesc());
                res.setResult(url);
                res.setState(true);
            } else {
                res.setState(false);
            }
        } else {
            res.setState(false);
        }
        return res;
    }

}
