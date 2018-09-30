package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.req.CouponCreateReq;
import com.mo9.raptor.bean.req.CouponUpdateReq;
import com.mo9.raptor.engine.calculator.ILoanCalculator;
import com.mo9.raptor.engine.calculator.LoanCalculatorFactory;
import com.mo9.raptor.engine.entity.CouponEntity;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.CouponService;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.engine.utils.EngineStaticValue;
import com.mo9.raptor.engine.utils.TimeUtils;
import com.mo9.raptor.enums.PayTypeEnum;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.lock.Lock;
import com.mo9.raptor.lock.RedisService;
import com.mo9.raptor.redis.RedisLockKeySuffix;
import com.mo9.raptor.utils.IDWorker;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 优惠券
 * Created by gqwu on 2018/9/29.
 */
@RestController()
@RequestMapping("/coupon")
public class CouponController {

    private static Logger logger = Log.get();
    @Autowired
    private IDWorker idWorker;

    @Autowired
    private ILoanOrderService loanOrderService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private LoanCalculatorFactory loanCalculatorFactory;

    @Autowired
    private CouponService couponService;

    @Autowired
    private IPayOrderService payOrderService;

    /**
     * 创建
     * @param req
     * @return
     */
    @PostMapping("/create")
    public BaseResponse<JSONObject> create(@Valid @RequestBody CouponCreateReq req, HttpServletRequest request) {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();

        /** 验证签名 */

        /** 验证优惠是否超额 ：优惠金额 <= 当前应还 - （最小应还 - 已入账实际还款） */
        LoanOrderEntity loanOrder = loanOrderService.getByOrderId(req.getBundleId());
        if (loanOrder == null || loanOrder.getStatus().equals(StatusEnum.LENDING.name())) {
            return response.buildFailureResponse(ResCodeEnum.LOAN_ORDER_NOT_EXISTED);
        }

        ILoanCalculator loanCalculator = loanCalculatorFactory.load(loanOrder);
        BigDecimal minRepay = loanCalculator.minRepay(loanOrder);
        Item realItem = loanCalculator.realItem(System.currentTimeMillis(), loanOrder, PayTypeEnum.REPAY_AS_PLAN.name(), 0);
        BigDecimal allShouldPay = realItem.sum();
        List<PayOrderEntity> payOrders = payOrderService.listEntryDonePayLoan(req.getBundleId(), PayTypeEnum.PAY_LOAN);
        BigDecimal payLoan = BigDecimal.ZERO;
        if (payOrders != null && payOrders.size() > 0) {
            for (PayOrderEntity payOrder: payOrders) {
                payLoan = payLoan.add(payOrder.getEntryNumber());
            }
        }
        BigDecimal couldCoupon = allShouldPay.subtract(minRepay.subtract(payLoan));
        if (req.getNumber().compareTo(couldCoupon) > 0) {
            return response.buildFailureResponse(ResCodeEnum.INVALID_COUPON_NUMBER);
        }

        /** 为订单优惠券创建行为加锁，但不为还款行为加锁，所以当并发发生还款行为时，可导致后续可优惠金额增大，可忽略 */
        // 锁定用户借款行为
        Lock lock = new Lock(loanOrder.getOrderId() + RedisLockKeySuffix.LOAN_COUPON_CREATE_KEY, idWorker.nextId()+"");

        try {
            if (redisService.lock(lock.getName(), lock.getValue(), 1500000, TimeUnit.MILLISECONDS)) {

                /** 验证是否已存在有效优惠券 */
                CouponEntity effectiveCoupon = couponService.getEffectiveBundledCoupon(req.getBundleId());
                if (effectiveCoupon == null) {
                    /** 创建优惠券 */
                    effectiveCoupon = new CouponEntity();
                    effectiveCoupon.setCouponId(String.valueOf(idWorker.nextId()));
                    Long today = TimeUtils.extractDateTime(System.currentTimeMillis());
                    effectiveCoupon.setEffectiveDate(today);
                    effectiveCoupon.setExpireDate(today + EngineStaticValue.DAY_MILLIS);
                    effectiveCoupon.setStatus(StatusEnum.BUNDLED.name());
                    effectiveCoupon.setCreator(req.getCreator());
                    effectiveCoupon.setReason(req.getReason());
                    effectiveCoupon.setBoundOrderId(req.getBundleId());
                } else {
                    logger.info("操作者[{}]将优惠券[{}]由于[{}]原因将金额由[{}]更改为[{}]", req.getCreator(), effectiveCoupon.getCouponId(), req.getReason(), effectiveCoupon.getApplyAmount(), req.getNumber());
                }

                /**
                 * 仅仅可以更新金额
                 */
                effectiveCoupon.setApplyAmount(req.getNumber());
                couponService.save(effectiveCoupon);

                response.setCode(0);
                response.setMessage("成功");

                return response;
            } else {
                logger.warn("借款订单[{}]优惠券创建竞争锁失败", loanOrder.getOrderId());
                return response.buildFailureResponse(ResCodeEnum.GET_LOCK_FAILED);
            }
        } catch (Exception e) {
            Log.error(logger , e ,"借款订单[{}]优惠券创建异常", loanOrder.getOrderId());
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        } finally {
            redisService.release(lock);
        }
    }

    /**
     * 更新, 暂时无用
     * @param req
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<JSONObject> update(@Valid @RequestBody CouponUpdateReq req, HttpServletRequest request) {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();

        return response;
    }

}
