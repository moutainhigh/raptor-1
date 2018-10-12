package com.mo9.raptor.task;

import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.service.impl.LoanOrderServiceImpl;
import com.mo9.raptor.utils.CommonValues;
import com.mo9.raptor.utils.push.PushBean;
import com.mo9.raptor.utils.push.PushUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by jyou on 2018/10/11.
 *
 * @author jyou
 */
@Component("pushTask")
public class PushTask {

    private Logger logger = LoggerFactory.getLogger(PushTask.class);

    @Resource
    private PushUtils pushUtils;

    @Resource
    private ILoanOrderService loanOrderService;

    @Value("${task.open}")
    private String taskOpen ;

    @Scheduled(cron = "0 0 9,12 * * ?")
    public void pushPayOrder(){
        if(!CommonValues.TRUE.equals(taskOpen)){
            return;
        }

        List<LoanOrderEntity>  list = loanOrderService.listShouldPayOrder();
        if(list == null || list.size() == 0){
            logger.info("推送今天所有需要还款人消息，没有需要推送的用户");
            return;
        }

        for(LoanOrderEntity entity : list){
            String userCode = entity.getOwnerId();
            PushBean pushBean = new PushBean(userCode, "天天有钱提醒您还款", "今天为还款日，请及时还款哦~");
            pushUtils.push(pushBean);
        }
        logger.info("今日还款提醒推送完成，共计推送条数count={}", list.size());
    }

}
