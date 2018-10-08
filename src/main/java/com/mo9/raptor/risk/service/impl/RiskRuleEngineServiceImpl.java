package com.mo9.raptor.risk.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.risk.entity.TRiskTelInfo;
import com.mo9.raptor.risk.service.RiskRuleEngineService;
import com.mo9.raptor.risk.service.RiskTelInfoService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.log.Log;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * @author wtwei .
 * @date 2018/10/8 .
 * @time 15:13 .
 */

@Service("riskRuleEngineService")
public class RiskRuleEngineServiceImpl implements RiskRuleEngineService {
    //手机号入网时长阈值 150天
    private static final Long ONLINE_LENGTH_LIMIT = 150 * 24 * 60 * 60L;
    
    private static final int CALL_MERGENCY_TIMES = 1;
    
    private static final int ONE_LOAN_COMPANY_CALL_TIMES = 3;
    
    private static final int DIFFERENT_LOAN_COMPANY_CALL_TIMES = 6;


    @Value("${raptor.sockpuppet}")
    private String sockpuppet;

    @Value("${raptor.oss.read-endpoint}")
    private String readEndpoint;

    @Value("${raptor.oss.catalog.callLogReport}")
    private String secondDomain;

    private static Logger logger = Log.get();
    
    
    @Resource
    private HttpClientApi httpClientApi;
    
    @Resource
    private RiskTelInfoService riskTelInfoService;
    
    @Resource
    private UserService userService;
    
    @Resource
    private ILoanOrderService loanOrderService;
    
    @Override
    public AuditResponseEvent openDateRule(String userCode) {
        UserEntity userEntity = userService.findByUserCode(userCode);
        String mobile = userEntity.getMobile();
        
        TRiskTelInfo telInfo = riskTelInfoService.findByMobile(mobile);
        if (telInfo == null){
            logger.warn("规则引擎未找到用户的通话记录详单信息，mobile：{}", mobile);
            return new AuditResponseEvent(userCode, false, "规则引擎未找到用户的通话记录详单信息");
        }
        
        String openDate = telInfo.getOpenDate();
        if (StringUtils.isBlank(openDate)){
            logger.warn("手机号码 {} 的开户时间为空, 校验失败", mobile);
            return new AuditResponseEvent(userCode, false, "手机号码的开户时间为");
        }
        
        Long openDateMillions = Long.parseLong(openDate);
        
        //当前时间 - 开户时间  > 150天
        if (Calendar.getInstance().getTimeInMillis() - openDateMillions > ONLINE_LENGTH_LIMIT){
            return new AuditResponseEvent(userCode, true, "");
        }

        return new AuditResponseEvent(userCode, false, "手机号码开户时间不足150天");
        
    }

    @Override
    public AuditResponseEvent mergencyCallTimesRule(String userCode) {

        UserEntity userEntity = userService.findByUserCode(userCode);
        String mobile = userEntity.getMobile();
        
        String reportJson = getReport(mobile);
        if (!checkReportStatus(reportJson)){
            logger.info("运营商报告状态不正常，校验失败，规则：mergencyCallTimesRule");
            return new AuditResponseEvent(userCode, false, "运营商报告状态不正常");
        }

        JSONObject jsonObject = JSON.parseObject(reportJson);

        JSONArray mergencyContactArray = jsonObject.getJSONArray("mergency_contact");

        for (int i = 0; i < mergencyContactArray.size(); i++) {
            JSONObject mergencyContract = mergencyContactArray.getJSONObject(i);

            Integer callTimes = mergencyContract.getInteger("call_times");
            
            if (callTimes >= CALL_MERGENCY_TIMES){
                return new AuditResponseEvent(userCode, true, "");
            }
        }
        
        return new AuditResponseEvent(userCode, false, "6个月内与紧急联系人通话次数少于" + CALL_MERGENCY_TIMES);
    }

    @Override
    public AuditResponseEvent mergencyHadNoDoneOrderRule(String userCode) {
        UserEntity userEntity = userService.findByUserCode(userCode);
        String mobile = userEntity.getMobile();
        
        String reportJson = getReport(mobile);
        if (!checkReportStatus(reportJson)){
            logger.info("运营商报告状态不正常，校验失败，规则：mergencyHadNoDoneOrderRule");
            return new AuditResponseEvent(userCode, false, "运营商报告状态不正常" );
        }

        List<String> LEND = Arrays.asList(StatusEnum.LENDING.name());
        LoanOrderEntity lendOrder = loanOrderService.getLastIncompleteOrder(userCode, LEND);
        if (lendOrder == null){
            return new AuditResponseEvent(userCode, true, "" );
        }
        
        return new AuditResponseEvent(userCode, false, "紧急联系人有未完成的订单" );
    }

    @Override
    public AuditResponseEvent mergencyInJHJJBlackListRule(String userCode) {
        UserEntity userEntity = userService.findByUserCode(userCode);
        String mobile = userEntity.getMobile();
        
        String reportJson = getReport(mobile);
        if (!checkReportStatus(reportJson)){
            logger.info("运营商报告状态不正常，校验失败，规则：mergencyInJHJJBlackListRule");
            return new AuditResponseEvent(userCode, false, "运营商报告状态不正常" );
        }
        return new AuditResponseEvent(userCode, false, "紧急联系人命中江湖救急黑名单" );
    }

    @Override
    public AuditResponseEvent calledTimesByOneLoanCompanyRule(String userCode) {
        UserEntity userEntity = userService.findByUserCode(userCode);
        String mobile = userEntity.getMobile();

        String reportJson = getReport(mobile);
        if (!checkReportStatus(reportJson)){
            logger.info("运营商报告状态不正常，校验失败，规则：calledTimesByOneLoanCompanyRule");
            return new AuditResponseEvent(userCode, false, "运营商报告状态不正常" );
        }
        return new AuditResponseEvent(userCode, false, "被同一贷款机构呼叫次数大于 " + ONE_LOAN_COMPANY_CALL_TIMES);
    }

    @Override
    public AuditResponseEvent calledTimesByDifferentLoanCompanyRule(String userCode) {
        UserEntity userEntity = userService.findByUserCode(userCode);
        String mobile = userEntity.getMobile();

        String reportJson = getReport(mobile);
        if (!checkReportStatus(reportJson)){
            logger.info("运营商报告状态不正常，校验失败，规则：calledTimesByDifferentLoanCompanyRule");
            return new AuditResponseEvent(userCode, false, "运营商报告状态不正常" );
        }
        return new AuditResponseEvent(userCode, false, "被不同贷款机构呼叫次数大于 " + DIFFERENT_LOAN_COMPANY_CALL_TIMES);
    }


    /**
     * 读取运营商报告
     * @param mobile
     * @return
     */
    private String getReport(String mobile){
        String url = readEndpoint + "/" + secondDomain + "/" + sockpuppet + "-" + mobile + "-report.json";

        try {
            logger.info("规则引擎读取用户运营商报告 -- {}", url);
            String reportJsonStr = httpClientApi.doGet(url);
            
            return reportJsonStr;
        } catch (IOException e) {
            logger.error("规则引擎读取用户运营商报告出错。" , e);
            return null;
        }
    }
    
    private boolean checkReportStatus(String reportJson){
        JSONObject jsonObject = JSON.parseObject(reportJson);
        Integer status = jsonObject.getInteger("status");
        
        if (status == 0){
            return true;
        }
        return false;
    }
}