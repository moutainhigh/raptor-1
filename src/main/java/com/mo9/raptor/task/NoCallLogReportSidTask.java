package com.mo9.raptor.task;

import com.mo9.raptor.controller.RiskController;
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

    @Resource
    private RiskController riskController;
    
    
    @Scheduled(cron = "0 0 0/6 * * ?")
    public void run(){
        riskController.processNoSidMobile("5jomophlia5k1l22fcj781b225");
    }
}
