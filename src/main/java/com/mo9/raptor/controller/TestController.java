package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.mqclient.MqMessage;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.ReqHeaderParams;
import com.mo9.raptor.engine.entity.CouponEntity;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.BillService;
import com.mo9.raptor.engine.service.CouponService;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.engine.utils.EngineStaticValue;
import com.mo9.raptor.engine.utils.TimeUtils;
import com.mo9.raptor.entity.PayOrderLogEntity;
import com.mo9.raptor.entity.RabbitProducerMqEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.CaptchaBusinessEnum;
import com.mo9.raptor.enums.CurrencyEnum;
import com.mo9.raptor.enums.PayTypeEnum;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.mq.listen.LoanMo9mqListener;
import com.mo9.raptor.mq.producer.RabbitProducer;
import com.mo9.raptor.redis.RedisParams;
import com.mo9.raptor.redis.RedisServiceApi;
import com.mo9.raptor.service.PayOrderLogService;
import com.mo9.raptor.service.RabbitProducerMqService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.IDWorker;
import com.mo9.raptor.utils.Md5Encrypt;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author zma
 * @date 2018/9/17
 */
@RestController
@RequestMapping(value = "/test")
public class TestController {

    private static Logger logger = Log.get();
    @Resource
    private RedisServiceApi redisServiceApi;

    @Resource
    private UserService userService;

    @Autowired
    private ILoanOrderService loanOrderService;

    @Autowired
    private RabbitProducerMqService rabbitProducerMqService;

    @Autowired
    private RabbitProducer rabbitProducer;

    @Autowired
    private PayOrderLogService payOrderLogService;

    @Autowired
    private LoanMo9mqListener loanMo9mqListener;

    @Autowired
    private IPayOrderService payOrderService;

    @Autowired
    private BillService billService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private IDWorker idWorker;

    @Resource(name = "raptorRedis")
    private RedisTemplate raptorRedis;

    @Value("${test.open}")
    private String testOpen = "false";

    @Value("${raptor.sockpuppet}")
    private String sockpuppet;

    /**
     * 获取短信验证码
     * @param request
     * @param receive
     * @return
     */
    @RequestMapping("/fetch_mobile_verification_code")
    public BaseResponse fetchSmsVerificationCode(HttpServletRequest request, @RequestParam("mobile")String receive){
        BaseResponse response = new BaseResponse();
        if(!Boolean.valueOf(testOpen)){
            return response.buildFailureResponse(ResCodeEnum.TEST_OPEN_CLOSE) ;
        }
        String redisKey = getRedisKey(RedisParams.MOBILE_CAPTCHA_KEY, receive, CaptchaBusinessEnum.LOGIN);
        String redisCaptcha = (String) redisServiceApi.get(redisKey, raptorRedis);
        JSONObject codeData = new JSONObject();
        codeData.put("code",redisCaptcha);
        return new BaseResponse("手机验证码",codeData);
    }

