package com.mo9.raptor.task;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mo9.mqclient.MqAction;
import com.mo9.raptor.bean.res.LoanOrderLendRes;
import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.engine.state.event.impl.lend.LendResponseEvent;
import com.mo9.raptor.engine.state.event.impl.loan.LoanResponseEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.utils.CommonValues;
import com.mo9.raptor.utils.GatewayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 查询失败的放款结果
 * Created by xzhang on 2018/9/18.
 */
@Component("queryLendResultTask")
public class QueryLendResultTask {

    private static final Logger logger = LoggerFactory.getLogger(QueryLendResultTask.class);

    @Autowired
    private ILendOrderService lendOrderService;

    @Autowired
    private GatewayUtils gatewayUtils;

    @Value("${task.open}")
    private String taskOpen ;

    @Autowired
    private IEventLauncher lendEventLauncher;

    @Scheduled(cron = "0 0/10 * * * ?")
    //@Scheduled(cron = "0 0/1 * * * ?")
    public void doTask() {
        if(CommonValues.TRUE.equals(taskOpen)){
            logger.info("查询放款结果定时器开始");
            List<LendOrderEntity> lendOrderEntities = lendOrderService.listAllLendingOrder();
            for (LendOrderEntity lendOrderEntity : lendOrderEntities) {
                LoanOrderLendRes orderMsg = gatewayUtils.getOrderMsg(lendOrderEntity.getApplyUniqueCode());
                //查单失败 , 订单不存在
                if (orderMsg != null && "failed".equals(orderMsg.getStatus())) {
                    try {
                        LendResponseEvent lendResponse = new LendResponseEvent(
                                lendOrderEntity.getOrderId(), false,
                                "先玩后付", null,
                                null, "放款失败",
                                lendOrderEntity.getChannel(), "订单未生成");
                        lendEventLauncher.launch(lendResponse);
                    } catch (Exception e) {
                        logger.error("放款订单失败查询处理异常" + lendOrderEntity.getOrderId());
                    }

                }
            }
        }

    }
}
