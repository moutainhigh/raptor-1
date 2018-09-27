package com.mo9.raptor.task;

import com.mo9.raptor.controller.RiskController;
import com.mo9.raptor.risk.entity.TRiskTelInfo;
import com.mo9.raptor.risk.service.RiskTelInfoService;
import com.mo9.raptor.service.CommonService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.CommonValues;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.log.Log;
import com.mo9.raptor.utils.oss.OSSProperties;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;

/**
 * @author wtwei .
 * @date 2018/9/27 .
 * @time 10:23 .
 * 
 * 获取运营商报告补偿任务
 */
@Component
public class CallLogReportTask {
    private static Logger logger = Log.get();
    
    @Value("${task.open}")
    private String taskOpen ;

    @Resource
    private OSSProperties ossProperties;

    @Value("${raptor.sockpuppet}")
    private String sockpuppet;
    
    @Resource
    private RiskController riskController;
    
    @Resource
    private RiskTelInfoService riskTelInfoService;

    @Resource
    private UserService userService;

    @Scheduled(cron = "0 0/15 * * * ?")
    public void run(){
        if(CommonValues.TRUE.equals(taskOpen)){
            logger.info("-----开始执行运营商报告补偿任务-----");
            
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -30);
            
            List<TRiskTelInfo> noReportRecords = riskTelInfoService.findNoReportTelInfo(calendar.getTime());
            
            logger.info("-----运营报告补偿任务--> 共发现30天内有{}条数据没有成功获取到运营商报告。", noReportRecords.size());

            String sid;
            for (TRiskTelInfo noReportRecord : noReportRecords) {
                sid = noReportRecord.getSid();

                String report = riskController.getCallLogReport(sid, "report");

                String fileName = ossProperties.getCatalogCallLog() + "/" + sockpuppet + "-" + noReportRecord.getMobile() + "-report.json";
                
                riskController.uploadFile2Oss(report, fileName);

                //通知用户状态，报告已生成
                try {
                    userService.updateReceiveCallHistory(noReportRecord.getUid(), true);

                    TRiskTelInfo riskTelInfo =  riskTelInfoService.findByMobile(noReportRecord.getMobile());
                    riskTelInfo.setReportReceived(true);
                    riskTelInfoService.update(riskTelInfo);
                    logger.info("定时任务更新用户运营商报告获取状态成功，tel: " + noReportRecord.getMobile() + ", uid: " + noReportRecord.getUid() + ", sid: " + noReportRecord.getSid());
                } catch (Exception e) {
                    logger.error("定时任务更新用户运营商报告获取状态失敗，tel: " + noReportRecord.getMobile() + ", uid: " + noReportRecord.getUid()+ ", sid: " + noReportRecord.getSid(), e);
                }
                
            }

            logger.info("-----执行运营商报告补偿任务执行完成-----");
        }
    }
    
}
