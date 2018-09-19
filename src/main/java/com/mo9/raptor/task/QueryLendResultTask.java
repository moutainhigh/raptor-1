package com.mo9.raptor.task;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mo9.raptor.bean.res.LoanOrderLendRes;
import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.utils.GatewayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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


    @Scheduled(cron = "0 0/10 * * * ?")
    //@Scheduled(cron = "0 0/1 * * * ?")
    public void doTask() {
        logger.info("查询放款结果定时器开始");
        int num = 0;
        List<LendOrderEntity> lendOrderEntities = lendOrderService.listAllLendingOrder();
        for (LendOrderEntity lendOrderEntity : lendOrderEntities) {
            LoanOrderLendRes orderMsg = gatewayUtils.getOrderMsg(lendOrderEntity.getApplyUniqueCode());
            if ("failed".equals(orderMsg.getOrderStatus())) {
                lendOrderEntity.setFailReason(orderMsg.getFailReason());
                lendOrderEntity.setChannel(orderMsg.getChannel());
                lendOrderEntity.setDealCode(orderMsg.getDealcode());
                lendOrderEntity.setStatus(StatusEnum.FAILED.name());
                lendOrderService.save(lendOrderEntity);
                num++;
            }
        }
        logger.info("查询放款结果定时器结束, 共更新[{}]条放款记录", num);
    }

}
