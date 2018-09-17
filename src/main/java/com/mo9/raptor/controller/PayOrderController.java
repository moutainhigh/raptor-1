package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.ReqHeaderParams;
import com.mo9.raptor.bean.req.CashRenewalReq;
import com.mo9.raptor.bean.req.CashRepayReq;
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
import com.mo9.raptor.entity.ChannelEntity;
import com.mo9.raptor.entity.PayOrderLogEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.*;
import com.mo9.raptor.service.ChannelService;
import com.mo9.raptor.service.PayOrderLogService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.IDWorker;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;

/**
 * 还款
 * Created by xzhang on 2018/9/13.
 */
@RestController()
@RequestMapping("/cash")
public class PayOrderController {

    private static final Logger logger = LoggerFactory.getLogger(PayOrderController.class);

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
    private LoanCalculatorFactory loanCalculatorFactory;

    @Value("${raptor.sockpuppet}")
    private String sockpuppet;

    /**
     * 还清
     * @param req
     * @return
     */
    @PostMapping("/repay")
    public BaseResponse<JSONObject> repay(@Valid @RequestBody CashRepayReq req, HttpServletRequest request) {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        String clientId = request.getHeader(ReqHeaderParams.CLIENT_ID);
        String clientVersion = request.getHeader(ReqHeaderParams.CLIENT_VERSION);
        // 用户没删就行, 拉黑也能还
        UserEntity user = userService.findByUserCodeAndDeleted(userCode, false);
        if (user == null) {
            return response.buildFailureResponse(ResCodeEnum.USER__NOT_EXIST);
        }

        // 检查可用渠道
        ChannelEntity channelEntity = channelService.getByChannelId(req.getChannelType());
        if (channelEntity == null || !channelEntity.getChannelType().equals(ChannelTypeEnum.REPAY.name())) {
            return response.buildFailureResponse(ResCodeEnum.NO_REPAY_CHANNEL);
        }

        // 获得订单
        String loanOrderId = req.getOrderId();
        LoanOrderEntity loanOrder = loanOrderService.getByOrderId(loanOrderId);
        if (loanOrder == null || !StatusEnum.LENT.name().equals(loanOrder.getStatus())) {
            return response.buildFailureResponse(ResCodeEnum.ILLEGAL_LOAN_ORDER_STATUE);
        }
        // 检查用户
        if (!loanOrder.getOwnerId().equals(userCode)) {
            return response.buildFailureResponse(ResCodeEnum.ILLEGAL_REPAYMENT);
        }

        String orderId = sockpuppet + "-" + String.valueOf(idWorker.nextId());
        PayOrderEntity payOrder = new PayOrderEntity();
        payOrder.setOrderId(orderId);
        payOrder.setStatus(StatusEnum.PENDING.name());
        payOrder.setOwnerId(userCode);

        ILoanCalculator calculator = loanCalculatorFactory.load(loanOrder);
        Item realItem = calculator.realItem(System.currentTimeMillis(), loanOrder);
        payOrder.setType(realItem.getRepaymentType().name());
        payOrder.setApplyNumber(realItem.sum());
        payOrder.setPostponeDays(0);
        payOrder.setPayCurrency(CurrencyEnum.getDefaultCurrency().name());
        payOrder.setLoanOrderId(loanOrderId);
        payOrder.setChannel(channelEntity.getChannel());
        payOrder.create();
        PayOrderLogEntity payOrderLog = new PayOrderLogEntity();
        payOrderLog.setOrderId(payOrder.getLoanOrderId());
        payOrderLog.setPayOrderId(payOrder.getOrderId());
        payOrderLog.setBankCard(req.getBankCard());
        payOrderLog.setBankMobile(req.getBankMobile());
        payOrderLog.setIdCard(req.getIdCard());
        payOrderLog.setUserName(req.getUserName());
        payOrderLog.setChannel(channelEntity.getChannel());
        payOrderLog.setRepayAmount(payOrder.getApplyNumber());
        payOrderLog.setUserCode(userCode);
        payOrderLog.setClientId(clientId);
        payOrderLog.setClientVersion(clientVersion);
        payOrderLog.create();
        payOrderService.savePayOrderAndLog(payOrder, payOrderLog);

        PayOderChannelRes res = getRes(orderId, channelEntity.getId());
        JSONObject data = new JSONObject();
        data.put("entities", res);
        return response.buildSuccessResponse(data);
    }

