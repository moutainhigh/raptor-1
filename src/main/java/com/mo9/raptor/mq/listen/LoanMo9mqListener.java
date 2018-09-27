package com.mo9.raptor.mq.listen;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mo9.mqclient.IMqMsgListener;
import com.mo9.mqclient.MqAction;
import com.mo9.mqclient.MqMessage;
import com.mo9.raptor.bean.condition.FetchPayOrderCondition;
import com.mo9.raptor.bean.res.LendInfoMqRes;
import com.mo9.raptor.bean.res.RepayDetailRes;
import com.mo9.raptor.bean.res.RepayInfoMqRes;
import com.mo9.raptor.bean.res.UserInfoMqRes;
import com.mo9.raptor.engine.calculator.ILoanCalculator;
import com.mo9.raptor.engine.calculator.LoanCalculatorFactory;
import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.service.IPayOrderDetailService;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.state.event.impl.lend.LendResponseEvent;
import com.mo9.raptor.engine.state.event.impl.pay.DeductResponseEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.engine.structure.field.Field;
import com.mo9.raptor.engine.structure.field.FieldTypeEnum;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.entity.*;
import com.mo9.raptor.enums.PayTypeEnum;
import com.mo9.raptor.mq.producer.RabbitProducer;
import com.mo9.raptor.service.*;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 先玩后付相关监听器
 * @author xtgu
 */
@Component
public class LoanMo9mqListener implements IMqMsgListener{

	private static Logger logger = Log.get();

	@Autowired
	private BankService bankService ;

	@Autowired
	private IPayOrderService payOrderService;

	@Autowired
	private PayOrderLogService payOrderLogService;

	@Autowired
	private ILoanOrderService loanOrderService;

	@Autowired
	private ILendOrderService lendOrderService;

	@Autowired
	private IPayOrderDetailService payOrderDetailService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserCertifyInfoService userCertifyInfoService;

	@Autowired
	private UserContactsService userContactsService;

	@Autowired
	private IEventLauncher payEventLauncher;

	@Autowired
	private IEventLauncher lendEventLauncher;

    @Autowired
    private LoanCalculatorFactory loanCalculatorFactory;

	@Autowired
	private RabbitProducer rabbitProducer;

	@Resource
	private CardBinInfoService cardBinInfoService;

	@Override
	 public MqAction consume(MqMessage msg, Object consumeContext) {
		String tag = msg.getTag() ;
		logger.info("获取tag -- " + tag);
		logger.info("已收到mq消息:{}", msg.toString());
		if("MQ_RAPTOR_LOAN_TAG".equals(tag)){
			//先玩后付订单状态回调
			return payment(msg);
		}else if("MQ_RAPTOR_PAYOFF_TAG".equals(tag)){
			//先玩后付订单状态回调
			return payoff(msg);
		}
		return MqAction.CommitMessage;
	}

