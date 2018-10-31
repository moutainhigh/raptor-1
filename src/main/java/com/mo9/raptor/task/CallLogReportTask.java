package com.mo9.raptor.task;

import com.mo9.raptor.controller.RiskController;
import com.mo9.raptor.entity.UserEntity;
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

    @Scheduled(cron = "0 0/15 * * * ?")
    public void run(){
        if(CommonValues.TRUE.equals(taskOpen)){
            logger.info("-----开始执行运营商报告补偿任务-----");
            
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -30);
            
            Set<String> noReportMobiles = riskTelInfoService.findNoReportMobiles(calendar.getTime());
            
            logger.info("-----运营商报告补偿任务--> 共发现30天内有 {} 条数据没有成功获取到运营商报告。", noReportMobiles.size());

            String sid;
            String uid;
            String mobile;
            Long nowTime = Calendar.getInstance().getTimeInMillis();
            for (String noReportMobile : noReportMobiles) {
                TRiskTelInfo noReportRecord = riskTelInfoService.findByMobile(noReportMobile);
                
                UserEntity userEntity = userService.findByUserCode(noReportRecord.getUid());
                
                if (userEntity == null){
                    logger.info("-----运营商报告补偿任务--> t_raptor_user表未找到userCode为{}的数据, 跳过", noReportRecord.getUid());
                    continue;
                }
                
                sid = noReportRecord.getSid();
                uid = noReportRecord.getUid();
                mobile = noReportRecord.getMobile();
                
                //入库不到一小时的跳过
                if (nowTime - noReportRecord.getUpdatedAt().getTime() < 60 * 60 * 1000){
                    logger.info("-----运营商报告补偿任务-->入库时间小于一小时，跳过。mobile: {}, userCode: {}", mobile, uid);
                    continue;
                }

                String report = riskController.getCallLogReport(sid, "report");
                
                //通知用户状态，报告已生成
                try {
                    if (report != null){
                        String fileName = ossProperties.getCatalogCallLog() + "/" + sockpuppet + "-" + mobile + "-report.json";
                        riskController.uploadFile2Oss(report, fileName);
                        
                        userService.updateReceiveCallHistory(uid, true);
                        noReportRecord.setReportReceived(true);
                        riskTelInfoService.update(noReportRecord);
                        logger.info("-----运营商报告补偿任务-->定时任务更新用户运营商报告获取状态成功，tel: " + mobile + ", uid: " + uid + ", sid: " + sid);
                    }else {
                        logger.info("-----运营商报告补偿任务-->用户提交认证后1小时内未获取到有效的运营商报告, 回退用户状态，tel: {}, uid: {}, sid: {}", mobile, uid, sid);
                        userService.backToCollecting(uid, "用户提交认证后1小时内未获取到有效的运营商报告");
                    }
                    
                } catch (Exception e) {
                    logger.error("-----运营商报告补偿任务-->定时任务更新用户运营商报告获取状态失敗，tel: " + mobile + ", uid: " + uid+ ", sid: " + sid, e);
                }
                
            }

            logger.info("-----执行运营商报告补偿任务执行完成-----");
        }
    }
    
}
