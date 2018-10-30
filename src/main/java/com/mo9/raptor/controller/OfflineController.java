package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.mqclient.MqMessage;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.req.OfflineRepayReq;
import com.mo9.raptor.engine.entity.CouponEntity;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.BillService;
import com.mo9.raptor.engine.service.CouponService;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.entity.PayOrderLogEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.CurrencyEnum;
import com.mo9.raptor.enums.PayTypeEnum;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.mq.listen.LoanMo9mqListener;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.IDWorker;
import com.mo9.raptor.utils.Md5Encrypt;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by xzhang on 2018/10/10.
 */
@RestController
@RequestMapping(value = "/offline")
public class OfflineController {

    private static Logger logger = Log.get();

    @Resource
    private UserService userService;

    @Autowired
    private ILoanOrderService loanOrderService;

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

    @Value("${raptor.sockpuppet}")
    private String sockpuppet;

    /**
     * 线下还款
     */
    @PostMapping("/repay")
    public BaseResponse<JSONObject> offlineRepay(@RequestBody @Validated OfflineRepayReq req){
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        String orderId = req.getOrderId();
        String userCode = req.getUserCode();
        String type = req.getType();
        BigDecimal amount = req.getAmount();
        String sign = req.getSign();
        String creator = req.getCreator();
        String reliefReason = req.getReliefReason();

        // 检验签名
        Map<String, String> signParams = new HashMap<String, String>();
        signParams.put("orderId", orderId);
        signParams.put("userCode", userCode);
        signParams.put("type", type);
        signParams.put("amount", amount.toPlainString());
        signParams.put("creator", creator);
        signParams.put("reliefReason", reliefReason);
        String resultSign = Md5Encrypt.sign(signParams, "TWlBfbVtgmJb6tlYeWuTl2N26xtKT5SX");
        if (!resultSign.equalsIgnoreCase(sign)) {
            return response.buildFailureResponse(ResCodeEnum.INVALID_SIGN);
        }

        logger.info("offline/repay接口开始, 参数:userCode[{}], type[{}], amount[{}], sign[{}], creator[{}]", userCode, type, amount, sign, creator);

        try {
            // 还清人员的实体
            UserEntity userEntity = userService.findByUserCodeAndDeleted(req.getUserCode(), false);
            if (userEntity == null) {
                return response.buildFailureResponse(ResCodeEnum.USER_NOT_EXIST);
            }

            LoanOrderEntity loanOrder = loanOrderService.getByOrderId(orderId);
            if (loanOrder == null) {
                return response.buildFailureResponse(ResCodeEnum.LOAN_ORDER_NOT_EXISTED);
            }
            if (!StatusEnum.LENT.name().equals(loanOrder.getStatus())) {
                return response.buildFailureResponse(ResCodeEnum.ILLEGAL_LOAN_ORDER_STATUE);
            }
            if (!userCode.equals(loanOrder.getOwnerId())) {
                return response.buildFailureResponse(ResCodeEnum.MISMATCH_USER);
            }

            BigDecimal couponAmount = BigDecimal.ZERO;
            String payType = null;
            Integer postponeDays = 0;
            String channel = "manual_pay";

            // 获得当前减免金额
            CouponEntity effectiveBundledCoupon = couponService.getEffectiveBundledCoupon(loanOrder.getOrderId());
            BigDecimal currentCouponAmount = BigDecimal.ZERO;
            if (effectiveBundledCoupon != null) {
                currentCouponAmount = effectiveBundledCoupon.getApplyAmount();
            }

            // 计算还款信息
            if (type.equals("REPAY")) {
                // 最小应还款  LentNumber
                BigDecimal lentNumber = loanOrder.getLentNumber();
                if (amount.compareTo(lentNumber) < 0) {
                    // 还的钱比放款的要少
                    response.setCode(ResCodeEnum.EXCEPTION_CODE.getCode());
                    response.setMessage("至少应还" + lentNumber.toPlainString());
                    return response;
                }
                Item payoffRealItem = billService.payoffRealItem(loanOrder);
                payType = payoffRealItem.getRepaymentType().name();
                BigDecimal payoffSum = payoffRealItem.sum();
                if (payoffSum.compareTo(amount) < 0) {
                    // 还的钱过多
                    response.setCode(ResCodeEnum.EXCEPTION_CODE.getCode());
                    response.setMessage("还清最多可还" + payoffSum.toPlainString());
                    return response;
                }
                couponAmount = payoffSum.subtract(amount);
                if (currentCouponAmount.compareTo(couponAmount) > 0) {
                    response.setCode(ResCodeEnum.ILLEGAL_COUPON_AMOUNT.getCode());
                    response.setMessage("最大减免[" + couponAmount.toPlainString() + "]元, 请更新优惠券金额.");
                    return response;
                }
            } else if (type.equals("POSTPONE")) {
                BigDecimal postponeUnitCharge = loanOrder.getPostponeUnitCharge();
                if (amount.compareTo(postponeUnitCharge) < 0) {
                    // 还的钱比放款的要少
                    response.setCode(ResCodeEnum.EXCEPTION_CODE.getCode());
                    response.setMessage("至少应还" + postponeUnitCharge.toPlainString());
                    return response;
                }
                postponeDays = 7;
                Item postponeRealItem = billService.realItem(loanOrder, PayTypeEnum.REPAY_POSTPONE, postponeDays);
                payType = PayTypeEnum.REPAY_POSTPONE.name();
                BigDecimal postponeSum = postponeRealItem.sum();
                if (postponeSum.compareTo(amount) < 0) {
                    response.setCode(ResCodeEnum.EXCEPTION_CODE.getCode());
                    response.setMessage("延期最多可还" + postponeSum.toPlainString());
                    return response;
                }
                couponAmount = postponeSum.subtract(amount);
                if (currentCouponAmount.compareTo(couponAmount) != 0) {
                    response.setCode(ResCodeEnum.ILLEGAL_COUPON_AMOUNT.getCode());
                    response.setMessage("本次还款应减免[" + couponAmount.toPlainString() + "]元, 请更新优惠券金额.");
                    return response;
                }
            } else {
                return response.buildFailureResponse(ResCodeEnum.UNSUPPORTED_TYPE);
            }

            // 创建还款
            String payOrderId = sockpuppet + "-" + String.valueOf(idWorker.nextId());
            PayOrderEntity payOrder = new PayOrderEntity();
            payOrder.setOrderId(payOrderId);
            payOrder.setStatus(StatusEnum.DEDUCTING.name());
            payOrder.setOwnerId(userCode);
            payOrder.setType(payType);
            payOrder.setApplyNumber(amount);
            payOrder.setPostponeDays(postponeDays);
            payOrder.setLoanOrderId(loanOrder.getOrderId());
            payOrder.setDescription(System.currentTimeMillis() + ":用户线下还款" + amount.toPlainString() + ", 直接创建扣款中还款订单");
            payOrder.setPayCurrency(CurrencyEnum.getDefaultCurrency().name());
            payOrder.setChannel(channel);
            payOrder.create();

            PayOrderLogEntity payOrderLog = new PayOrderLogEntity();
            payOrderLog.setIdCard("000000000000000000");
            payOrderLog.setUserName("线下还款姓名未知");
            payOrderLog.setRepayAmount(amount);
            payOrderLog.setUserCode(userCode);
            payOrderLog.setClientId("线下还款未知");
            payOrderLog.setClientVersion("线下还款未知");
            payOrderLog.setBankCard("线下还款未知");
            payOrderLog.setBankMobile("线下还款未知");
            payOrderLog.setOrderId(loanOrder.getOrderId());
            if (PayTypeEnum.REPAY_POSTPONE.name().equals(payType)) {
                payOrderLog.setFormerRepaymentDate(loanOrder.getRepaymentDate());
            }
            payOrderLog.setPayOrderId(payOrder.getOrderId());
            payOrderLog.setChannel(channel);
            payOrderLog.create();
            payOrderService.savePayOrderAndLog(payOrder, payOrderLog);

            // 模拟先玩后付mq还款通知
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("status", "success");
            params.put("channel", channel);
            params.put("amount", payOrder.getApplyNumber());
            params.put("dealcode", "线下还款未知");
            params.put("channelDealcode", "线下还款未知");
            params.put("orderId", payOrder.getOrderId());
            params.put("offline", true);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("remark", params);

            MqMessage message = new MqMessage("TOPIC", "MQ_RAPTOR_PAYOFF_TAG", jsonObject.toJSONString());
            loanMo9mqListener.consume(message, null);
            logger.info("offline/repay接口结束");
            return response;
        } catch (Exception e) {
            logger.error("线下还款接口报错  ", e);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
    }
}
