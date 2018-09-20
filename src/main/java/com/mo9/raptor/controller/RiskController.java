package com.mo9.raptor.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.mo9.raptor.bean.req.risk.CallLogReq;
import com.mo9.raptor.risk.entity.TRiskCallLog;
import com.mo9.raptor.risk.entity.TRiskTelBill;
import com.mo9.raptor.risk.entity.TRiskTelInfo;
import com.mo9.raptor.risk.service.RiskCallLogService;
import com.mo9.raptor.risk.service.RiskTelBillService;
import com.mo9.raptor.risk.service.RiskTelInfoService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.oss.OSSFileUpload;
import com.mo9.raptor.utils.oss.OSSProperties;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 15:09 .
 */

@RestController
@RequestMapping("/risk")
public class RiskController {
    private static final String FILE_PATH = "/data/calllogfile/";
    
    private static Logger logger = LoggerFactory.getLogger(RiskController.class);
    
    @Resource
    private RiskTelInfoService riskTelInfoService;
    
    @Resource
    private RiskTelBillService riskTelBillService;
    
    @Resource
    private RiskCallLogService riskCallLogService;
    
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
    
    

    @PostMapping(value = "/save_call_log")
    public String saveCallLogResult(@RequestBody String callLogJson){
        logger.info("----收到通话记录post数据-----> " + callLogJson);

        CallLogReq callLogReq = JSONObject.parseObject(callLogJson, CallLogReq.class);
        
        boolean callLogStatus = true;

        if (callLogReq.getStatus() != 0 || callLogReq.getData().getTel() == null){
            logger.error(">>>>>>>>>>>第三方通话记录爬虫失败");
            callLogStatus = false;
        }
        if (callLogStatus){
            //机主信息
            TRiskTelInfo riskTelInfo = riskTelInfoService.coverReq2Entity(callLogReq);
            riskTelInfoService.save(riskTelInfo);

            //账单信息
            List<TRiskTelBill> riskTelBillList = riskTelBillService.coverReq2Entity(callLogReq);
            riskTelBillService.batchSave(riskTelBillList);

            //通话记录
            List<TRiskCallLog> riskCallLogList = riskCallLogService.coverReqToEntity(callLogReq);
            riskCallLogService.batchSave(riskCallLogList);

            //上传通话记录文件
            this.uploadFile2Oss(callLogReq.toString(), sockpuppet + "-" + callLogReq.getData().getTel() + ".json" );

            //上传运营商报告文件
            String report = this.getCallLogReport(callLogReq.getData().getSid());
            if (report != null){
                this.uploadFile2Oss(report, sockpuppet + "-" + callLogReq.getData().getTel() + "-report.json");
            }
        }

        try {
            userService.updateReceiveCallHistory(callLogReq.getData().getUid(), callLogStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "ok";
    }
    
    private void uploadFile2Oss(String str, String fileName){
        
        try {
            new OSSClient(ossProperties.getWriteEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret())
                    .putObject(
                        ossProperties.getBucketName(),
                        fileName,
                        new ByteArrayInputStream(str.getBytes())
            );

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
    private String getCallLogReport(String sid){
        String url = dianhuUrl + "report?token=" + dianhuToken + "&sid=" + sid;

        logger.info(url);
        try {
            String report = httpClientApi.doGet(url);
            
            JSONObject jsonObject = JSONObject.parseObject(report);
            Long status = jsonObject.getLong("status");
            
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
    
}
