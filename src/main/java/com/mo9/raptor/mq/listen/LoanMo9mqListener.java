package com.mo9.raptor.mq.listen;

import com.mo9.mqclient.IMqMsgListener;
import com.mo9.mqclient.MqAction;
import com.mo9.mqclient.MqMessage;
import com.mo9.raptor.service.BankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 先玩后付相关监听器
 * @author xtgu
 */
@Component
public class LoanMo9mqListener implements IMqMsgListener{
	
	private static final Logger logger = LoggerFactory.getLogger(LoanMo9mqListener.class);

	@Autowired
	private BankService bankService ;

	@Override
	 public MqAction consume(MqMessage msg, Object consumeContext) {
		String tag = msg.getTag() ;
		logger.info("获取tag -- " + tag);
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
		//TODO 获取参数
		/*"channel"  // 还款渠道
		"amount"  // 金额(元)
		"dealcode" //先玩后付订单号
		"channelDealcode" // 第三方订单号
		"status" // 状态
		"orderId" // 放款订单号  */

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
