package com.mo9.raptor.task;

import com.mo9.raptor.controller.RiskController;
import com.mo9.raptor.entity.DictDataEntity;
import com.mo9.raptor.redis.RedisServiceApi;
import com.mo9.raptor.service.DictService;
import com.mo9.raptor.utils.CommonValues;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wtwei .
 * @date 2018/10/9 .
 * @time 10:09 .
 * 处理没有成功获取到帮秒爬sid的用户数据
 * 登陆bmp后台系统，通过手机号爬取sid，并完成入库
 */

@Component
public class NoCallLogReportSidTask {
    private static Logger logger = Log.get();

    @Resource
    private RiskController riskController;
    
    @Resource
    private DictService dictService;
    
    @Value("${task.open}")
    private String taskOpen;
    
    @Scheduled(cron = "0 0 0/2 * * ?")
    public void run(){
        if(CommonValues.TRUE.equals(taskOpen)){
            logger.info("------开始处理未获取到通话记录SID的用户数据---");
            DictDataEntity dictData = dictService.findDictData("BMP_SESSION_ID", "SESSION_ID");
            if (dictData == null){
                logger.warn("未配置电话帮后台登陆SessionId，任务不执行");
                return;
            }
            
            riskController.processNoSidMobile(dictData.getName());
            logger.info("-------处理未获取到SID的用户数据任务完成-----");
        }
    }
}
