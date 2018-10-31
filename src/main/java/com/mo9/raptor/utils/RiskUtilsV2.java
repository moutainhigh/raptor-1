package com.mo9.raptor.utils;

import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.state.event.impl.user.BlackEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.pool.DingTalkNoticeThreadTask;
import com.mo9.raptor.pool.RiskPortalBlackTask;
import com.mo9.raptor.pool.ThreadPool;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by xtgu on 2018/10/30.
 * @author xtgu
 * 风控相关utils  -- V2结尾是天天有钱 , 钱够花同步方法
 */
@Component
public class RiskUtilsV2 {

    private static Logger logger = Log.get();
    @Autowired
    private CommonUtils commonUtils ;
    @Value("${over.loan.order.black.days}")
    private Integer overLoanOrderBlackDays ;

    @Autowired
    private IEventLauncher userEventLauncher;

    @Autowired
    private RiskPortalBlackTask riskPortalBlackTask ;

    @Autowired
    private ThreadPool threadPool;

    @Autowired
    private UserService userService ;

    private static RiskUtilsV2 riskStatic;

    @PostConstruct
    public void init() {
        riskStatic = this;
        riskStatic.threadPool = this.threadPool;
        riskStatic.riskPortalBlackTask = this.riskPortalBlackTask;

    }

    /**
     * 判断是否需要进行黑名单处理
     */
    public Boolean verifyNeedToBlack(LoanOrderEntity loanOrderEntity){
        //判断订单状态
        Long time = loanOrderEntity.getRepaymentDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Integer betweenDays = 0 ;
        if(StatusEnum.LENT.name().equals(loanOrderEntity.getStatus())){
            betweenDays = commonUtils.daysBetween(calendar.getTime() , new Date()) ;
        }else if(StatusEnum.PAYOFF.name().equals(loanOrderEntity.getStatus())){
            Calendar calendarPayoff = Calendar.getInstance();
            calendarPayoff.setTimeInMillis(loanOrderEntity.getPayoffTime());
            betweenDays = commonUtils.daysBetween(calendar.getTime() , calendarPayoff.getTime()) ;
        }else{
            throw new RuntimeException("用户订单状态异常 -- 黑名单判断" + loanOrderEntity.getOwnerId()+ " -- " + loanOrderEntity.getOrderId());
        }

        if(betweenDays >= overLoanOrderBlackDays){
            logger.info("用户 "+ loanOrderEntity.getOwnerId() + " 订单 " + loanOrderEntity.getOrderId() + " 状态 : " + loanOrderEntity.getStatus() + " 逾期 " + betweenDays + " 天 ");
            //黑名单拉黑
            setUserBlack(loanOrderEntity) ;
            return true ;
        }
        return false ;
    }

    /**
     * 处理用户黑名单
     */
    private void setUserBlack(LoanOrderEntity loanOrderEntity){
        //修改本地黑名单
        try {
            BlackEvent event = new BlackEvent(loanOrderEntity.getOwnerId(), "订单逾期7天黑名单 - "+ loanOrderEntity.getOrderId());
            userEventLauncher.launch(event);
            //修改江湖救急黑名单 异步线程
            UserEntity userEntity = userService.findByUserCode(loanOrderEntity.getOwnerId()) ;
            riskStatic.riskPortalBlackTask.setMobile(userEntity.getMobile());
            riskStatic.riskPortalBlackTask.setOrderId(loanOrderEntity.getOrderId());
            riskStatic.threadPool.execute(riskStatic.riskPortalBlackTask);
        } catch (Exception e) {
            Log.error(logger,e,"黑名单修改执行异常 - "+ loanOrderEntity.getOwnerId()+ " -- " + loanOrderEntity.getOrderId());
        }
    }

}