	/**
	 * 还款
	 * @param msg
	 * @return
	 */
	private MqAction payoff(MqMessage msg) {
		String body = msg.getBody();
		JSONObject remark = JSON.parseObject(body);
		JSONObject bodyJson = remark.getJSONObject("remark");
		String status = bodyJson.getString("status");
		String orderId = bodyJson.getString("orderId");
		String channel = bodyJson.getString("channel");
		BigDecimal amount = bodyJson.getBigDecimal("amount");
		String dealcode = bodyJson.getString("dealcode");
		String channelDealcode = bodyJson.getString("channelDealcode");
        // 失败原因
        String failReason = bodyJson.getString("failReason");
		DeductResponseEvent event;
		if ("success".equals(status)) {
			// 还款扣款成功事件
			event = new DeductResponseEvent(orderId, amount, true, "还款" + amount.toPlainString());
		} else {
			event = new DeductResponseEvent(orderId, null, false, "还款失败");
		}
		PayOrderLogEntity payOrderLog = payOrderLogService.getByPayOrderId(orderId);
		if (payOrderLog == null) {
			logger.error("还款订单号[{}], 查不到对应的还款log", orderId);
			return MqAction.CommitMessage;
		}
		payOrderLog.setChannel(channel);
		payOrderLog.setThirdChannelNo(channelDealcode);
		payOrderLog.setDealCode(dealcode);
		payOrderLog.setChannelResponse(body);
		payOrderLog.setChannelRepayNumber(amount);
		payOrderLog.setFailReason(failReason);
		payOrderLogService.save(payOrderLog);

		// 发送还款扣款成功事件
		try {
			try {
				CardBinInfoEntity cardBinInfoEntity = cardBinInfoService.findByCardPrefix(payOrderLog.getBankCard());
				String bankName = "银行卡" ;
				if(cardBinInfoEntity != null){
                    bankName = cardBinInfoEntity.getCardBank() ;
                }
				bankService.createOrUpdateBank( payOrderLog.getBankCard() ,  payOrderLog.getIdCard() ,  payOrderLog.getUserName() ,
                        payOrderLog.getBankMobile() ,  bankName ,  payOrderLog.getUserCode());
			} catch (Exception e) {
				Log.error(logger , e , "还款成功保存银行卡异常 orderId : " + orderId);
			}

			payEventLauncher.launch(event);
		} catch (Exception e) {
			Log.error(logger , e , "发送还款订单[{}]还款成功事件异常", orderId);
		}

		//修改或者存储银行卡信息 TODO

		// TODO: 发送消息给贷后
        if ("success".equals(status)) {
			// 增加延期次数
            List<FetchPayOrderCondition.Type> type = new ArrayList<FetchPayOrderCondition.Type>();
            type.add(FetchPayOrderCondition.Type.REPAY_POSTPONE);
            List<FetchPayOrderCondition.Status> statuses = new ArrayList<FetchPayOrderCondition.Status>();
            statuses.add(FetchPayOrderCondition.Status.ENTRY_DONE);
            FetchPayOrderCondition condition = new FetchPayOrderCondition();
            condition.setTypes(type);
            condition.setStates(statuses);
            PayOrderEntity payOrderEntity = payOrderService.getByOrderId(orderId);
            condition.setLoanOrderNumber(payOrderEntity.getLoanOrderId());
            Page<PayOrderEntity> payOrderEntities = payOrderService.listPayOrderByCondition(condition);
            LoanOrderEntity loanOrderEntity = loanOrderService.getByOrderId(payOrderEntity.getLoanOrderId());
			int count = payOrderEntities.getContent().size();
			loanOrderEntity.setPostponeCount(count);
            loanOrderEntity.setUpdateTime(System.currentTimeMillis());
            loanOrderService.save(loanOrderEntity);

			try {
				notifyMisRepay(payOrderLog, count, loanOrderEntity);
			} catch (Exception e) {
				logger.error("向贷后发送还款信息失败   ", e);
			}
		}
		return MqAction.CommitMessage;
	}

