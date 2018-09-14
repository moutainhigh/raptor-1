package com.mo9.raptor.mq.listen;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mo9.mqclient.IMqMsgListener;
import com.mo9.mqclient.MqAction;
import com.mo9.mqclient.MqMessage;
import com.mo9.raptor.engine.event.pay.DeductResponseEvent;
import com.mo9.raptor.engine.launcher.IEventLauncher;
import com.mo9.raptor.entity.PayOrderLogEntity;
import com.mo9.raptor.service.BankService;
import com.mo9.raptor.service.PayOrderLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 先玩后付相关监听器
 * @author xtgu
 */
@Component
public class LoanMo9mqListener implements IMqMsgListener{
	
	private static final Logger logger = LoggerFactory.getLogger(LoanMo9mqListener.class);

	@Autowired
	private BankService bankService ;

	@Autowired
	private PayOrderLogService payOrderLogService;

	@Autowired
	private IEventLauncher payOrderEventLauncher;

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
		JSONObject bodyJson = JSON.parseObject(body);
		String status = bodyJson.getString("status");
		if ("success".equals(status)) {
			String channel = bodyJson.getString("channel");
			BigDecimal amount = bodyJson.getBigDecimal("amount");
			String dealcode = bodyJson.getString("dealcode");
			String channelDealcode = bodyJson.getString("channelDealcode");
			String orderId = bodyJson.getString("orderId");
			PayOrderLogEntity payOrderLog = payOrderLogService.getByPayOrderId(orderId);
			if (payOrderLog == null) {
				logger.error("还款订单号[{}], 查不到对应的还款log", orderId);
				return MqAction.CommitMessage;
			}
			payOrderLog.setChannel(channel);
			payOrderLog.setDealCode(dealcode);
			payOrderLog.setThirdChannelNo(channelDealcode);
			payOrderLog.setChannelResponse(body);
			payOrderLogService.save(payOrderLog);

			// 发送还款扣款成功事件
			try {
				DeductResponseEvent event = new DeductResponseEvent(orderId, amount, true, System.currentTimeMillis() + ":还款" + amount.toPlainString());
				payOrderEventLauncher.launch(event);
			} catch (Exception e) {
				logger.error("发送还款订单[{}]还款成功事件异常", orderId, e);
			}
		} else {
			logger.warn("收到失败的还款信息, 无视");
		}
		//修改或者存储银行卡信息 TODO

		return MqAction.CommitMessage;
	}

	/**
	 * 放款
	 * @param msg
	 * @return
	 */
	private MqAction payment(MqMessage msg) {

		//TODO 获取参数
		/*"status" // 状态
		"bankCardNo"  //银行卡卡号
		"bankIdcardNo" //银行预留身份证号
		"bankUserName"  //银行卡预留用户姓名
		"bankMobile"  //银行预留手机号码
		"bankName"  //银行名称
		"bankBranch"  //支行
		"lendAmount",   //实际放款金额
		"lendReqpTime"  //放款实际请求时间
		"lendSettleTime"  //放款结算时间
		"lendChannel"  // 放款渠道
		"lendChannelType"  //放款渠道类型
		"lendId"  // 流水号
		"channelResponse"   //第三方返回信息
		"orderId",   //订单号
		"eventId",   //事件ID
		"eventTime"  //事件发送时间
		"operator"  //操作者
		"eventType"  //事件类型*/


		//修改或者存储银行卡信息 TODO
		return MqAction.CommitMessage;
	}


}
