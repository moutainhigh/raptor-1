package com.mo9.raptor.mq.listen;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.condition.FetchPayOrderCondition;
import com.mo9.raptor.bean.res.RepayDetailRes;
import com.mo9.raptor.bean.res.RepayInfoMqRes;
import com.mo9.raptor.engine.entity.CouponEntity;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.*;
import com.mo9.raptor.engine.state.event.impl.pay.DeductResponseEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.engine.structure.Unit;
import com.mo9.raptor.engine.structure.field.FieldTypeEnum;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.engine.utils.TimeUtils;
import com.mo9.raptor.entity.PayOrderLogEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.BusinessTypeEnum;
import com.mo9.raptor.enums.CreditStatusEnum;
import com.mo9.raptor.enums.PayTypeEnum;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.mq.producer.RabbitProducer;
import com.mo9.raptor.service.*;
import com.mo9.raptor.utils.log.Log;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xtgu
 * @date :   2018-07-10 14:40
 */
@Component
public class SeekerListener {

    private static Logger logger = Log.get();

    @Autowired
    private IPayOrderService payOrderService;

    @Autowired
    private PayOrderLogService payOrderLogService;

    @Autowired
    private ILoanOrderService loanOrderService;


    @Autowired
    private IPayOrderDetailService payOrderDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private IEventLauncher payEventLauncher;

    @Autowired
    private BillService billService;

    @Autowired
    private RabbitProducer rabbitProducer;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CashAccountService cashAccountService ;

    @Value("${raptor.sockpuppet}")
    private String sockpuppet;

    /**
     * 钱包充值 提现mq
     * @param message
     * @param channel
     * @param messageId
     * @param tag
     * @throws IOException
     */
    @RabbitListener(queues = "${raptor.mq.seeker.listener}")
    public void walletListener(
                                 Message message,
                                 Channel channel,
                                 @Header(AmqpHeaders.MESSAGE_ID) String messageId,
                                 @Header(AmqpHeaders.DELIVERY_TAG) long tag
                                 ) throws IOException {
        logger.info("接收到支付中心消息, messageId: {}, message: {}", messageId, new String(message.getBody() , "UTF-8"));
        /**
         * 原mq TAG字段 , 用来锁定业务类型
         */
        String messageTag = message.getMessageProperties().getReceivedRoutingKey();
        JSONObject mqMessage = JSON.parseObject(new String(message.getBody() , "UTF-8")) ;
        if("REMIT_NOTICE_TTYQ".equals(messageTag)){
            //充值
            payoff(mqMessage , channel , tag) ;
        }

    }

