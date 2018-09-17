package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.req.risk.CallLogReq;
import com.mo9.raptor.risk.entity.TRiskCallLog;
import com.mo9.raptor.risk.entity.TRiskTelBill;
import com.mo9.raptor.risk.entity.TRiskTelInfo;
import com.mo9.raptor.risk.service.RiskCallLogService;
import com.mo9.raptor.risk.service.RiskTelBillService;
import com.mo9.raptor.risk.service.RiskTelInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 15:09 .
 */

@RestController()
@RequestMapping("/risk")
public class RiskController {
    private static Logger logger = LoggerFactory.getLogger(RiskController.class);
    
    @Resource
    private RiskTelInfoService riskTelInfoService;
    
    @Resource
    private RiskTelBillService riskTelBillService;
    
    @Resource
    private RiskCallLogService riskCallLogService;

    @RequestMapping(value = "/save_call_log")
    public String saveCallLogResult(@RequestBody CallLogReq callLogReq){
        try{
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
            
        }catch (Exception e){
            logger.error(">>>>>>>>>>>>>保存第三方通话记录爬虫结果失败", e);
            return "error";
        }
        return "ok";
    }
}
