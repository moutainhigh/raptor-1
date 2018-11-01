package com.mo9.raptor.task;

import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.utils.CommonValues;
import com.mo9.raptor.utils.RiskUtilsV2;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by xtgu on 2018/10/30.
 * @author xtgu
 * 自动拉黑黑名单
 */
@Component("userBlackTask")
public class UserBlackTask {

    private static Logger logger = Log.get();

    @Autowired
    private ILoanOrderService loanOrderService ;

    @Autowired
    private RiskUtilsV2 riskUtilsV2 ;

    @Value("${task.open}")
    private String taskOpen ;

    @Scheduled(cron = "0 0 0 * * ?")
    public void verifyOverdueOrder(){
        if(CommonValues.TRUE.equals(taskOpen)){
            logger.info("定时器检索处理黑名单用户开始");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR , -6);
            calendar.set(Calendar.HOUR_OF_DAY , 0);
            calendar.set(Calendar.MINUTE , 0);
            calendar.set(Calendar.SECOND , 0);
            List<LoanOrderEntity> loanOrderEntityList = loanOrderService.listByOverDueOrder(calendar.getTime().getTime());
            for(LoanOrderEntity loanOrderEntity : loanOrderEntityList){
                riskUtilsV2.verifyNeedToBlack(loanOrderEntity);
            }
            logger.info("定时器检索处理黑名单用户结束  -  总处理 : " + loanOrderEntityList.size());
        }

    }



}
