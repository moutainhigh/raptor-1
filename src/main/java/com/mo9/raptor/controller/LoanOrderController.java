package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.ReqHeaderParams;
import com.mo9.raptor.bean.req.OrderAddReq;
import com.mo9.raptor.bean.res.LoanOrderRes;
import com.mo9.raptor.engine.calculator.ILoanCalculator;
import com.mo9.raptor.engine.calculator.LoanCalculatorFactory;
import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.state.event.impl.AuditLaunchEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.engine.utils.EngineStaticValue;
import com.mo9.raptor.engine.utils.TimeUtils;
import com.mo9.raptor.entity.DictDataEntity;
import com.mo9.raptor.entity.LoanProductEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.DictTypeNoEnum;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.lock.Lock;
import com.mo9.raptor.lock.RedisService;
import com.mo9.raptor.redis.RedisLockKeySuffix;
import com.mo9.raptor.service.DictService;
import com.mo9.raptor.service.LoanProductService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.IDWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 还款
 * Created by xzhang on 2018/9/13.
 */
@RestController()
@RequestMapping("/order")
public class LoanOrderController {

    private static final Logger logger = LoggerFactory.getLogger(LoanOrderController.class);

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
    private IEventLauncher loanEventLauncher;

    @Autowired
    private LoanCalculatorFactory loanCalculatorFactory;

    @Value("${raptor.sockpuppet}")
    private String sockpuppet;

    /**
     * 下单
     * @param req
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<JSONObject> add(@Valid @RequestBody OrderAddReq req, HttpServletRequest request) {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        String clientId = request.getHeader(ReqHeaderParams.CLIENT_ID);
        String clientVersion = request.getHeader(ReqHeaderParams.CLIENT_VERSION);

        UserEntity user = userService.findByUserCodeAndStatus(userCode, StatusEnum.PASSED);
        if (user == null) {
            return response.buildFailureResponse(ResCodeEnum.USER_NOT_EXIST);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat = sdf.format(new Date());
        DictDataEntity dictData = dictService.findDictData(DictTypeNoEnum.DAILY_LEND_AMOUNT.name(), dateFormat);
        if (dictData == null) {
            return response.buildFailureResponse(ResCodeEnum.NO_LEND_AMOUNT);
        }
        BigDecimal dailyLendAmount = lendOrderService.getDailyLendAmount();
        if (new BigDecimal(dictData.getName()).compareTo(dailyLendAmount) <= 0) {
            logger.warn("今日已放款[{}]元, 不再放款!", dailyLendAmount.toPlainString());
            return response.buildFailureResponse(ResCodeEnum.NO_LEND_AMOUNT);
        }

        LoanOrderEntity loanOrderEntity = loanOrderService.getLastIncompleteOrder(userCode);
        if (loanOrderEntity != null) {
            return response.buildFailureResponse(ResCodeEnum.ONLY_ONE_ORDER);
        }

        BigDecimal principal = req.getCapital();
        int loanTerm = req.getPeriod();
        LoanProductEntity product = productService.findByAmountAndPeriod(principal, loanTerm);
        if (product == null) {
            return response.buildFailureResponse(ResCodeEnum.ERROR_LOAN_PARAMS);
        }

        String orderId = sockpuppet + "-" + idWorker.nextId();
        // 锁定用户
        Lock lock = new Lock(userCode + RedisLockKeySuffix.PRE_LOAN_ORDER_KEY, idWorker.nextId()+"");
        try {
            if (redisService.lock(lock.getName(), lock.getValue(), 5000, TimeUnit.MILLISECONDS)) {
                LoanOrderEntity loanOrder = new LoanOrderEntity();
                loanOrder.setOrderId(orderId);
                loanOrder.setOwnerId(userCode);
                loanOrder.setType("RAPTOR");
                loanOrder.setLoanNumber(principal);
                loanOrder.setPostponeUnitCharge(product.getRenewalBaseAmount());
                loanOrder.setLoanTerm(loanTerm);
                loanOrder.setStatus(StatusEnum.PENDING.name());
                loanOrder.setInterestValue(product.getInterest());
                loanOrder.setPenaltyValue(product.getPenaltyForDay());
                loanOrder.setChargeValue(principal.subtract(product.getActuallyGetAmount()));
                loanOrder.setClientId(clientId);
                loanOrder.setClientVersion(clientVersion);

                long now = System.currentTimeMillis();
                Long today = TimeUtils.extractDateTime(now);
                loanOrder.setRepaymentDate(today + loanTerm * EngineStaticValue.DAY_MILLIS);
                loanOrder.setCreateTime(now);
                loanOrder.setUpdateTime(now);
                /** 创建借款订单 */
                loanOrderService.save(loanOrder);
                AuditLaunchEvent event = new AuditLaunchEvent(userCode, loanOrder.getOrderId());
                loanEventLauncher.launch(event);
                return response;
            } else {
                logger.warn("用户[{}]预借款[{}], 竞争锁失败", userCode);
                return response.buildFailureResponse(ResCodeEnum.GET_LOCK_FAILED);
            }
        } catch (Exception e) {
            logger.error("借款订单[{}]审核出错", orderId, e);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        } finally {
            redisService.unlock(lock.getName());
        }
    }

    /**
     * 获取上一笔未还清订单
     * @param request
     * @return
     */
    @GetMapping("/get_last_incomplete")
    public BaseResponse<LoanOrderRes> getLastIncomplete(HttpServletRequest request) {
        BaseResponse<LoanOrderRes> response = new BaseResponse<LoanOrderRes>();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        LoanOrderEntity loanOrderEntity = loanOrderService.getLastIncompleteOrder(userCode);
        if (loanOrderEntity == null) {
            return response;
        }
        ILoanCalculator calculator = loanCalculatorFactory.load(loanOrderEntity);
        Item realItem = calculator.realItem(System.currentTimeMillis(), loanOrderEntity);

        LoanOrderRes res = new LoanOrderRes();
        res.setOrderId(loanOrderEntity.getOrderId());
        res.setRepayAmount(realItem.sum().toPlainString());
        res.setRepayTime(loanOrderEntity.getRepaymentDate());
        res.setState(loanOrderEntity.getStatus());
        res.setAbateAmount("0");
        LendOrderEntity lendOrderEntity = lendOrderService.getByOrderId(loanOrderEntity.getOrderId());
        res.setReceiveBankCard(lendOrderEntity.getBankCard());
        res.setRenew(calculator.getRenew(loanOrderEntity));
        return response.buildSuccessResponse(res);
    }

}
