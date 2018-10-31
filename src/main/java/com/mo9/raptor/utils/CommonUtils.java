package com.mo9.raptor.utils;

import com.mo9.raptor.redis.RedisServiceApi;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xtgu on 2018/9/20.
 * @author xtgu
 */
@Component
public class CommonUtils {
    private static Logger logger = Log.get();

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
        for(int i = 0 ; i < 10 ; i ++){
            String key = userCode + CommonValues.BANK_VERIFY + "_" + i ;
            Object object = redisServiceApi.get(key , raptorRedis) ;
            if(object == null){
                try {
                    redisServiceApi.set(key , key , 5*60L , raptorRedis) ;
                } catch (Exception e) {
                    Log.error(logger , e ,"银行卡验证 , 缓存次数异常 ! ");
                }
                flag = true ;
                break;
            }
        }
        return flag ;
    }

    /**
     * 计算日期差
     * @param smdate
     * @param bdate
     * @return
     */
    public Integer daysBetween(Date smdate, Date bdate ){
        int days = 0 ;
        try {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            smdate=sdf.parse(sdf.format(smdate));
            bdate=sdf.parse(sdf.format(bdate));
            Calendar cal = Calendar.getInstance();
            cal.setTime(smdate);
            long time1 = cal.getTimeInMillis();
            cal.setTime(bdate);
            long time2 = cal.getTimeInMillis();
            long between_days=(time2-time1)/(1000*3600*24);
            days = Integer.parseInt(String.valueOf(between_days));
        } catch (Exception e) {
            logger.error("时间转换异常" , e);
        }
        return days ;
    }


}