    /**
     * 还款
     * @param mqMessage
     * @param channelReturn
     * @param tag
     * @return
     */
    private void payoff(JSONObject mqMessage , Channel channelReturn, long tag) {

        String status = mqMessage.getString("status");
        String orderId = mqMessage.getString("businessCode");
        String channel = StringUtils.isBlank(mqMessage.getString("channelCode")) ? "未知":mqMessage.getString("channelCode");
        BigDecimal amount = mqMessage.getBigDecimal("orderAmount");
        String dealcode = mqMessage.getString("orderCode");
        String channelDealcode = mqMessage.getString("invoice");
        // 失败原因
        String failReason = mqMessage.getString("remark");
        DeductResponseEvent event;
        if ("SUCCESS".equals(status)) {
            // 还款扣款成功事件
            event = new DeductResponseEvent(orderId, amount, true, "还款" + amount.toPlainString());
        } else if("FAILED".equals(status)){
            event = new DeductResponseEvent(orderId, null, false, "还款失败");
        }else{
            //未知状态 , 不做处理
            try {
                channelReturn.basicAck(tag, false);
            } catch (Exception e) {
                logger.error( "commit Mq 异常" , e );
            }
            return ;
        }
        PayOrderLogEntity payOrderLog = payOrderLogService.getByPayOrderId(orderId);
        if (payOrderLog == null) {
            logger.error("还款订单号[{}], 查不到对应的还款log, 可能由于放款请求未结束而MQ先到, 延后再次发送MQ", orderId);
            try {
                channelReturn.basicReject(tag , false);
            } catch (Exception e) {
                logger.error( " no commit Mq 异常" , e );
            }
            return ;
        }
        payOrderLog.setChannel(channel);
        payOrderLog.setThirdChannelNo(channelDealcode);
        payOrderLog.setDealCode(dealcode);
        payOrderLog.setChannelResponse(mqMessage.toJSONString());
        payOrderLog.setChannelRepayNumber(amount);
        payOrderLog.setFailReason(failReason);
        payOrderLogService.save(payOrderLog);
        //查询还款订单状态
        PayOrderEntity payOrderEntityTemp = payOrderService.getByOrderId(orderId);
        if(StatusEnum.END_REPAY.contains(payOrderEntityTemp.getStatus())){
            //已处理
            try {
                channelReturn.basicAck(tag, false);
            } catch (Exception e) {
                logger.error( "commit Mq 异常" , e );
            }
            return ;
        }
        // 发送还款扣款成功事件
        try {
            try {
                if("SUCCESS".equals(status)){
                    //保存 用户流水
                    ResCodeEnum resCodeEnum = ResCodeEnum.SUCCESS ;
                    if(PayTypeEnum.REPAY_POSTPONE.name().equals(payOrderEntityTemp.getType())){
                        resCodeEnum = cashAccountService.recharge(payOrderLog.getUserCode() , payOrderLog.getChannelRepayNumber(), payOrderLog.getPayOrderId() , BusinessTypeEnum.ONLINE_POSTPONE);
                    }else{
                        resCodeEnum = cashAccountService.recharge(payOrderLog.getUserCode() , payOrderLog.getChannelRepayNumber(), payOrderLog.getPayOrderId() , BusinessTypeEnum.ONLINE_REPAY);
                    }
                    if(ResCodeEnum.SUCCESS != resCodeEnum && ResCodeEnum.CASH_ACCOUNT_BUSINESS_NO_IS_EXIST != resCodeEnum){
                        logger.error("用户" + payOrderLog.getUserCode() +  " 还款现金账户处理" + payOrderLog.getPayOrderId()  + "异常 : " + resCodeEnum );
                        channelReturn.basicAck(tag, false);
                        return ;
                    }
                }

            } catch (Exception e) {
                Log.error(logger , e , "还款成功保存银行卡异常 orderId : " + orderId);
            }

            payEventLauncher.launch(event);
        } catch (Exception e) {
            Log.error(logger , e , "发送还款订单[{}]还款成功事件异常", orderId);
        }

        //修改或者存储银行卡信息

        if ("SUCCESS".equals(status)) {
            PayOrderEntity payOrderEntity = payOrderService.getByOrderId(orderId);
            LoanOrderEntity loanOrderEntity = loanOrderService.getByOrderId(payOrderEntity.getLoanOrderId());
            String payOrderStatus = payOrderEntity.getStatus();
            // 入账成功才向贷后发消息
            if (StatusEnum.ENTRY_DONE.name().equals(payOrderStatus)) {
                // 增加延期次数
                List<FetchPayOrderCondition.Type> type = new ArrayList<FetchPayOrderCondition.Type>();
                type.add(FetchPayOrderCondition.Type.REPAY_POSTPONE);
                List<FetchPayOrderCondition.Status> statuses = new ArrayList<FetchPayOrderCondition.Status>();
                statuses.add(FetchPayOrderCondition.Status.ENTRY_DONE);
                FetchPayOrderCondition condition = new FetchPayOrderCondition();
                condition.setTypes(type);
                condition.setStates(statuses);
                condition.setLoanOrderNumber(payOrderEntity.getLoanOrderId());
                Page<PayOrderEntity> payOrderEntities = payOrderService.listPayOrderByCondition(condition);
                int count = payOrderEntities.getContent().size();
                loanOrderEntity.setPostponeCount(count);
                loanOrderEntity.setUpdateTime(System.currentTimeMillis());
                loanOrderService.save(loanOrderEntity);

                try {
                    // 发送消息给贷后
                    notifyMisRepay(payOrderLog, count, loanOrderEntity);
                } catch (Exception e) {
                    logger.error("向贷后发送还款信息失败   ", e);
                }

                // 修改用户信用状态
                String userCode = payOrderEntity.getOwnerId();
                UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
                if (userEntity != null) {
                    String newCreditStatus = null;
                    String creditStatus = userEntity.getCreditStatus();
                    String payOrderType = payOrderEntity.getType();
                    if (PayTypeEnum.REPAY_POSTPONE.name().equals(payOrderType)) {
                        Long formerRepaymentDate = TimeUtils.extractDateTime(payOrderLog.getFormerRepaymentDate());
                        Long createTime = TimeUtils.extractDateTime(payOrderEntity.getCreateTime());
                        if (formerRepaymentDate < createTime) {
                            newCreditStatus = CreditStatusEnum.REPAY_OVERDUE.name();
                        } else {
                            newCreditStatus = CreditStatusEnum.REPAY_REGULAR.name();
                        }
                    } else if (PayTypeEnum.REPAY_OVERDUE.name().equals(payOrderType)) {
                        newCreditStatus = CreditStatusEnum.REPAY_OVERDUE.name();
                    } else {
                        newCreditStatus = CreditStatusEnum.REPAY_REGULAR.name();
                    }
                    if (StringUtils.isBlank(creditStatus) || CreditStatusEnum.INITIAL.name().equals(creditStatus)) {
                        userEntity.setCreditStatus(newCreditStatus);
                    }
                    if (CreditStatusEnum.REPAY_REGULAR.name().equals(creditStatus) && CreditStatusEnum.REPAY_OVERDUE.name().equals(newCreditStatus)) {
                        userEntity.setCreditStatus(newCreditStatus);
                    }
                    userEntity.setUpdateTime(System.currentTimeMillis());
                    userService.save(userEntity);
                } else {
                    logger.error("userCode[{}] 查不到用户实体", userCode);
                }
            } else {
                logger.warn("还款订单[{}]入账出现问题, 不向贷后发送mq以及更改用户信用状态", payOrderEntity.getOrderId());
            }
        }
        try {
            channelReturn.basicAck(tag, false);
        } catch (Exception e) {
            logger.error( "commit Mq 异常" , e );
        }
        return ;
    }