    /**
     * 修改用户状态
     */
    @RequestMapping("/update_user_status")
    public BaseResponse<UserEntity> updateUserStatus(HttpServletRequest request, @RequestParam("status") StatusEnum status){
        BaseResponse<UserEntity> response = new BaseResponse<UserEntity>();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        try{
            if(!Boolean.valueOf(testOpen)){
                return response.buildFailureResponse(ResCodeEnum.TEST_OPEN_CLOSE) ;
            }
            UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
            userEntity.setStatus(status.name());
            userEntity.setRemark("测试接口修改用户状态为:" + status.name());
            userService.save(userEntity);
            logger.info("测试修改用户状态为status={}", status);
            return response.buildSuccessResponse(userEntity);
        }catch (Exception e){
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
    }

    @RequestMapping("/test")
    public String test(){
        return "1";
    }

    private String getRedisKey(String redisKey, String receive, CaptchaBusinessEnum businessCode){
        return redisKey + businessCode + "_" + receive;
    }

    /**
     * 推送mq
     */
    @RequestMapping("/mq")
    public BaseResponse<JSONObject> mq(@RequestParam("sign") String sign, HttpServletRequest request){
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        if (!"28B21099FBDD85467CC01E7B80146FF0".equals(sign)) {
            response.setMessage("验签错误");
            return response;
        }

        List<PayOrderEntity> payOrderEntities = payOrderService.findByStatus(StatusEnum.ENTRY_DONE.name());
        for (PayOrderEntity payOrderEntity : payOrderEntities) {
            LoanOrderEntity loanOrderEntity = loanOrderService.getByOrderId(payOrderEntity.getLoanOrderId());

            PayOrderLogEntity payOrderLogEntity = payOrderLogService.getByPayOrderId(payOrderEntity.getOrderId());

            loanMo9mqListener.notifyMisRepay(payOrderLogEntity, loanOrderEntity.getPostponeCount(), loanOrderEntity);
        }

//        List<LoanOrderEntity> loanOrderEntities = loanOrderService.listByStatus(Arrays.asList(StatusEnum.LENT));
//        if (loanOrderEntities == null || loanOrderEntities.size() == 0) {
//            response.setMessage("无订单");
//            return response;
//        }
//
//        for (LoanOrderEntity loanOrderEntity : loanOrderEntities) {
//            RabbitProducerMqEntity producerMqEntity = rabbitProducerMqService.findByMessageKey(loanOrderEntity.getOrderId());
//            if (producerMqEntity != null) {
//                logger.info("订单[{}]已发送过mq, 跳过", loanOrderEntity.getOrderId());
//                continue;
//            }
//            loanMo9mqListener.notifyMisLend(loanOrderEntity.getOrderId());
//        }
        response.setMessage("ok");
        return response;
    }

    /**
     * 推送指定的mq
     */
    @RequestMapping("/mq2")
    public BaseResponse<JSONObject> mq2(@RequestParam("sign") String sign, HttpServletRequest request){
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        if (!"28B21099FBDD85467CC01E7B80146FF0".equals(sign)) {
            response.setMessage("验签错误");
            return response;
        }

        loanMo9mqListener.notifyMisLend("TTYQ-230353561840652288");
        loanMo9mqListener.notifyMisLend("TTYQ-230351599736852480");
        loanMo9mqListener.notifyMisLend("TTYQ-230352143721304064");

        response.setMessage("ok");
        return response;
    }

    /**
     * 推送还清的借款订单
     */
    @RequestMapping("/mq3")
    public BaseResponse<JSONObject> mq3(@RequestParam("sign") String sign, HttpServletRequest request){
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        if (!"28B21099FBDD85467CC01E7B80146FF0".equals(sign)) {
            response.setMessage("验签错误");
            return response;
        }

        List<LoanOrderEntity> loanOrderEntities = loanOrderService.listByStatus(Arrays.asList(StatusEnum.PAYOFF));
        if (loanOrderEntities == null || loanOrderEntities.size() == 0) {
            response.setMessage("无订单");
            return response;
        }

        for (LoanOrderEntity loanOrderEntity : loanOrderEntities) {
            RabbitProducerMqEntity producerMqEntity = rabbitProducerMqService.findByMessageKey(loanOrderEntity.getOrderId());
            if (producerMqEntity != null) {
                logger.info("订单[{}]已发送过mq, 跳过", loanOrderEntity.getOrderId());
                continue;
            }
            loanMo9mqListener.notifyMisLend(loanOrderEntity.getOrderId());
        }

        response.setMessage("ok");
        return response;
    }

    /**
     * 线下还款
     */
//    @GetMapping("/offline_repay")
//    public BaseResponse<JSONObject> offlineRepay(
//            @RequestParam("userCode") String userCode,
//            @RequestParam("type") String type,
//            @RequestParam("amount") BigDecimal amount,
//            @RequestParam("accessUserCode") String accessUserCode,
//            @RequestParam("sign") String sign,
//            HttpServletRequest request){
//        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
//        List<String> access = new ArrayList<String>();
//        access.add("0E85007DC2B3852AD5EF198763049E83");
//        access.add("DD73904B9D39FD45CD7AC4E54F9576A8");
//
//        if (!access.contains(accessUserCode)) {
//            response.setMessage("不可访问");
//            return response;
//        }
//
//        // 检验签名
//        Map<String, String> signParams = new  HashMap<String, String> ();
//        signParams.put("userCode", userCode);
//        signParams.put("type", type);
//        signParams.put("amount", amount.toPlainString());
//        signParams.put("accessUserCode", accessUserCode);
//        String resultSign = Md5Encrypt.sign(signParams, "mo9123456");
//        if (!resultSign.equalsIgnoreCase(sign)) {
//            return response.buildFailureResponse(ResCodeEnum.INVALID_SIGN);
//        }
//
//
//        // 调用人员的实体
//        UserEntity accessUserEntity = userService.findByUserCodeAndDeleted(accessUserCode, false);
//        // 还清人员的实体
//        UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
//        if (userEntity == null) {
//            return response.buildFailureResponse(ResCodeEnum.USER_NOT_EXIST);
//        }
//
//        LoanOrderEntity loanOrder = loanOrderService.getLastIncompleteOrder(userEntity.getUserCode(), Arrays.asList(StatusEnum.LENT.name()));
//        if (loanOrder == null) {
//            return response.buildFailureResponse(ResCodeEnum.LOAN_ORDER_NOT_EXISTED);
//        }
//        if (!StatusEnum.LENT.name().equals(loanOrder.getStatus())) {
//            return response.buildFailureResponse(ResCodeEnum.ILLEGAL_LOAN_ORDER_STATUE);
//        }
//
//        BigDecimal couponAmount = BigDecimal.ZERO;
//        String payType = null;
//        Integer postponeDays = 0;
//        String channel = "manual_pay";
//
//        // 计算还款信息
//        if (type.equals("REPAY")) {
//            BigDecimal lentNumber = loanOrder.getLentNumber();
//            if (amount.compareTo(lentNumber) < 0) {
//                // 还的钱比放款的要少
//                response.setMessage("至少应还" + lentNumber.toPlainString());
//                return response;
//            }
//            Item payoffRealItem = billService.payoffRealItem(loanOrder);
//            payType = payoffRealItem.getRepaymentType().name();
//            BigDecimal payoffSum = payoffRealItem.sum();
//            if (payoffSum.compareTo(amount) < 0) {
//                // 还的钱过多
//                response.setMessage("还清最多可还" + payoffSum.toPlainString());
//                return response;
//            }
//            couponAmount = payoffSum.subtract(amount);
//        } else if (type.equals("POSTPONE")) {
//            BigDecimal postponeUnitCharge = loanOrder.getPostponeUnitCharge();
//            if (amount.compareTo(postponeUnitCharge) < 0) {
//                // 还的钱比放款的要少
//                response.setMessage("至少应还" + postponeUnitCharge.toPlainString());
//                return response;
//            }
//            postponeDays = 7;
//            Item postponeRealItem = billService.realItem(loanOrder, PayTypeEnum.REPAY_POSTPONE, postponeDays);
//            payType = PayTypeEnum.REPAY_POSTPONE.name();
//            BigDecimal postponeSum = postponeRealItem.sum();
//            if (postponeSum.compareTo(amount) < 0) {
//                response.setMessage("延期最多可还" + postponeSum.toPlainString());
//                return response;
//            }
//            couponAmount = postponeSum.subtract(amount);
//        } else {
//            response.setMessage("不支持的type");
//            return response;
//        }
//
//        // 创建还款
//        String orderId = sockpuppet + "-" + String.valueOf(idWorker.nextId());
//        PayOrderEntity payOrder = new PayOrderEntity();
//        payOrder.setOrderId(orderId);
//        payOrder.setStatus(StatusEnum.DEDUCTING.name());
//        payOrder.setOwnerId(userCode);
//        payOrder.setType(payType);
//        payOrder.setApplyNumber(amount);
//        payOrder.setPostponeDays(postponeDays);
//        payOrder.setLoanOrderId(loanOrder.getOrderId());
//        payOrder.setDescription(System.currentTimeMillis() + ":用户线下还款" + amount.toPlainString() + ", 直接创建扣款中还款订单");
//        payOrder.setPayCurrency(CurrencyEnum.getDefaultCurrency().name());
//        payOrder.setChannel(channel);
//        payOrder.create();
//
//        PayOrderLogEntity payOrderLog = new PayOrderLogEntity();
//        payOrderLog.setIdCard("000000000000000000");
//        payOrderLog.setUserName("线下还款姓名未知");
//        payOrderLog.setRepayAmount(amount);
//        payOrderLog.setUserCode(userCode);
//        payOrderLog.setClientId("线下还款未知");
//        payOrderLog.setClientVersion("线下还款未知");
//        payOrderLog.setBankCard("线下还款未知");
//        payOrderLog.setBankMobile("线下还款未知");
//        payOrderLog.setOrderId(loanOrder.getOrderId());
//        if (PayTypeEnum.REPAY_POSTPONE.name().equals(payType)) {
//            payOrderLog.setFormerRepaymentDate(loanOrder.getRepaymentDate());
//        }
//        payOrderLog.setPayOrderId(payOrder.getOrderId());
//        payOrderLog.setChannel(channel);
//        payOrderLog.create();
//        payOrderService.savePayOrderAndLog(payOrder, payOrderLog);
//
//        // 制作优惠券
//        CouponEntity effectiveBundledCoupon = couponService.getEffectiveBundledCoupon(loanOrder.getOrderId());
//        if (effectiveBundledCoupon == null) {
//            CouponEntity coupon = new CouponEntity();
//            coupon.setCouponId(String.valueOf(idWorker.nextId()));
//            coupon.setBoundOrderId(loanOrder.getOrderId());
//            coupon.setApplyAmount(couponAmount);
//            Long today = TimeUtils.extractDateTime(System.currentTimeMillis());
//            coupon.setEffectiveDate(today);
//            coupon.setExpireDate(today + EngineStaticValue.DAY_MILLIS);
//            coupon.setStatus(StatusEnum.BUNDLED.name());
//            coupon.setCreator(accessUserEntity.getRealName());
//            coupon.setEntryAmount(BigDecimal.ZERO);
//            coupon.setReason("用户线下还清");
//            couponService.save(coupon);
//        } else {
//            logger.info("用户线下还款, 更新优惠券[{}]的金额为[{}], 原金额[{}]", effectiveBundledCoupon.getCouponId(), couponAmount, effectiveBundledCoupon.getApplyAmount());
//            effectiveBundledCoupon.setApplyAmount(couponAmount);
//            couponService.save(effectiveBundledCoupon);
//        }
//
//        // 模拟先玩后付mq还款通知
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("status", "success");
//        params.put("channel", channel);
//        params.put("amount", payOrder.getApplyNumber());
//        params.put("dealcode", "线下还款未知");
//        params.put("channelDealcode", "线下还款未知");
//        params.put("orderId", payOrder.getOrderId());
//        params.put("offline", true);
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("remark", params);
//
//        MqMessage message = new MqMessage("TOPIC", "MQ_RAPTOR_PAYOFF_TAG", jsonObject.toJSONString());
//        loanMo9mqListener.consume(message, null);
//        return response;
//    }
}
