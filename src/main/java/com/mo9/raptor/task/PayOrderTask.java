package com.mo9.raptor.task;

import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.service.impl.PayOrderServiceImpl;
import com.mo9.raptor.entity.ChannelEntity;
import com.mo9.raptor.entity.PayOrderLogEntity;
import com.mo9.raptor.service.ChannelService;
import com.mo9.raptor.service.PayOrderLogService;
import com.mo9.raptor.utils.CommonValues;
import com.mo9.raptor.utils.GatewayUtils;
import com.mo9.raptor.utils.SeekerUtils;
import com.mo9.raptor.utils.log.Log;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by xtgu on 2018/9/22.
 * @author xtgu
 * 还款订单定时器
 */
@Component("payOrderTask")
public class PayOrderTask {

    private static Logger logger = Log.get();

    @Autowired
    private PayOrderLogService payOrderLogService ;

    @Autowired
    private IPayOrderService payOrderService ;

    @Autowired
    private GatewayUtils gatewayUtils ;

    @Autowired
    private SeekerUtils seekerUtils ;

    @Autowired
    private ChannelService channelService ;

    @Value("${task.open}")
    private String taskOpen ;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void notSuccessOrderTask(){

        if(CommonValues.TRUE.equals(taskOpen)){
            logger.info("还款未最终状态定时器开启");
            List<ChannelEntity> channels = channelService.listAllAvailableChannels() ;
            StringBuffer channelStr = new StringBuffer() ;
            for(ChannelEntity channelEntity : channels){
                channelStr.append(channelEntity.getChannel()) ;
            }
            String channel = channelStr.toString() ;
            List<PayOrderEntity> list = payOrderService.findByStatus(StatusEnum.DEDUCTING.name()) ;
            for(PayOrderEntity payOrderEntity : list){
                PayOrderLogEntity payOrderLogEntity = payOrderLogService.getByPayOrderId(payOrderEntity.getOrderId());
                if(!StringUtils.isBlank(payOrderLogEntity.getDealCode())){
                    if(channel.contains(payOrderLogEntity.getChannel())){
                        gatewayUtils.gatewayMqPush(payOrderLogEntity.getDealCode());
                    }else{
                        seekerUtils.gatewayMqPush(payOrderLogEntity.getDealCode());
                    }

                }
            }
            logger.info("还款未最终状态定时器结束 共处理 " + list.size() + "条数据");
        }

    }
}