    /**
	 * 放款
	 * @param msg
	 * @return
	 */
	private MqAction payment(MqMessage msg) {

		String body = msg.getBody();
		JSONObject remark = JSON.parseObject(body);
		JSONObject bodyJson = remark.getJSONObject("remark");
		String status = bodyJson.getString("status");
		// 放款结算时间
		Long lendSettleTime = bodyJson.getLong("lendSettleTime");
		// 流水号
		String lendId = bodyJson.getString("lendId");
		//第三方返回信息
		String channelResponse = bodyJson.getString("channelResponse");
		//订单号
		String orderId = bodyJson.getString("orderId");
		// 放款渠道
		String lendChannel = bodyJson.getString("lendChannel");

		/*********下面参数暂无用**************/
		//银行卡卡号
		String bankCardNo = bodyJson.getString("bankCardNo");
		//银行预留身份证号
		String bankIdcardNo = bodyJson.getString("bankIdcardNo");
		//银行卡预留用户姓名
		String bankUserName = bodyJson.getString("bankUserName");
		//银行预留手机号码
		String bankMobile = bodyJson.getString("bankMobile");
		//银行名称
		String bankName = bodyJson.getString("bankName");
		// 支行
		String bankBranch = bodyJson.getString("bankBranch");
		// 实际放款金额
		BigDecimal lendAmount = bodyJson.getBigDecimal("lendAmount");
		//放款实际请求时间
		Long lendReqpTime = bodyJson.getLong("lendReqpTime");
		//放款渠道类型
		String lendChannelType = bodyJson.getString("lendChannelType");
		//事件ID
		String eventId = bodyJson.getString("eventId");
		//事件发送时间
		Long eventTime = bodyJson.getLong("eventTime");
		//操作者
		String operator = bodyJson.getString("operator");
		//事件类型
		String eventType = bodyJson.getString("eventType");


		Boolean isSucceed = "1".equals(status);
		LendResponseEvent lendResponse;
		if (isSucceed) {
			//获取银行卡信息
			try {
				LoanOrderEntity loanOrderEntity = loanOrderService.getByOrderId(orderId);
				if(loanOrderEntity != null){
                    bankService.createOrUpdateBank( bankCardNo ,  bankIdcardNo ,  bankUserName ,  bankMobile ,  bankName ,  loanOrderEntity.getOwnerId());
                }
			} catch (Exception e) {
				Log.error(logger , e ,"放款成功保存银行卡信息异常 orderId : " + orderId);
			}
			lendResponse = new LendResponseEvent(
					orderId, true,
					lendAmount, "先玩后付",
					lendId, channelResponse,
					lendSettleTime, "放款成功", lendChannel);
		} else {
			// 失败原因
			String failReason = bodyJson.getString("failReason");
			logger.error("MQ接收到了订单[{}]放款失败的信息", orderId);
			lendResponse = new LendResponseEvent(
					orderId, false,
					"先玩后付", lendId,
					channelResponse, "放款失败",
					lendChannel, failReason);
		}
		try {
			LendOrderEntity lendOrderEntity = lendOrderService.getByOrderId(orderId);
			if (lendOrderEntity == null || !StatusEnum.LENDING.name().equals(lendOrderEntity.getStatus())) {
				logger.info("接收到了订单[{}]放款的MQ, 然而查不到放款订单, 可能由于放款请求未结束而MQ先到, 延后再次发送MQ", orderId);
				return MqAction.ReconsumeLater;
			}

			lendEventLauncher.launch(lendResponse);
		} catch (Exception e) {
			Log.error(logger , e ,"订单[{}]放款[{}]事件报错,", orderId, isSucceed);
		}

		//修改或者存储银行卡信息 TODO

		// TODO: 发送消息给贷后
        if ("1".equals(status)) {
			try {
				notifyMisLend(orderId);
			} catch (Exception e) {
				logger.error("向贷后发送放款信息失败  ", e);
			}
		}
		return MqAction.CommitMessage;
	}

