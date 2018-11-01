package com.mo9.raptor.utils;

import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.state.event.impl.user.BlackEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
    @Value("${risk.portal.black.url}")
    private String riskPortalBlackUrl ;
    @Value("${loan.name.en}")
    private String loanNameEn ;

    @Autowired
    private IEventLauncher userEventLauncher;

    @Autowired
    private UserService userService ;

    @Autowired
    private HttpClientApi httpClientApi ;

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
            //黑名单拉黑
            setUserBlack(loanOrderEntity , betweenDays) ;
            return true ;
        }
        return false ;
    }

    /**
     * 处理用户黑名单
     */
    private void setUserBlack(LoanOrderEntity loanOrderEntity , Integer betweenDays){
        //修改本地黑名单
        try {
            UserEntity userEntity = userService.findByUserCode(loanOrderEntity.getOwnerId()) ;
            if(StatusEnum.BLACK.name().equals(userEntity.getStatus())){
                return ;
            }
            logger.info("用户 "+ loanOrderEntity.getOwnerId() + " 订单 " + loanOrderEntity.getOrderId() + " 状态 : " + loanOrderEntity.getStatus() + " 逾期 " + betweenDays + " 天 ");
            BlackEvent event = new BlackEvent(loanOrderEntity.getOwnerId(), "订单逾期7天黑名单 - "+ loanOrderEntity.getOrderId());
            userEventLauncher.launch(event);
            //修改江湖救急黑名单 异步线程
            Map<String , String> param = new HashMap<String , String>();
            param.put("mobile" , userEntity.getMobile()) ;
            param.put("orderId" , loanOrderEntity.getOrderId()) ;
            param.put("channel" , loanNameEn) ;
            int num = 0 ;
            this.toBlack(num , param);

        } catch (Exception e) {
            Log.error(logger,e,"黑名单修改执行异常 - "+ loanOrderEntity.getOwnerId()+ " -- " + loanOrderEntity.getOrderId());
        }
    }

    /**
     * 执行黑名单
     * @param num
     * @return
     */
    private void toBlack(int num , Map<String , String> param) {
        //输入 0 默认加 1 最大循环三次
        num++ ;
        try {
            if(num < 4){
                String result = httpClientApi.doGet(riskPortalBlackUrl , param) ;
                if("success".equals(result)){
                    logger.error("手机号 " + param.get("mobile") + "江湖救急黑名单执行成功 " + param.get("orderId"));
                }else{
                    logger.error("手机号 " + param.get("mobile") + "江湖救急黑名单执行失败 第"+ num + "次" +param.get("orderId"));
                    this.toBlack(num , param);
                }
            }
        } catch (Exception e) {
            logger.error("手机号 " + param.get("mobile") + "江湖救急黑名单执行异常 第"+ num + "次"+param.get("orderId"));
            this.toBlack(num , param);
        }
    }

}
