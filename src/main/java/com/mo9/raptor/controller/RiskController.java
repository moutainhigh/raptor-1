package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.mo9.raptor.bean.req.risk.CallLogReq;
import com.mo9.raptor.entity.DianHuaBangApiLogEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.risk.entity.TRiskCallLog;
import com.mo9.raptor.risk.entity.TRiskTelBill;
import com.mo9.raptor.risk.entity.TRiskTelInfo;
import com.mo9.raptor.risk.service.RiskCallLogService;
import com.mo9.raptor.risk.service.RiskTelBillService;
import com.mo9.raptor.risk.service.RiskTelInfoService;
import com.mo9.raptor.service.DianHuaBangApiLogService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.CallLogUtils;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.log.Log;
import com.mo9.raptor.utils.oss.OSSProperties;
import okhttp3.OkHttpClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Calendar;
import java.util.List;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 15:09 .
 */

@RestController
@RequestMapping("/risk")
public class RiskController {

    private static Logger logger = Log.get();
    @Resource
    private RiskTelInfoService riskTelInfoService;
    
    @Resource
    private DianHuaBangApiLogService dianHuaBangApiLogService;
    
    @Resource
    private UserService userService;
    
    @Resource
    private OSSProperties ossProperties;
    
    @Resource
    private HttpClientApi httpClientApi;
    
    @Value("${raptor.sockpuppet}")
    private String sockpuppet;
    
    @Value("${risk.dianhuaapi.url}")
    private String dianhuUrl;
    
    @Value("${risk.dianhuaapi.token}")
    private String dianhuToken;
    
    
    @PostMapping(value = "/call_log_auth_result")
    public String callLogAuthResult(@RequestBody String authJson){
        logger.info("----收到通话授权结果数据-----> " + authJson);
        JSONObject jsonObject = JSONObject.parseObject(authJson);
        Long status = jsonObject.getLong("status");
        
        if (status == 0){
            logger.info("通话记录爬虫授权成功");
        }
        
        return "ok";
    }

    @PostMapping(value = "/save_call_log")
    public String saveCallLogResult(@RequestBody String callLogJson, HttpServletRequest request){
        CallLogReq callLogReq = JSONObject.parseObject(callLogJson, CallLogReq.class);
        logger.info("----收到通话记录post数据-----> tel: " + callLogReq.getData().getTel() + 
                ", uid: " + callLogReq.getData().getUid() + 
                ", sid: " + callLogReq.getData().getSid());
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //记录日志
                    if (request != null) {
                        DianHuaBangApiLogEntity logEntity = createLogEntity(callLogReq);
                        dianHuaBangApiLogService.create(logEntity);
                    }
                }catch (Exception e){
                    logger.error("保存电话邦调用日志出错", e);
                }


