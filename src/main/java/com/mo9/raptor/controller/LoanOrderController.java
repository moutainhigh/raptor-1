package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.ReqHeaderParams;
import com.mo9.raptor.bean.condition.StrategyCondition;
import com.mo9.raptor.bean.req.OrderAddReq;
import com.mo9.raptor.bean.res.LoanOrderRes;
import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.BillService;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.engine.utils.EngineStaticValue;
import com.mo9.raptor.entity.*;
import com.mo9.raptor.enums.DictTypeNoEnum;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.lock.Lock;
import com.mo9.raptor.lock.RedisService;
import com.mo9.raptor.redis.RedisLockKeySuffix;
import com.mo9.raptor.service.*;
import com.mo9.raptor.utils.IDWorker;
import com.mo9.raptor.utils.RiskUtilsV2;
import com.mo9.raptor.utils.log.Log;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private SpreadChannelService spreadChannelService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private BillService billService;

    @Value("${raptor.sockpuppet}")
    private String sockpuppet;

    @Value("${loan.name.en}")
    private String loanNameEn;

    @Autowired
    private BankService bankService;

    @Autowired
    private StrategyService strategyService;

    @Autowired
    private CardBinInfoService cardBinInfoService;

    @Autowired
    private RiskUtilsV2 riskUtilsV2 ;

    /**
     * 下单
     *
     * @param req
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<JSONObject> add(@Validated @RequestBody OrderAddReq req, HttpServletRequest request) {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        String clientId = request.getHeader(ReqHeaderParams.CLIENT_ID);
        String clientVersion = request.getHeader(ReqHeaderParams.CLIENT_VERSION);
        logger.info("order/add方法开始，usercode:" + userCode);
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
        Lock lock = new Lock(userCode + RedisLockKeySuffix.PRE_LOAN_ORDER_KEY, idWorker.nextId() + "");
        try {
            if (redisService.lock(lock.getName(), lock.getValue(), 1500000, TimeUnit.MILLISECONDS)) {
                LoanOrderEntity payoffOrder = loanOrderService.getLastIncompleteOrder(userCode, StatusEnum.OLD_PAYOFF);
                //没有payoff订单的用户不可以借款
                if (null == payoffOrder) {
                    if(StringUtils.isBlank(user.getSource()) || !spreadChannelService.checkSourceIsAllow(user.getSource())) {
                        logger.warn("用户渠道[{}], 不放款!", user.getSource());
                        return response.buildFailureResponse(ResCodeEnum.NO_LEND_AMOUNT);
                    }
                    // 锁定后检查今天是否还有限额
                    BigDecimal dailyLendAmount = lendOrderService.getDailyLendAmount();
                    if (new BigDecimal(dictData.getName()).compareTo(dailyLendAmount.add(actuallyGetAmount)) < 0) {
                        logger.warn("今日已放款[{}]元, 不再放款!", dailyLendAmount.toPlainString());
                        return response.buildFailureResponse(ResCodeEnum.NO_LEND_AMOUNT);
                    } else {
                        logger.info("今日已放款[{}]元", dailyLendAmount.toPlainString());
                    }
                    logger.warn("新用户开始借款,usercode:" + userCode);
                } else {
                    logger.info("老用户开始借款,usercode:" + userCode);
                }

                // 锁定后再查询 是否有在接订单
                LoanOrderEntity loanOrderEntity = loanOrderService.getLastIncompleteOrder(userCode, StatusEnum.PROCESSING);
                if (loanOrderEntity != null) {
                    return response.buildFailureResponse(ResCodeEnum.ONLY_ONE_ORDER);
                }

                //查询最后一笔订单
                LoanOrderEntity lastLoanOrder = loanOrderService.getLastIncompleteOrder(userCode);
                if(lastLoanOrder != null){
                    if(StatusEnum.PAYOFF.name().equals(lastLoanOrder.getStatus()) || StatusEnum.LENT.name().equals(lastLoanOrder.getStatus())){
                        Boolean canLoan = riskUtilsV2.verifyNeedToBlack(lastLoanOrder) ;
                        if(canLoan){
                            //黑名单
                            return response.buildFailureResponse(ResCodeEnum.NOT_WHITE_LIST_USER);
                        }
                    }
                }

                LoanOrderEntity loanOrder = new LoanOrderEntity();
                loanOrder.setOrderId(orderId);
                loanOrder.setOwnerId(userCode);
                loanOrder.setType(loanNameEn);
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
                if (StringUtils.isBlank(req.getCard())) {
                    lendOrder.setBankCard(bankEntity.getBankNo());
                    lendOrder.setBankName(bankEntity.getBankName());
                } else {
                    if(req.getCard().length() < 7){
                        return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
                    }
                    CardBinInfoEntity byCardPrefix = cardBinInfoService.findByCardPrefix(req.getCard().substring(0, 6));
                    lendOrder.setBankCard(req.getCard());
                    if(byCardPrefix == null){
                        lendOrder.setBankName("银行卡");
                    }else{
                        lendOrder.setBankName(byCardPrefix.getCardBank());
                    }

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

                //检查银行卡是否支持
                StrategyCondition condition = new StrategyCondition(true);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(StrategyCondition.BANK_NAME_CONDITION, lendOrder.getBankName());
                condition.setCondition(jsonObject);
                ResCodeEnum resCodeEnum = strategyService.loanOrderStrategy(condition);
                if(resCodeEnum != ResCodeEnum.SUCCESS){
                    logger.warn("借款订单银行卡不支持userCode={}, bankName={},bankNo={}", userCode, lendOrder.getBankName(), lendOrder.getBankCard());
                    return response.buildFailureResponse(resCodeEnum);
                }

                // 保存借款订单, 还款订单
                loanOrderService.saveLendOrder(loanOrder, lendOrder);
                logger.info("order/add方法结束，usercode:" + userCode + ",loanOrderId:" + loanOrder.getOrderId());
                return response;
            } else {
                logger.warn("用户[{}]预借款[{}], 竞争锁失败", userCode);
                return response.buildFailureResponse(ResCodeEnum.GET_LOCK_FAILED);
            }
        } catch (Exception e) {
            Log.error(logger, e, "借款订单[{}]审核出错", orderId);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        } finally {
            redisService.release(lock);
        }
    }

    /**
     * 获取上一笔未还清订单
     *
     * @param request
     * @return
     */
    @GetMapping("/get_last_incomplete")
    public BaseResponse<Map<String, LoanOrderRes>> getLastIncomplete(HttpServletRequest request) {
        BaseResponse<Map<String, LoanOrderRes>> response = new BaseResponse<>();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        try {
            HashMap<String, LoanOrderRes> map = new HashMap<>(16);
            // 查询订单进行中的状态
            LoanOrderEntity loanOrderEntity = loanOrderService.getLastIncompleteOrder(userCode, StatusEnum.PROCESSING);
            if (loanOrderEntity == null) {
                // 如果没有订单进行中的状态, 则查询用户的所有订单
                loanOrderEntity = loanOrderService.getLastIncompleteOrder(userCode);
                if (loanOrderEntity == null) {
                    // 没有订单, 则直接返回
                    return response;
                }
            }
            Item shouldPayItem = billService.payoffShouldPayItem(loanOrderEntity);

            LoanOrderRes res = new LoanOrderRes();
            res.setOrderId(loanOrderEntity.getOrderId());
            res.setActuallyGet(loanOrderEntity.getLoanNumber().subtract(loanOrderEntity.getChargeValue()).toPlainString());
            res.setRepayAmount(shouldPayItem.sum().toPlainString());
            res.setRepayTime(shouldPayItem.getRepayDate());
            res.setState(String.valueOf(LoanOrderRes.StateEnum.getCode(loanOrderEntity.getStatus())));
            res.setAbateAmount("0");
            LendOrderEntity lendOrderEntity = lendOrderService.getByOrderId(loanOrderEntity.getOrderId());
            if (lendOrderEntity != null) {
                res.setReceiveBankCard(lendOrderEntity.getBankCard());
            }
            res.setRenew(billService.getRenewInfo(loanOrderEntity));
            res.setAgreementUrl("https://www.baidu.com");
            map.put("entity", res);
            return response.buildSuccessResponse(map);
        } catch (Exception e) {
            Log.error(logger, e, "用户[{}]获取上一笔未还清订单错误, ", userCode);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
    }

    /**
     * 判断时间是否在时间段内
     *
     * @param nowTime
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if ((date.equals(begin) || date.after(begin)) && (date.equals(end) || date.before(end))) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        Date nowTime = null;
        Date beginTime = null;
        Date endTime = null;
        try {
            nowTime = df.parse(df.format(new Date()));
            beginTime = df.parse("00:00");
            endTime = df.parse("22:16");
        } catch (Exception e) {
            Log.error(logger, e, "时间解析出错");
        }
        Boolean flag = belongCalendar(nowTime, beginTime, endTime);
        System.out.println(flag);
    }
}
