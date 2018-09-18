package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.req.risk.CallLogReq;
import com.mo9.raptor.risk.entity.TRiskCallLog;
import com.mo9.raptor.risk.entity.TRiskTelBill;
import com.mo9.raptor.risk.entity.TRiskTelInfo;
import com.mo9.raptor.risk.service.RiskCallLogService;
import com.mo9.raptor.risk.service.RiskTelBillService;
import com.mo9.raptor.risk.service.RiskTelInfoService;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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

    @PostMapping(value = "/save_call_log")
    public String saveCallLogResult(@RequestBody CallLogReq callLogReq){
        logger.info("----收到通话记录post数据-----");
        logger.info(callLogReq.toString());
        
        if (callLogReq.getStatus() != 0){
            logger.error(">>>>>>>>>>>第三方通话记录爬虫失败");
        }
        //机主信息
        TRiskTelInfo riskTelInfo = riskTelInfoService.coverReq2Entity(callLogReq);
        riskTelInfoService.save(riskTelInfo);
        
        //账单信息
        List<TRiskTelBill> riskTelBillList = riskTelBillService.coverReq2Entity(callLogReq);
        riskTelBillService.batchSave(riskTelBillList);
        
        //通话记录
        List<TRiskCallLog> riskCallLogList = riskCallLogService.coverReqToEntity(callLogReq);
        riskCallLogService.batchSave(riskCallLogList);
            
        writeFile2Disk(callLogReq.toString(), callLogReq.getData().getTel());
        //todo 保存完成后，数据传给忆楠一份
        
        
        return "ok";
    }
    
    private void writeFile2Disk(String str, String mobile){
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String dirName = sf.format(date);
        String dir = FILE_PATH + dirName;

        File dirF = new File(dir);
        if (!dirF.exists()){
            dirF.mkdir();
        }
        
        try {
            File file = new File(dirF + File.separator + mobile + ".json");
            if (!file.exists()){
                file.createNewFile();
            }
            
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(str.getBytes());
            
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