                if (callLogReq.getStatus() != 0 || callLogReq.getData() == null){
                    logger.error("--------------第三方通话记录爬虫失败----------");
                    logger.error(callLogJson);
                }else {
                    //保存通话记录所有信息
                    riskTelInfoService.saveAllCallLogData(callLogReq);

                    //上传通话记录文件
                    String fileName = ossProperties.getCatalogCallLog() + "/callLog/" + sockpuppet + "-" + callLogReq.getData().getTel() + ".json";
                    uploadFile2Oss(callLogReq.toString(),  fileName);
                }
            }
        }).start();
        
        
        return "ok";
    }
    
    
    @PostMapping(value = "/call_log_report_status")
    public String receiveCallLogReport(@RequestBody String statusJson){
        logger.info("-----收到运营商生成报告状态通知-------> " + statusJson);
        JSONObject jsonObject = JSONObject.parseObject(statusJson);
        int status= jsonObject.getInteger("status");
        if (status == 0){
            //上传运营商报告文件
            String report = this.getCallLogReport(jsonObject.getString("sid"), "report");
            String tel = jsonObject.getString("tel");
            String uid = jsonObject.getString("uid");
            if (report != null){
                String fileName = ossProperties.getCatalogCallLog() + "/" + sockpuppet + "-" + tel + "-report.json";
                
                this.uploadFile2Oss(report, fileName);
                try {
                    
                    //通知用户状态，报告已生成
                    userService.updateReceiveCallHistory(uid, true);

                    TRiskTelInfo riskTelInfo =  riskTelInfoService.findByMobile(tel);
                    riskTelInfo.setReportReceived(true);
                    riskTelInfoService.update(riskTelInfo);
                    logger.info("更新用户通话记录历史信息成功，tel: " + tel + ", uid: " + uid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        return "ok";
    }
    
    
    @PostMapping(value = "/pull_call_log")
    public String mobile2Sid(@RequestBody String sessionId){
        if (StringUtils.isBlank(sessionId)){
            return "sessionId不能为空";
        }
        
        CallLogUtils callLogUtils = new CallLogUtils();
        OkHttpClient httpClient = new OkHttpClient();
        try {
            List<UserEntity> noReportUsers = userService.findNoCallLogReports();
            
            logger.info("----共有{}个没有运营商报告的用户记录。", noReportUsers.size());
            for (UserEntity noReportUser : noReportUsers) {
                TRiskTelInfo hasCallLogUser = riskTelInfoService.findByMobile(noReportUser.getMobile());
                if (hasCallLogUser == null){ 
                    logger.info("-----UserCode为{}的用户未查询到有通话记录，现在重新拉取。", noReportUser.getUserCode());
                    //没有通话记录，则先查找sid，然后主动拉取callLog
                    String sid = callLogUtils.getSidByMobile(sessionId, noReportUser.getMobile(), httpClient);
                    if (StringUtils.isNotBlank(sid)){
                        
                        String callLogJson = this.getCallLogReport(sid, "record");
                        if (StringUtils.isNotBlank(callLogJson)){
                            logger.info("----UserCode为{}的用户成功拉取到通话记录", noReportUser.getUserCode());
                            this.saveCallLogResult(callLogJson, null);
                        }
                    }else {
                        logger.info("----未查询到UserCode为{}，手机号为{}的sid信息，拉取失败", noReportUser.getUserCode(), noReportUser.getMobile());
                    }
                }
                
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "ok";
    }
    
    public void uploadFile2Oss(String str, String fileName){
        
        try {
            OSSClient ossClient = new OSSClient(ossProperties.getWriteEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret());
            ossClient
                    .putObject(
                        ossProperties.getBucketName(),
                        fileName,
                        new ByteArrayInputStream(str.getBytes())
            );
            ossClient.shutdown();

            StringBuilder sb = new StringBuilder();
            sb.append(ossProperties.getHttpPrefix())
                    .append(ossProperties.getReadEndpoint().substring(ossProperties.getHttpPrefix().length()))
                    .append("/").append(fileName);
            logger.info("CallLog文件上传成功：" + sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取运营商报告
     * @param sid
     * @return
     */
    
    private int MAX_PULL_REPORT_TIMES = 3;
    
    public String getCallLogReport(String sid, String recordOrReport){
        String url = dianhuUrl + recordOrReport + "?token=" + dianhuToken + "&sid=" + sid;

        logger.info(url);
        try {
            String report = httpClientApi.doGet(url);
            
            JSONObject jsonObject = JSONObject.parseObject(report);
            Long status = jsonObject.getLong("status");
            
            if (status == 3101){
                Thread.sleep(60 * 1000);
                logger.info("运营商报告数据生成中, sid: " + sid);
                
                return null;
            }
            
            if (status != 0){
                logger.error("运营商报告获取异常：" + report);
                return null;
            }
            
            if (report != null){
                return report;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private DianHuaBangApiLogEntity createLogEntity(CallLogReq callLogReq){
        DianHuaBangApiLogEntity entity = new DianHuaBangApiLogEntity();
        
        if (callLogReq.getData() != null){
            entity.setMobile(callLogReq.getData().getTel());
            entity.setSid(callLogReq.getData().getSid());
            entity.setUid(callLogReq.getData().getUid());
        }

        entity.setRemark(callLogReq.getMsg());
        entity.setStatus(Long.parseLong(callLogReq.getStatus() + ""));
        entity.setPlatform(sockpuppet);
        
        return entity;
    }
}
