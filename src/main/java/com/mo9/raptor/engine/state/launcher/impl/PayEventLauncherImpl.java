package com.mo9.raptor.engine.state.launcher.impl;

import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.state.event.IStateEvent;
import com.mo9.raptor.engine.state.launcher.AbstractStateEventLauncher;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.enums.BusinessTypeEnum;
import com.mo9.raptor.enums.PayTypeEnum;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.service.CashAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gqwu on 2018/4/4.
 */
@Component("payEventLauncher")
public class PayEventLauncherImpl extends AbstractStateEventLauncher<PayOrderEntity, IStateEvent> implements IEventLauncher<IStateEvent> {

    @Autowired
    private IPayOrderService payOrderService;

    @Autowired
    private CashAccountService cashAccountService;

    @Override
    public PayOrderEntity selectEntity(String orderId) {
        return this.payOrderService.getByOrderId(orderId);
    }

    @Override
    public void saveEntity(PayOrderEntity entity) {
        entity.setUpdateTime(System.currentTimeMillis());
        //根据还款类型区分账户出账类型
        String channel = entity.getChannel() ;
        String type = entity.getType() ;
        ResCodeEnum resCodeEnum = ResCodeEnum.SUCCESS ;
        if(channel.equals("manual_pay")){
            //线下入账
            if(PayTypeEnum.REPAY_POSTPONE.name().equals(type)){
                //延期
                resCodeEnum = cashAccountService.entry(entity.getOwnerId() , entity.getPayNumber(), entity.getOrderId() , BusinessTypeEnum.UNDERLINE_POSTPONE);
            }else{
                //还款
                resCodeEnum = cashAccountService.entry(entity.getOwnerId() , entity.getPayNumber(), entity.getOrderId() , BusinessTypeEnum.UNDERLINE_REPAY);
            }
        }else{
            //线上入账
            if(PayTypeEnum.REPAY_POSTPONE.name().equals(type)){
                //延期
                resCodeEnum = cashAccountService.entry(entity.getOwnerId() , entity.getPayNumber(), entity.getOrderId(), BusinessTypeEnum.ONLINE_POSTPONE);
            }else{
                //还款
                resCodeEnum = cashAccountService.entry(entity.getOwnerId() , entity.getPayNumber(), entity.getOrderId(), BusinessTypeEnum.ONLINE_REPAY);
            }
        }
        if(ResCodeEnum.SUCCESS != resCodeEnum && ResCodeEnum.CASH_ACCOUNT_BUSINESS_NO_IS_EXIST != resCodeEnum){
            throw new RuntimeException("入账操作现金账户异常 " + resCodeEnum + "  -- 借款订单 : " + entity.getLoanOrderId() + " -- 还款订单" + entity.getOrderId() + " -- 用户 " + entity.getOwnerId() ) ;
        }
        this.payOrderService.save(entity);
    }
}
