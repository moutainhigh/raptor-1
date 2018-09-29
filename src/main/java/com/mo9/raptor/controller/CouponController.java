package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.ReqHeaderParams;
import com.mo9.raptor.bean.req.CouponCreateReq;
import com.mo9.raptor.bean.req.OrderAddReq;
import com.mo9.raptor.bean.res.LoanOrderRes;
import com.mo9.raptor.engine.calculator.ILoanCalculator;
import com.mo9.raptor.engine.calculator.LoanCalculatorFactory;
import com.mo9.raptor.engine.entity.CouponEntity;
import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.CouponService;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.engine.utils.EngineStaticValue;
import com.mo9.raptor.entity.BankEntity;
import com.mo9.raptor.entity.DictDataEntity;
import com.mo9.raptor.entity.LoanProductEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.DictTypeNoEnum;
import com.mo9.raptor.enums.PayTypeEnum;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.lock.Lock;
import com.mo9.raptor.lock.RedisService;
import com.mo9.raptor.redis.RedisLockKeySuffix;
import com.mo9.raptor.service.BankService;
import com.mo9.raptor.service.DictService;
import com.mo9.raptor.service.LoanProductService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.IDWorker;
import com.mo9.raptor.utils.log.Log;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private UserService userService;

    @Autowired
    private ILoanOrderService loanOrderService;

    @Autowired
    private ILendOrderService lendOrderService;

    @Autowired
    private LoanProductService productService;

    @Autowired
    private DictService dictService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private LoanCalculatorFactory loanCalculatorFactory;

    @Value("${raptor.sockpuppet}")
    private String sockpuppet;

    @Autowired
    private BankService bankService;

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

        /** 验证是否已存在有效优惠券 */
        CouponEntity couponEntity = couponService.getEffectiveByLoanOrderId(req.getBundleId());
        if (couponEntity != null) {
            return response.buildFailureResponse(ResCodeEnum.EFFECTIVE_COUPON_EXISTED);
        }
        /** 验证优惠是否超额 ：优惠金额 <= 当前应还 - （最小应还 - 已入账借贷还款） */
        LoanOrderEntity loanOrder = loanOrderService.getByOrderId(req.getBundleId());
        if (loanOrder == null) {
            return response.buildFailureResponse(ResCodeEnum.LOAN_ORDER_NOT_EXISTED);
        }
        ILoanCalculator loanCalculator = loanCalculatorFactory.load(loanOrder);
        BigDecimal minRepay = loanCalculator.minRepay(loanOrder);
        Item realItem = loanCalculator.realItem(System.currentTimeMillis(), loanOrder, PayTypeEnum.REPAY_AS_PLAN.name());
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

        /** 创建优惠券 */

        return response;
    }

}
