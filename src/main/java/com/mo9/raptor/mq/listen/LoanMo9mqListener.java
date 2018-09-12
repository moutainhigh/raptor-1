package com.mo9.raptor.mq.listen;

import com.mo9.mqclient.IMqMsgListener;
import com.mo9.mqclient.MqAction;
import com.mo9.mqclient.MqMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 先玩后付相关监听器
 * @author xtgu
 */
public class LoanMo9mqListener implements IMqMsgListener{
	
	private static final Logger logger = LoggerFactory.getLogger(LoanMo9mqListener.class);

	@Override
	 public MqAction consume(MqMessage msg, Object consumeContext) {
		String tag = msg.getTag() ;
		logger.info("获取tag -- " + tag);
		if("proxypay_payStatus".equals(tag)){
			//先玩后付订单状态回调
			return payment(msg);
		}else if("proxypay_payStatus".equals(tag)){
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
		return MqAction.CommitMessage;
	}

	/**
	 * 放款
	 * @param msg
	 * @return
	 */
	private MqAction payment(MqMessage msg) {
		return MqAction.CommitMessage;
	}


}