    /**
     * 通知贷后放款
     * @param orderId
     */
    private void notifyMisLend(String orderId) {
        LendOrderEntity lendOrderEntity = lendOrderService.getByOrderId(orderId);
		LoanOrderEntity loanOrderEntity = loanOrderService.getByOrderId(orderId);
		LendInfoMqRes lendInfo = new LendInfoMqRes();
        BeanUtils.copyProperties(lendOrderEntity, lendInfo);
        lendInfo.setLoanOrderId(lendOrderEntity.getApplyUniqueCode());
        lendInfo.setLoanNumber(loanOrderEntity.getLoanNumber());
        lendInfo.setLoanTerm(loanOrderEntity.getLoanTerm());
        lendInfo.setLentNumber(loanOrderEntity.getLentNumber());
        lendInfo.setInterestValue(loanOrderEntity.getInterestValue());
        lendInfo.setPenaltyValue(loanOrderEntity.getPenaltyValue());
        lendInfo.setChargeValue(loanOrderEntity.getChargeValue());
        lendInfo.setPostponeUnitCharge(loanOrderEntity.getPostponeUnitCharge());
        lendInfo.setOrderType(loanOrderEntity.getType());
        lendInfo.setOrderStatus(loanOrderEntity.getStatus());
        List<PayOrderEntity> payOrderEntities = payOrderService.listByLoanOrderIdAndType(orderId, PayTypeEnum.REPAY_POSTPONE);
        lendInfo.setPostponeCount(payOrderEntities.size());
        lendInfo.setRepaymentTime(loanOrderEntity.getRepaymentDate());



        String ownerId = lendOrderEntity.getOwnerId();
        UserEntity userEntity = userService.findByUserCode(ownerId);
        UserInfoMqRes userInfo = new UserInfoMqRes();
        UserCertifyInfoEntity userCertifyInfoEntity = userCertifyInfoService.findByUserCode(ownerId);
        userInfo.setUserCode(ownerId);
        userInfo.setMobile(userEntity.getMobile());
        userInfo.setRealName(userEntity.getRealName());
        userInfo.setIdCard(userEntity.getIdCard());
        userInfo.setCreditStatus(userEntity.getCreditStatus());
        userInfo.setUserIp(userEntity.getUserIp());
        userInfo.setLastLoginTime(userEntity.getLastLoginTime());
        userInfo.setOcrIdCardAddress(userCertifyInfoEntity.getOcrIdCardAddress());
        userInfo.setCallHistory(userEntity.getCallHistory());
        UserContactsEntity userContactsEntity = userContactsService.getByUserCode(ownerId);
        userInfo.setContactsList(userContactsEntity.getContactsList());
        userInfo.setGender(userCertifyInfoEntity.getOcrGender());
        userInfo.setDeleted(userEntity.getDeleted());

        JSONObject result = new JSONObject();
        result.put("lendInfo", lendInfo);
        result.put("userInfo", userInfo);
        logger.info(result.toJSONString());
        rabbitProducer.sendMessageLoan(orderId, result.toJSONString());
    }


    /**
     * 通知贷后还款
	 * @param payOrderLog  还款log
	 */
    private void notifyMisRepay(PayOrderLogEntity payOrderLog, Integer postponeCount, LoanOrderEntity loanOrderEntity) {
        RepayInfoMqRes repayInfo = new RepayInfoMqRes();
        BeanUtils.copyProperties(payOrderLog, repayInfo);

        PayOrderEntity payOrderEntity = payOrderService.getByOrderId(payOrderLog.getPayOrderId());
        repayInfo.setPostponeDays(payOrderEntity.getPostponeDays());
        String status = payOrderEntity.getStatus();
        repayInfo.setEntryDone(StatusEnum.ENTRY_DONE.name().equals(status));
        repayInfo.setPayType(payOrderEntity.getType());
        repayInfo.setPostponeDays(postponeCount);

        List<RepayDetailRes> repayDetail = payOrderDetailService.getRepayDetail(payOrderEntity.getOrderId());
        repayInfo.setRepayDetail(repayDetail);

        ILoanCalculator calculator = loanCalculatorFactory.load(loanOrderEntity);
        Item realItem = calculator.realItem(System.currentTimeMillis(), loanOrderEntity, PayTypeEnum.REPAY_AS_PLAN.name());
        List<RepayDetailRes> shouldPay = new ArrayList<RepayDetailRes>();
        for (Map.Entry<FieldTypeEnum, Field> entry : realItem.entrySet()) {
            BigDecimal number = entry.getValue().getNumber();
            if (BigDecimal.ZERO.compareTo(number) < 0) {
                RepayDetailRes res = new RepayDetailRes();
                res.setFieldType(entry.getKey().name());
                res.setNumber(number);
                shouldPay.add(res);
            }
        }
        repayInfo.setShouldPay(shouldPay);
		repayInfo.setRepaymentDate(loanOrderEntity.getRepaymentDate());

		// TODO:增加还清时间
		if (StatusEnum.PAYOFF.name().equals(loanOrderEntity.getStatus())) {
			repayInfo.setPayoffTime(loanOrderEntity.getUpdateTime());
		}

        JSONObject result = new JSONObject();
        result.put("repayInfo", repayInfo);
        logger.info(result.toJSONString());
        rabbitProducer.sendMessageRepay(payOrderLog.getPayOrderId(), result.toJSONString());
    }

}
