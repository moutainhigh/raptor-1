package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.ReqHeaderParams;
import com.mo9.raptor.bean.req.LoanOrderRenewal;
import com.mo9.raptor.bean.req.LoanOrderRepay;
import com.mo9.raptor.bean.req.PayInfoCache;
import com.mo9.raptor.bean.res.ChannelDetailRes;
import com.mo9.raptor.bean.res.PayOderChannelRes;
import com.mo9.raptor.engine.calculator.ILoanCalculator;
import com.mo9.raptor.engine.calculator.LoanCalculatorFactory;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.structure.field.FieldTypeEnum;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.entity.BankEntity;
import com.mo9.raptor.entity.ChannelEntity;
import com.mo9.raptor.entity.PayOrderLogEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.*;
import com.mo9.raptor.redis.RedisParams;
import com.mo9.raptor.redis.RedisServiceApi;
import com.mo9.raptor.service.BankService;
import com.mo9.raptor.service.ChannelService;
import com.mo9.raptor.service.PayOrderLogService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.IDWorker;
import com.mo9.raptor.utils.log.Log;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    private LoanCalculatorFactory loanCalculatorFactory;

    @Resource
    private RedisServiceApi redisServiceApi;

    @Resource(name = "raptorRedis")
    private RedisTemplate raptorRedis;

    @Value("${raptor.sockpuppet}")
    private String sockpuppet;

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
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        String clientId = request.getHeader(ReqHeaderParams.CLIENT_ID);
        String clientVersion = request.getHeader(ReqHeaderParams.CLIENT_VERSION);

        // 用户没删就行, 拉黑也能还
        UserEntity user = userService.findByUserCodeAndDeleted(userCode, false);
        if (user == null) {
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

        ILoanCalculator calculator = loanCalculatorFactory.load(loanOrder);
        Item realItem = calculator.realItem(System.currentTimeMillis(), loanOrder, PayTypeEnum.REPAY_AS_PLAN.name());

        JSONObject data = new JSONObject();

        String code = String.valueOf(idWorker.nextId());

        PayInfoCache payInfoCache = new PayInfoCache();
        payInfoCache.setUserCode(userCode);
        payInfoCache.setLoanOrderId(loanOrderId);
        payInfoCache.setPayType(realItem.getRepaymentType().name());
        payInfoCache.setPayNumber(realItem.sum());
        payInfoCache.setPeriod(0);
        payInfoCache.setUserName(user.getRealName());
        payInfoCache.setIdCard(user.getIdCard());
        payInfoCache.setClientId(clientId);
        payInfoCache.setClientVersion(clientVersion);

        redisServiceApi.set(RedisParams.PAY_CODE + code, payInfoCache, RedisParams.EXPIRE_5M, raptorRedis);

        String url = request.getScheme()+ "://" + request.getServerName() + request.getContextPath() + "/cash/cashier?code=" + code;
        data.put("url", url);

        return response.buildSuccessResponse(data);
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
        int period = req.getPeriod();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        String clientId = request.getHeader(ReqHeaderParams.CLIENT_ID);
        String clientVersion = request.getHeader(ReqHeaderParams.CLIENT_VERSION);
        // 用户没删就行, 拉黑也能还
        UserEntity user = userService.findByUserCodeAndDeleted(userCode, false);
        if (user == null) {
            return response.buildFailureResponse(ResCodeEnum.USER_NOT_EXIST);
        }

        // 延期天数暂时和借款订单脱离关系
        Boolean checkRenewableDays = RenewableDaysEnum.checkRenewableDays(period);
        if (!checkRenewableDays) {
            return response.buildFailureResponse(ResCodeEnum.INVALID_RENEWAL_DAYS);
        }
        Integer basicRenewableDaysTimes = RenewableDaysEnum.getBasicRenewableDaysTimes(period);

        LoanOrderEntity loanOrder = loanOrderService.getByOrderId(req.getLoanOrderId());
        if (loanOrder == null || !StatusEnum.LENT.name().equals(loanOrder.getStatus())) {
            return response.buildFailureResponse(ResCodeEnum.ILLEGAL_LOAN_ORDER_STATUE);
        }
        // 检查用户
        if (!loanOrder.getOwnerId().equals(userCode)) {
            return response.buildFailureResponse(ResCodeEnum.ILLEGAL_REPAYMENT);
        }

        ILoanCalculator calculator = loanCalculatorFactory.load(loanOrder);
        Item realItem = calculator.realItem(System.currentTimeMillis(), loanOrder, PayTypeEnum.REPAY_POSTPONE.name());
        BigDecimal applyAmount = realItem.sum()
                .subtract(realItem.getFieldNumber(FieldTypeEnum.PENALTY))
                .multiply(new BigDecimal(basicRenewableDaysTimes))
                .add(realItem.getFieldNumber(FieldTypeEnum.PENALTY));

        JSONObject data = new JSONObject();

        String code = String.valueOf(idWorker.nextId());

        PayInfoCache payInfoCache = new PayInfoCache();
        payInfoCache.setUserCode(userCode);
        payInfoCache.setLoanOrderId(loanOrderId);
        payInfoCache.setPayType(PayTypeEnum.REPAY_POSTPONE.name());
        payInfoCache.setPayNumber(applyAmount);
        payInfoCache.setPeriod(period);
        payInfoCache.setUserName(user.getRealName());
        payInfoCache.setIdCard(user.getIdCard());
        payInfoCache.setClientId(clientId);
        payInfoCache.setClientVersion(clientVersion);

        redisServiceApi.set(RedisParams.PAY_CODE + code, payInfoCache, RedisParams.EXPIRE_5M, raptorRedis);

        String url = request.getScheme()+ "://" + request.getServerName() + request.getContextPath() + "/cash/cashier?code=" + code;
        data.put("url", url);

        return response.buildSuccessResponse(data);
    }

    @GetMapping("/cashier")
    public String cashier (Model model, @RequestParam String code, HttpServletRequest request) {

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
        /** 默认支付银行卡 */
        model.addAttribute("defaultBank", bank);

        /** 增加可用于扣款的银行卡列表 */
        List<BankEntity> banks = bankService.findByUserCode(payInfoCache.getUserCode());
        model.addAttribute("banks", banks);

        /** 增加代扣渠道列表 */
        List<ChannelEntity> channels = channelService.listByChannelType(ChannelTypeEnum.REPAY.name());
        model.addAttribute("channels", channels);

        return "cashier/pay";
    }

    @GetMapping("/card_cashier")
    public String cardCashier (Model model, @RequestParam String code, @RequestParam String channel, HttpServletRequest request) {

        PayInfoCache payInfoCache =  (PayInfoCache) redisServiceApi.get(RedisParams.PAY_CODE + code, raptorRedis);

        model.addAttribute("code", code);
        if (payInfoCache == null) {
            model.addAttribute("message", ResCodeEnum.PAY_INFO_EXPIRED.getCode());
            /** 返回支付过期页面 */
            return "cashier/feedback_fail";
        }

        model.addAttribute("channel", channel);

        return "cashier/card_adder";
    }

    @PostMapping("/cashier/submit")
    @ResponseBody
    public BaseResponse<JSONObject> cashierSubmit (@RequestParam String code,
                                                   @RequestParam String channel,
                                                   @RequestParam String bankNo) {

        PayInfoCache payInfoCache =  (PayInfoCache) redisServiceApi.get(RedisParams.PAY_CODE + code, raptorRedis);

        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        if (payInfoCache == null) {
            return response.buildFailureResponse(ResCodeEnum.PAY_INFO_EXPIRED);
        }

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

        payOrder.setOwnerId(payInfoCache.getUserCode());
        payOrder.setType(payInfoCache.getPayType());
        payOrder.setApplyNumber(payInfoCache.getPayNumber());
        payOrder.setPostponeDays(payInfoCache.getPeriod());
        payOrder.setLoanOrderId(payInfoCache.getLoanOrderId());

        payOrder.setPayCurrency(CurrencyEnum.getDefaultCurrency().name());
        payOrder.setChannel(channel);
        payOrder.create();

        List<PayOrderEntity> payOrderEntities = payOrderService.listByLoanOrderIdAndType(payInfoCache.getLoanOrderId(), PayTypeEnum.REPAY_POSTPONE);
        PayOrderLogEntity payOrderLog = new PayOrderLogEntity();
        payOrderLog.setIdCard(payInfoCache.getIdCard());
        payOrderLog.setUserName(payInfoCache.getUserName());
        payOrderLog.setRepayAmount(payInfoCache.getPayNumber());
        payOrderLog.setUserCode(payInfoCache.getUserCode());
        payOrderLog.setClientId(payInfoCache.getClientId());
        payOrderLog.setClientVersion(payInfoCache.getClientVersion());
        payOrderLog.setOrderId(payInfoCache.getLoanOrderId());
        payOrderLog.setPostponeCount(payOrderEntities.size() + 1);

        payOrderLog.setPayOrderId(payOrder.getOrderId());
        payOrderLog.setChannel(channel);
        payOrderLog.setBankCard(bankNo);
        payOrderLog.setBankMobile(bank.getMobile());

        payOrderLog.create();
        payOrderService.savePayOrderAndLog(payOrder, payOrderLog);

        redisServiceApi.remove(RedisParams.PAY_CODE + code, raptorRedis);

        PayOderChannelRes res = getRes(orderId, channelEntity.getId());

        JSONObject data = new JSONObject();

        data.put("entities", res);

        return response.buildSuccessResponse(data);

    }

    @PostMapping("/card_cashier/submit")
    @ResponseBody
    public BaseResponse<JSONObject> cardCashierSubmit (@RequestParam String code,
                                   @RequestParam String channel,
                                   @RequestParam String userName,
                                   @RequestParam String idCard,
                                   @RequestParam String bankNo,
                                   @RequestParam String mobile) {

        PayInfoCache payInfoCache =  (PayInfoCache) redisServiceApi.get(RedisParams.PAY_CODE + code, raptorRedis);

        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        if (payInfoCache == null) {
            return response.buildFailureResponse(ResCodeEnum.PAY_INFO_EXPIRED);
        }

        // 检查渠道
        ChannelEntity channelEntity = channelService.getChannelByType(channel, ChannelTypeEnum.REPAY.name());
        if (channelEntity == null) {
            return response.buildFailureResponse(ResCodeEnum.NO_REPAY_CHANNEL);
        }

        String orderId = sockpuppet + "-" + String.valueOf(idWorker.nextId());
        PayOrderEntity payOrder = new PayOrderEntity();
        payOrder.setOrderId(orderId);
        payOrder.setStatus(StatusEnum.PENDING.name());

        payOrder.setOwnerId(payInfoCache.getUserCode());
        payOrder.setType(payInfoCache.getPayType());
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

        payOrderLog.setPayOrderId(payOrder.getOrderId());
        payOrderLog.setChannel(channel);
        payOrderLog.setBankCard(bankNo);
        payOrderLog.setBankMobile(mobile);

        payOrderLog.create();
        payOrderService.savePayOrderAndLog(payOrder, payOrderLog);

        redisServiceApi.remove(RedisParams.PAY_CODE + code, raptorRedis);

        PayOderChannelRes res = getRes(orderId, channelEntity.getId());

        JSONObject data = new JSONObject();

        data.put("entities", res);

        return response.buildSuccessResponse(data);
    }

    @GetMapping("/failed")
    public String failed (Model model, @RequestParam String code, @RequestParam String message, HttpServletRequest request) {

        model.addAttribute("code", code);
        model.addAttribute("message", message);

        return "cashier/feedback_fail";
    }

    /**
     * 获取渠道列表
     * @return
     */
    @GetMapping("/get_repay_channels")
    @ResponseBody
    public BaseResponse<JSONObject> getRepayChannels () {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
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

}