    /**
     * 续期
     * @param req
     * @return
     */
    @PostMapping("/renewal")
    public BaseResponse<JSONObject> renewal(@Valid @RequestBody CashRenewalReq req, HttpServletRequest request) {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        String clientId = request.getHeader(ReqHeaderParams.CLIENT_ID);
        String clientVersion = request.getHeader(ReqHeaderParams.CLIENT_VERSION);
        // 用户没删就行, 拉黑也能还
        UserEntity user = userService.findByUserCodeAndDeleted(userCode, false);
        if (user == null) {
            return response.buildFailureResponse(ResCodeEnum.USER__NOT_EXIST);
        }

        // 检查可用渠道
        ChannelEntity channelEntity = channelService.getByChannelId(req.getChannelType());
        if (channelEntity == null || !channelEntity.getChannelType().equals(ChannelTypeEnum.REPAY.name())) {
            return response.buildFailureResponse(ResCodeEnum.NO_REPAY_CHANNEL);
        }

        // 延期天数暂时和借款订单脱离关系
        Boolean checkRenewableDays = RenewableDaysEnum.checkRenewableDays(req.getPeriod());
        if (!checkRenewableDays) {
            return response.buildFailureResponse(ResCodeEnum.INVALID_RENEWAL_DAYS);
        }
        Integer basicRenewableDaysTimes = RenewableDaysEnum.getBasicRenewableDaysTimes(req.getPeriod());

        // 获得订单
        String loanOrderId = req.getOrderId();
        LoanOrderEntity loanOrder = loanOrderService.getByOrderId(loanOrderId);
        if (loanOrder == null || !StatusEnum.LENT.name().equals(loanOrder.getStatus())) {
            return response.buildFailureResponse(ResCodeEnum.ILLEGAL_LOAN_ORDER_STATUE);
        }
        // 检查用户
        if (!loanOrder.getOwnerId().equals(userCode)) {
            return response.buildFailureResponse(ResCodeEnum.ILLEGAL_REPAYMENT);
        }

        String orderId = sockpuppet + "-" + String.valueOf(idWorker.nextId());
        PayOrderEntity payOrder = new PayOrderEntity();
        payOrder.setOrderId(orderId);
        payOrder.setStatus(StatusEnum.PENDING.name());
        payOrder.setOwnerId(userCode);
        payOrder.setType(PayTypeEnum.REPAY_POSTPONE.name());

        ILoanCalculator calculator = loanCalculatorFactory.load(loanOrder);
        Item realItem = calculator.realItem(System.currentTimeMillis(), loanOrder);
        BigDecimal applyAmount = realItem.sum()
                .subtract(realItem.getFieldNumber(FieldTypeEnum.PRINCIPAL))
                .subtract(realItem.getFieldNumber(FieldTypeEnum.PENALTY))
                .multiply(new BigDecimal(basicRenewableDaysTimes))
                .add(realItem.getFieldNumber(FieldTypeEnum.PENALTY));
        payOrder.setApplyNumber(applyAmount);

        payOrder.setPostponeDays(req.getPeriod());
        payOrder.setPayCurrency(CurrencyEnum.getDefaultCurrency().name());
        payOrder.setLoanOrderId(loanOrderId);
        payOrder.setChannel(channelEntity.getChannel());
        payOrder.create();

        PayOrderLogEntity payOrderLog = new PayOrderLogEntity();
        payOrderLog.setOrderId(payOrder.getLoanOrderId());
        payOrderLog.setPayOrderId(payOrder.getOrderId());
        payOrderLog.setBankCard(req.getBankCard());
        payOrderLog.setBankMobile(req.getBankMobile());
        payOrderLog.setIdCard(req.getIdCard());
        payOrderLog.setUserName(req.getUserName());
        payOrderLog.setChannel(channelEntity.getChannel());
        payOrderLog.setRepayAmount(payOrder.getApplyNumber());
        payOrderLog.setUserCode(userCode);
        payOrderLog.setClientId(clientId);
        payOrderLog.setClientVersion(clientVersion);
        payOrderLog.create();
        payOrderService.savePayOrderAndLog(payOrder, payOrderLog);

        PayOderChannelRes res = getRes(orderId, channelEntity.getId());
        JSONObject data = new JSONObject();
        data.put("entities", res);
        return response.buildSuccessResponse(data);
    }

    /**
     * 获取渠道列表
     * @return
     */
    @GetMapping("/get_repay_channels")
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
