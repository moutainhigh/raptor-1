package com.mo9.raptor.engine.state.action.impl.pay;

import com.mo9.raptor.bean.ReqHeaderParams;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.engine.utils.TimeUtils;
import com.mo9.raptor.entity.PayOrderLogEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.PayTypeEnum;
import com.mo9.raptor.service.PayOrderLogService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.Md5Util;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xzhang on 2018/10/11
 * 放款成功通知Action
 */
public class WhiteUserAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(WhiteUserAction.class);

    private final ILoanOrderService loanOrderService;

    private final IPayOrderService payOrderService;

    private final PayOrderLogService payOrderLogService;

    private final UserService userService;

    private final HttpClientApi httpClientApi;

    private final String userCode;

    private final String whiteBaseUrl;

    private static final String salt = "rtsDDcogZcPCu!NYkfgfjQq6O;~2Brtr";

    public WhiteUserAction(ILoanOrderService loanOrderService, IPayOrderService payOrderService, PayOrderLogService payOrderLogService, UserService userService, HttpClientApi httpClientApi, String userCode, String whiteBaseUrl) {
        this.payOrderService = payOrderService;
        this.loanOrderService = loanOrderService;
        this.payOrderLogService = payOrderLogService;
        this.userService = userService;
        this.httpClientApi = httpClientApi;
        this.whiteBaseUrl = whiteBaseUrl;
        this.userCode = userCode;
    }

    @Override
    public void run() {

        List<LoanOrderEntity> loanOrders = loanOrderService.listByUserAndStatus(userCode, StatusEnum.EFFECTIVE_LOAN);
        if (loanOrders == null || loanOrders.size() == 0) {
            logger.info("没有借款，不推送！！！用户号：[{}]", userCode);
            //没有借款，不推送
            return;
        }
        for (LoanOrderEntity loanOrder: loanOrders) {
            Long payoffTime = loanOrder.getPayoffTime();
            //无还清时间，以当天为准
            if (payoffTime == null || payoffTime < 1) {
                payoffTime = System.currentTimeMillis();
            }
            int overdueDays = TimeUtils.dateDiff(loanOrder.getRepaymentDate(), payoffTime);
            if (overdueDays >= 4) {
                //判断用户单次借款，历史逾期天数，若大于等于4天不推送
                logger.info("借款订单判定，订单号[{}], 历史逾期天数[{}]，不推送！！！用户号：[{}]", overdueDays, userCode);
                return;
            }
        }

        List<PayOrderEntity> payOrders = payOrderService.listByUserAndStatus(userCode, StatusEnum.EFFECTIVE_PAY);
        if (payOrders == null || payOrders.size() == 0) {
            //没有还款历史，不推送
            logger.info("没有还款历史，不推送！用户号：[{}]", userCode);
            return;
        }
        //判断还款时逾期情况，通过成功延期还款的订单查询到还款日志，计算逾期天数
        for (PayOrderEntity payOrder: payOrders) {
            if (payOrder.getType().equals(PayTypeEnum.REPAY_POSTPONE)) {
                PayOrderLogEntity payOrderLog = payOrderLogService.getByPayOrderId(payOrder.getOrderId());
                if (payOrderLog == null) {
                    logger.info("还款日志异常，还款订单对应日志不存在！！！还款订单号：[{}], 用户号：[{}]", payOrder.getOrderId(), userCode);
                    return;
                }
                int overdueDays = TimeUtils.dateDiff(payOrderLog.getFormerRepaymentDate(), payOrderLog.getUpdateTime());
                if (overdueDays >= 4) {
                    logger.info("还款日志判定，历史逾期天数[{}]，不推送！！！用户号：[{}]", overdueDays, userCode);
                    return;
                }
            }
        }

        //验证通过，发送手机号给钱够花
        UserEntity user = userService.findByUserCode(userCode);
        long timeStamp = System.currentTimeMillis();

        String str = userCode + timeStamp + user.getMobile() +  salt;
        String sign = Md5Util.getMD5(str);

        String url = whiteBaseUrl + "/outside/import_white_user?mobile=" + user.getMobile();

        Map<String, String> headers = new HashMap<String, String> ();
        headers.put(ReqHeaderParams.ACCOUNT_CODE, userCode);
        headers.put(ReqHeaderParams.TIMESTAMP, String.valueOf(timeStamp));
        headers.put(ReqHeaderParams.SIGN, sign);

        String res  = null;
        try {
            logger.info("白名单推送请求用户号：[{}]", userCode);
            res  = httpClientApi.doGetByHeader(url, headers);
            logger.info("白名单发送响应报文：[{}]，用户号：[{}]", res, userCode);
        } catch (IOException e) {
            logger.error("白名单发送失败，用户CODE:[{}]，用户手机号MOBILE:[{}]，响应报文：[{}]", userCode, user.getMobile(), res);
        }
    }

    @Override
    public String getActionType() {
        return this.getClass().getName();
    }

    @Override
    public String getOrderId() {
        return null;
    }
}
