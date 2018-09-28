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
import java.util.Set;

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

    @Scheduled(cron = "0 0/2 * * * ?")
    public void run(){
        if(CommonValues.TRUE.equals(taskOpen)){
            logger.info("-----开始执行运营商报告补偿任务-----");
            
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -30);
            
            Set<TRiskTelInfo> noReportRecords = riskTelInfoService.findNoReportTelInfo(calendar.getTime());
            
            logger.info("-----运营报告补偿任务--> 共发现30天内有{}条数据没有成功获取到运营商报告。", noReportRecords.size());

            String sid;
            String uid;
            String mobile;
            Long nowTime = Calendar.getInstance().getTimeInMillis();
            for (TRiskTelInfo noReportRecord : noReportRecords) {
                
                //入库不到一小时的跳过
                if (nowTime - noReportRecord.getUpdatedAt().getTime() < 60 * 60 * 1000){
                    continue;
                }
                
                sid = noReportRecord.getSid();
                uid = noReportRecord.getUid();
                mobile = noReportRecord.getMobile();

                String report = riskController.getCallLogReport(sid, "report");
                
                if (report == null){
                    logger.info("-----运营报告补偿任务-->运营商报告未生成，tel: {}, uid: {}, sid: {}", mobile, uid, sid);
                    continue;
                }

                String fileName = ossProperties.getCatalogCallLog() + "/" + sockpuppet + "-" + mobile + "-report.json";
                
                riskController.uploadFile2Oss(report, fileName);

                //通知用户状态，报告已生成
                try {
                    
                    if (noReportRecord != null){
                        userService.updateReceiveCallHistory(noReportRecord.getUid(), true);
                        noReportRecord.setReportReceived(true);
                        riskTelInfoService.update(noReportRecord);
                        logger.info("定时任务更新用户运营商报告获取状态成功，tel: " + mobile + ", uid: " + uid + ", sid: " + sid);
                    }
                    
                } catch (Exception e) {
                    logger.error("定时任务更新用户运营商报告获取状态失敗，tel: " + mobile + ", uid: " + uid+ ", sid: " + sid, e);
                }
                
            }

            logger.info("-----执行运营商报告补偿任务执行完成-----");
        }
    }
    
}