    /**
     * 通知贷后还款
     * @param payOrderLog  还款log
     */
    public void notifyMisRepay(PayOrderLogEntity payOrderLog, Integer postponeCount, LoanOrderEntity loanOrderEntity) {
        RepayInfoMqRes repayInfo = new RepayInfoMqRes();
        BeanUtils.copyProperties(payOrderLog, repayInfo);

        PayOrderEntity payOrderEntity = payOrderService.getByOrderId(payOrderLog.getPayOrderId());
        repayInfo.setPostponeDays(payOrderEntity.getPostponeDays());
        String status = payOrderEntity.getStatus();
        repayInfo.setEntryDone(StatusEnum.ENTRY_DONE.name().equals(status));
        repayInfo.setPayType(payOrderEntity.getType());
        repayInfo.setPostponeCount(postponeCount);
        repayInfo.setPostponeDays(payOrderEntity.getPostponeDays());

        List<RepayDetailRes> repayDetail = payOrderDetailService.getRepayDetail(payOrderEntity.getOrderId());
        repayInfo.setRepayDetail(repayDetail);

        Item realItem = billService.payoffRealItem(loanOrderEntity);
        List<RepayDetailRes> shouldPay = new ArrayList<RepayDetailRes>();

        for (Map.Entry<FieldTypeEnum, Unit> entry : realItem.entrySet()) {
            BigDecimal number = entry.getValue().sum();
            if (BigDecimal.ZERO.compareTo(number) < 0) {
                RepayDetailRes res = new RepayDetailRes();
                res.setFieldType(entry.getKey().name());
                res.setNumber(number);
                shouldPay.add(res);
            }
        }
        repayInfo.setShouldPay(shouldPay);
        repayInfo.setRepaymentDate(loanOrderEntity.getRepaymentDate());
        repayInfo.setPayoffTime(loanOrderEntity.getPayoffTime());

        // 设置减免金额
        BigDecimal totalDeductedAmount = couponService.getTotalDeductedAmount(loanOrderEntity.getOrderId());
        repayInfo.setTotalReliefAmount(totalDeductedAmount);

        List<CouponEntity> couponEntities = couponService.getByPayOrderId(payOrderLog.getPayOrderId());
        BigDecimal reliefAmount = BigDecimal.ZERO;
        if (couponEntities != null && couponEntities.size() > 0) {
            for (CouponEntity couponEntity : couponEntities) {
                reliefAmount = reliefAmount.add(couponEntity.getEntryAmount());
            }
        }
        repayInfo.setReliefAmount(reliefAmount);
        repayInfo.setProductType(sockpuppet);
        repayInfo.setCreateTime(payOrderEntity.getCreateTime());
        repayInfo.setPostponeTime(payOrderEntity.getEntryOverTime());


        JSONObject result = new JSONObject();
        result.put("repayInfo", repayInfo);
        logger.info(result.toJSONString());
        rabbitProducer.sendMessageRepay(payOrderLog.getPayOrderId(), result.toJSONString());
    }

}
