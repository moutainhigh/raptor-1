package com.mo9.raptor.utils;

import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.mq.listen.LoanMo9mqListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by xtgu on 2018/9/12.
 * @author xtgu
 * 先玩后付相关util
 */
@Component
public class GatewayUtils {

    private static final Logger logger = LoggerFactory.getLogger(GatewayUtils.class);

    /**
     * 先玩后付地址
     */
    @Value("${gateway.url}")
    private String gatewayUrl ;

    /**
     * 放款
     * @return
     */
    public ResCodeEnum loan(){
        //TODO
        return ResCodeEnum.SUCCESS ;
    }

    /**
     * 还款
     * @return
     */
    public ResCodeEnum payoff(){
        //TODO
        return ResCodeEnum.SUCCESS ;
    }

    /**
     * 检查银行卡四要素
     * @return
     */
    public ResCodeEnum verifyBank(){
        //TODO
        return ResCodeEnum.SUCCESS ;
    }

    /**
     * 同步先玩后付用户 - 手机号
     * @param mobile
     * @return
     */
    public ResCodeEnum syncUserByMobile(String mobile){
        //TODO
        return ResCodeEnum.SUCCESS ;
    }


}
