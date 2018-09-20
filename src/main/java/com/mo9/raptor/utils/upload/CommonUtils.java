package com.mo9.raptor.utils.upload;

import com.mo9.raptor.redis.RedisServiceApi;
import com.mo9.raptor.utils.CommonValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by xtgu on 2018/9/20.
 * @author xtgu
 */
@Component
public class CommonUtils {
    private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    @Autowired
    private RedisServiceApi redisServiceApi;

    @Resource(name = "raptorRedis")
    private RedisTemplate raptorRedis;

    /**
     * 判断五分钟次数是否正常
     * @param userCode
     * @return
     */
    public Boolean fiveMinutesNumberOk(String userCode){
        boolean flag = false ;
        //五分钟四次
        for(int i = 0 ; i < 4 ; i ++){
            String key = userCode + CommonValues.BANK_VERIFY + "_" + i ;
            Object object = redisServiceApi.get(key , raptorRedis) ;
            if(object == null){
                try {
                    redisServiceApi.set(key , key , 5*60*1000L , raptorRedis) ;
                } catch (Exception e) {
                    logger.error("银行卡验证 , 缓存次数异常 ! ");
                }
                flag = true ;
                break;
            }
        }
        return flag ;
    }

}
