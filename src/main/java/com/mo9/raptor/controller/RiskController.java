package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.mo9.raptor.entity.DianHuaBangApiLogEntity;
import com.mo9.raptor.entity.RiskMergencyContact;
import com.mo9.raptor.entity.RiskTelYellowPage;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.service.DianHuaBangApiLogService;
import com.mo9.raptor.service.RiskMergencyContactService;
import com.mo9.raptor.service.RiskTelYellowPageService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.CallLogUtils;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.log.Log;
import com.mo9.raptor.utils.oss.OSSProperties;
import com.mo9.risk.bean.CallLogReq;
import com.mo9.risk.entity.TRiskTelInfo;
import com.mo9.risk.service.RiskTelInfoService;
import okhttp3.OkHttpClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 15:09 .
 */

@RestController
@RequestMapping("/risk")
public class RiskController {
    
    private ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
            5,
            50,
            10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(200),
            new ThreadPoolExecutor.CallerRunsPolicy());

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
    
    @Resource
    private RiskMergencyContactService riskMergencyContactService;
    
    @Resource
    private RiskTelYellowPageService riskTelYellowPageService;
    
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
        
        poolExecutor.execute(new Runnable() {
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
        });
        
        
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
                    
                    try {
                        saveYellowPageAndContact(report);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        return "ok";
    }
    
    
    @PostMapping(value = "/pull_call_log")
    public String processNoSidMobile(@RequestBody String sessionId){
        if (StringUtils.isBlank(sessionId)){
            return "sessionId不能为空";
        }
        
        CallLogUtils callLogUtils = new CallLogUtils();
        OkHttpClient httpClient = new OkHttpClient();
        try {
            List<UserEntity> noReportUsers = userService.findNoCallLogReports();
            
            logger.info("----共有{}个没有SID的用户记录。", noReportUsers.size());
            for (UserEntity noReportUser : noReportUsers) {
                TRiskTelInfo hasCallLogUser = riskTelInfoService.findByMobile(noReportUser.getMobile());
                
                if (hasCallLogUser == null){
                    logger.info("-----手机号为 {} 的用户未查询到有通话记录，现在重新拉取。", noReportUser.getMobile());
                    //没有通话记录，则先查找sid，然后主动拉取callLog
                    String sid = callLogUtils.getSidByMobile(sessionId, noReportUser.getMobile(), httpClient);
                    logger.info("----获取sid。mobile: {}, sid: {}",noReportUser.getMobile(), sid);
                    if (StringUtils.isNotBlank(sid)){
                        
                        String callLogJson = this.getCallLogReport(sid, "record");
                        if (StringUtils.isNotBlank(callLogJson)){
                            logger.info("----Mobile为 {} 的用户成功拉取到通话记录", noReportUser.getMobile());
                            this.saveCallLogResult(callLogJson, null);
                        }else {
                            logger.info("----通话记录详单不存在或采集失败，回退用户状态。mobile: {} , sid: {}", noReportUser.getMobile(), sid);
                            userService.backToCollecting(noReportUser.getUserCode(), "通话记录采集失败");
                        }
                    }else {
                        logger.info("----未查询到UserCode为 {} ，手机号为 {} 的sid信息，拉取失败，回退用户状态。", noReportUser.getUserCode(), noReportUser.getMobile());
                        userService.backToCollecting(noReportUser.getUserCode(), "在电话邦未查询到用户的SID");
                    }
                }else if (hasCallLogUser != null && hasCallLogUser.isReportReceived()){
                    try {
                        logger.info("----没有sid的用户补偿任务---已收到有效的运营商报告，通知userService更改状态, {}", hasCallLogUser.getMobile());
                        userService.updateReceiveCallHistory(hasCallLogUser.getUid(), true);
                    } catch (Exception e) {
                        logger.error("运行calllog补偿任务，通知userService收到通话记录时出错", e);
                    }
                }
                
            }
            
        } catch (Exception e) {
            logger.error("处理没有sid的用户时出现错误", e);
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
            
            if (status != 0 || report.length() < 100){
                logger.error("运营商报告获取异常：" + report);
                return null;
            }
            
            if (report != null){
                return report;
            }
        } catch (Exception e) {
            logger.error("获取电话邦数据出现致命错误", e);
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

    //emoji表情
    final Pattern pattern = Pattern.compile("(?:[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83E\uDD00-\uD83E\uDDFF]|[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|[\u2600-\u26FF]\uFE0F?|[\u2700-\u27BF]\uFE0F?|\u24C2\uFE0F?|[\uD83C\uDDE6-\uD83C\uDDFF]{1,2}|[\uD83C\uDD70\uD83C\uDD71\uD83C\uDD7E\uD83C\uDD7F\uD83C\uDD8E\uD83C\uDD91-\uD83C\uDD9A]\uFE0F?|[\u0023\u002A\u0030-\u0039]\uFE0F?\u20E3|[\u2194-\u2199\u21A9-\u21AA]\uFE0F?|[\u2B05-\u2B07\u2B1B\u2B1C\u2B50\u2B55]\uFE0F?|[\u2934\u2935]\uFE0F?|[\u3030\u303D]\uFE0F?|[\u3297\u3299]\uFE0F?|[\uD83C\uDE01\uD83C\uDE02\uD83C\uDE1A\uD83C\uDE2F\uD83C\uDE32-\uD83C\uDE3A\uD83C\uDE50\uD83C\uDE51]\uFE0F?|[\u203C\u2049]\uFE0F?|[\u25AA\u25AB\u25B6\u25C0\u25FB-\u25FE]\uFE0F?|[\u00A9\u00AE]\uFE0F?|[\u2122\u2139]\uFE0F?|\uD83C\uDC04\uFE0F?|\uD83C\uDCCF\uFE0F?|[\u231A\u231B\u2328\u23CF\u23E9-\u23F3\u23F8-\u23FA]\uFE0F?)");

    /**
     * 保存黄页和紧急联系人数据
     * @param reportJson
     */
    public void saveYellowPageAndContact(String reportJson){

        poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(reportJson).getJSONObject("data");
                    String mobile = jsonObject.getJSONObject("tel_info").getString("tel");

                    //保存紧急联系人
                    JSONArray mergencyObjectArray = jsonObject.getJSONArray("mergency_contact");

                    for (int i = 0; i < mergencyObjectArray.size(); i++) {
                        JSONObject mergencyObject = mergencyObjectArray.getJSONObject(i);
                        String contactName = mergencyObject.getString("contact_name");
                        Matcher matcher = pattern.matcher(contactName);
                        contactName = matcher.find() ? matcher.replaceAll("") : contactName;

                        RiskMergencyContact mergencyContact = new RiskMergencyContact();
                        mergencyContact.setTagsLabel(mergencyObject.getString("tags_label"));

                        List tagsFinaList = mergencyObject.getJSONArray("tags_financial").toJavaList(String.class);

                        mergencyContact.setTagsFinancial(list2Str(tagsFinaList));
                        mergencyContact.setFanchaTelloc(mergencyObject.getString("fancha_telloc"));
                        mergencyContact.setContractTel(mergencyObject.getString("format_tel"));
                        mergencyContact.setMobile(mobile);
                        mergencyContact.setCallTimes(mergencyObject.getString("call_times"));
                        mergencyContact.setCallLength(mergencyObject.getString("call_length"));
                        mergencyContact.setContactRelationship(mergencyObject.getString("contact_relationship"));
                        mergencyContact.setContactName(contactName);
                        mergencyContact.setContactPriority(mergencyObject.getInteger("contact_priority"));
                        mergencyContact.setTagsYellowPage(mergencyObject.getString("tags_yellow_page"));
                        riskMergencyContactService.saveOrUpdate(mergencyContact);
                        logger.info("保存紧急联系人, mobile: {}, contactTel: {}", mobile, mergencyContact.getContractTel());
                    }


                    //保存黄页
                    JSONArray callLogLabelsArray = jsonObject.getJSONArray("call_log_group_by_tel");
                    JSONObject callLogJson = null;
                    String tagsLabel = null;
                    String tagsYellowPage = null;
                    List tagsFinancial = null;
                    String tagsFinancialStr = null;
                    RiskTelYellowPage yellowPage;
                    for (int i = 0; i < callLogLabelsArray.size(); i++) {
                        callLogJson = callLogLabelsArray.getJSONObject(i);
                        tagsLabel = callLogJson.getString("tags_label");
                        tagsYellowPage = callLogJson.getString("tags_yellow_page");
                        tagsFinancial = callLogJson.getJSONArray("tags_financial").toJavaList(String.class);

                        if (StringUtils.isBlank(tagsLabel) && StringUtils.isBlank(tagsYellowPage) && CollectionUtils.isEmpty(tagsFinancial)){
                            continue;
                        }

                        tagsFinancialStr = list2Str(tagsFinancial);

                        yellowPage = new RiskTelYellowPage();

                        yellowPage.setFormatTel(callLogJson.getString("format_tel"));
                        yellowPage.setTagsLabel(tagsLabel);
                        yellowPage.setTagsYellowPage(tagsYellowPage);
                        yellowPage.setTagsFinancial(tagsFinancialStr);
                        yellowPage.setTagsLabelTimes(callLogJson.getInteger("tags_label_times"));
                        yellowPage.setFanchaTelloc(callLogJson.getString("fancha_telloc"));

                        riskTelYellowPageService.saveOrUpdate(yellowPage);
                    }


                    logger.info("紧急联系人和黄页数据保存完成，mobile: {}", mobile);
                }catch (Exception e){

                    logger.error("保存紧急联系人和黄页数据失败, 本错误不影响正常的业务流程", e);
                }
            }
        });
    }
    
    private String list2Str(List list){
        String str = null;
        StringBuffer sb = new StringBuffer();
        for (Object o : list) {
            sb.append(o).append(",");
        }

        if (sb.length() > 0){
            str = sb.substring(0, sb.length() - 1);
        }
        
        return str;
    }
}
