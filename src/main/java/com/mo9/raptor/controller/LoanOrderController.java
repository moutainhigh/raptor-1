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
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 借款
 * Created by xzhang on 2018/9/13.
 */
@RestController()
@RequestMapping("/order")
public class LoanOrderController {

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

        // 检查用户是否存在及是否合法
        UserEntity user = userService.findByUserCodeAndStatus(userCode, StatusEnum.PASSED);
        if (user == null) {
            return response.buildFailureResponse(ResCodeEnum.USER_NOT_EXIST);
        }

        // 检查是否有每日限额配置, 没配直接不让借
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat = sdf.format(new Date());
        DictDataEntity dictData = dictService.findDictData(DictTypeNoEnum.DAILY_LEND_AMOUNT.name(), dateFormat);
        if (dictData == null) {
            return response.buildFailureResponse(ResCodeEnum.NO_LEND_AMOUNT);
        }

        // 检查借款参数是否合法
        BigDecimal principal = req.getCapital();
        int loanTerm = req.getPeriod();
        LoanProductEntity product = productService.findByAmountAndPeriod(principal, loanTerm);
        if (product == null) {
            return response.buildFailureResponse(ResCodeEnum.ERROR_LOAN_PARAMS);
        }
        BigDecimal actuallyGetAmount = product.getActuallyGetAmount();
        BigDecimal amount = product.getAmount();
        if (amount.compareTo(actuallyGetAmount) < 0) {
            return response.buildFailureResponse(ResCodeEnum.PRODUCT_ERROR);
        }

        String orderId = sockpuppet + "-" + idWorker.nextId();
        // 锁定用户借款行为
        Lock lock = new Lock(userCode + RedisLockKeySuffix.PRE_LOAN_ORDER_KEY, idWorker.nextId()+"");
        try {
            if (redisService.lock(lock.getName(), lock.getValue(), 5000, TimeUnit.MILLISECONDS)) {
                // 锁定后检查今天是否还有限额
                BigDecimal dailyLendAmount = lendOrderService.getDailyLendAmount();
                if (new BigDecimal(dictData.getName()).compareTo(dailyLendAmount.add(principal)) <= 0) {
                    logger.warn("今日已放款[{}]元, 不再放款!", dailyLendAmount.toPlainString());
                    return response.buildFailureResponse(ResCodeEnum.NO_LEND_AMOUNT);
                }

                // 锁定后再查询 是否有在接订单
                LoanOrderEntity loanOrderEntity = loanOrderService.getLastIncompleteOrder(userCode, StatusEnum.PROCESSING);
                if (loanOrderEntity != null) {
                    return response.buildFailureResponse(ResCodeEnum.ONLY_ONE_ORDER);
                }

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
                loanOrder.setRepaymentDate(now + (loanTerm - 1) * EngineStaticValue.DAY_MILLIS);
                loanOrder.setCreateTime(now);
                loanOrder.setUpdateTime(now);

                LendOrderEntity lendOrder = new LendOrderEntity();
                lendOrder.setOrderId(String.valueOf(idWorker.nextId()));
                lendOrder.setOwnerId(userCode);
                lendOrder.setApplyUniqueCode(orderId);
                lendOrder.setApplyNumber(loanOrder.getLoanNumber().subtract(loanOrder.getChargeValue()));
                lendOrder.setApplyTime(System.currentTimeMillis());
                BankEntity bankEntity = bankService.findByUserCodeLastOne(loanOrder.getOwnerId());
                if (bankEntity == null) {
                    return response.buildFailureResponse(ResCodeEnum.NO_LEND_INFO);
                }
                lendOrder.setUserName(bankEntity.getUserName());
                lendOrder.setIdCard(bankEntity.getCardId());
                lendOrder.setBankName(bankEntity.getBankName());
                if (StringUtils.isBlank(req.getCard())) {
                    lendOrder.setBankCard(bankEntity.getBankNo());
                } else {
                    lendOrder.setBankCard(req.getCard());
                }
                if (StringUtils.isBlank(req.getCardMobile())) {
                    lendOrder.setBankMobile(bankEntity.getMobile());
                } else {
                    lendOrder.setBankMobile(req.getCardMobile());
                }
                lendOrder.setStatus(StatusEnum.PENDING.name());
                lendOrder.setType("");
                lendOrder.setCreateTime(now);
                lendOrder.setUpdateTime(now);

                // 保存借款订单, 还款订单
                loanOrderService.saveLendOrder(loanOrder, lendOrder);
                return response;
            } else {
                logger.warn("用户[{}]预借款[{}], 竞争锁失败", userCode);
                return response.buildFailureResponse(ResCodeEnum.GET_LOCK_FAILED);
            }
        } catch (Exception e) {
            Log.error(logger , e ,"借款订单[{}]审核出错", orderId);
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
    public BaseResponse<Map<String,LoanOrderRes>> getLastIncomplete(HttpServletRequest request) {
        BaseResponse<Map<String,LoanOrderRes>> response = new BaseResponse<>();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        try {
            HashMap<String,LoanOrderRes> map = new HashMap<>(16);
            LoanOrderEntity loanOrderEntity = loanOrderService.getLastIncompleteOrder(userCode);
            if (loanOrderEntity == null) {
                return response;
            }
            ILoanCalculator calculator = loanCalculatorFactory.load(loanOrderEntity);
            Item realItem = calculator.realItem(System.currentTimeMillis(), loanOrderEntity, PayTypeEnum.REPAY_AS_PLAN.name());
            // System.out.println(JSONObject.toJSONString(realItem, SerializerFeature.PrettyFormat));

            LoanOrderRes res = new LoanOrderRes();
            res.setOrderId(loanOrderEntity.getOrderId());
            res.setActuallyGet(loanOrderEntity.getLoanNumber().subtract(loanOrderEntity.getChargeValue()).toPlainString());
            res.setRepayAmount(realItem.sum().toPlainString());
            res.setRepayTime(realItem.getRepayDate());
            res.setState(String.valueOf(LoanOrderRes.StateEnum.getCode(loanOrderEntity.getStatus())));
            res.setAbateAmount("0");
            LendOrderEntity lendOrderEntity = lendOrderService.getByOrderId(loanOrderEntity.getOrderId());
            if (lendOrderEntity != null) {
                res.setReceiveBankCard(lendOrderEntity.getBankCard());
            }
            res.setRenew(calculator.getRenew(loanOrderEntity));
            res.setAgreementUrl("https://www.baidu.com");
            map.put("entity",res);
            return response.buildSuccessResponse(map);
        } catch (Exception e) {
            Log.error(logger , e , "用户[{}]获取上一笔未还清订单错误, ", userCode);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
    }

}
