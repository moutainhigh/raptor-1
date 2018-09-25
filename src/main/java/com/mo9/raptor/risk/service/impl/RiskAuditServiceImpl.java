package com.mo9.raptor.risk.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;
import com.mo9.raptor.entity.UserCertifyInfoEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.risk.entity.TRiskCallLog;
import com.mo9.raptor.risk.repo.RiskCallLogRepository;
import com.mo9.raptor.risk.service.LinkFaceService;
import com.mo9.raptor.risk.service.RiskAuditService;
import com.mo9.raptor.service.RuleLogService;
import com.mo9.raptor.service.UserCertifyInfoService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.oss.OSSFileUpload;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yngong
 */
@Service("riskAuditService")
public class RiskAuditServiceImpl implements RiskAuditService {

    @Value("${raptor.sockpuppet}")
    private String sockpuppet;

    @Value("${raptor.oss.read-endpoint}")
    private String readEndpoint;

    @Value("${raptor.oss.catalog.callLogReport}")
    private String secondDomain;

    @Resource
    private OSSFileUpload ossFileUpload;

    private static final Logger logger = LoggerFactory.getLogger(RiskAuditServiceImpl.class);

    @Resource
    private RiskCallLogRepository riskCallLogRepository;

    @Resource
    private LinkFaceService linkFaceService;

    @Resource
    private UserCertifyInfoService userCertifyInfoService;

    @Resource
    private UserService userService;

    @Resource
    private RuleLogService ruleLogService;

    @Override
    public AuditResponseEvent audit(String userCode) {
        UserEntity user = userService.findByUserCodeAndDeleted(userCode, false);
        if (!user.getReceiveCallHistory()) {
            return null;
        }
        AuditResponseEvent finalResult = null;
        AuditResponseEvent res = callLogRule(userCode);
        ruleLogService.create(userCode, "CallLogRule", res.isPass(), true, res.getExplanation());
        if (!res.isPass()) {
            finalResult = res;
        }

        if (finalResult == null) {
            res = threeElementCheck(userCode);
            ruleLogService.create(userCode, "ThreeElementCheck", res.isPass(), true, res.getExplanation());
            if (!res.isPass()) {
                finalResult = res;
            }
        } else {
            ruleLogService.create(userCode, "ThreeElementCheck", null, false, "");
        }

        if (finalResult == null) {
            res = antiHackRule(userCode);
            ruleLogService.create(userCode, "AntiHackRule", res.isPass(), true, res.getExplanation());
            if (!res.isPass()) {
                finalResult = res;
            }
        } else {
            ruleLogService.create(userCode, "AntiHackRule", null, false, "");
        }

        if (finalResult == null) {
            res = livePicCompareRule(userCode);
            ruleLogService.create(userCode, "LivePicCompareRule", res.isPass(), true, res.getExplanation());
            if (!res.isPass()) {
                finalResult = res;
            }
        } else {
            ruleLogService.create(userCode, "LivePicCompareRule", null, false, "");
        }

        if (finalResult == null) {
            res = idPicCompareRule(userCode);
            ruleLogService.create(userCode, "IdPicCompareRule", res.isPass(), true, res.getExplanation());
            finalResult = res;
        } else {
            ruleLogService.create(userCode, "IdPicCompareRule", null, false, "");
        }
        return finalResult;
    }

    private static final int HTTP_OK = 200;
    private static final int ERROR_SCORE_CODE = -1;

    public AuditResponseEvent idPicCompareRule(String userCode) {
        UserCertifyInfoEntity userCertifyInfo = userCertifyInfoService.findByUserCode(userCode);
        double limit = 0.7;
        if (userCertifyInfo != null) {
            String url = userCertifyInfo.getAccountOcr();
            double score = linkFaceService.judgeOnePerson(userCode, url, userCertifyInfo.getIdCard(), userCertifyInfo.getRealName());
            if (score == ERROR_SCORE_CODE) {
                return new AuditResponseEvent(userCode, false, "调用LINKFACE出错");
            } else {
                return new AuditResponseEvent(userCode, score >= limit, score >= limit ? "" : "活体照与公安库身份证照比对,相似度低于" + limit);
            }
        }
        return new AuditResponseEvent(userCode, false, "查询不到用户" + userCode + "数据");
    }

    public AuditResponseEvent livePicCompareRule(String userCode) {
        UserCertifyInfoEntity userCertifyInfo = userCertifyInfoService.findByUserCode(userCode);
        double limit = 0.7;
        if (userCertifyInfo != null) {
            String url = userCertifyInfo.getAccountFrontImg();
            double score = linkFaceService.judgeIdCardPolice(userCode, url, userCertifyInfo.getIdCard(), userCertifyInfo.getRealName());
            if (score == ERROR_SCORE_CODE) {
                return new AuditResponseEvent(userCode, false, "调用LINKFACE出错");
            } else {
                return new AuditResponseEvent(userCode, score >= limit, score >= limit ? "" : "身份证正面与公安照正面对比,相似度低于" + limit);
            }
        }
        return new AuditResponseEvent(userCode, false, "查询不到用户" + userCode + "数据");
    }

    public AuditResponseEvent antiHackRule(String userCode) {
        UserCertifyInfoEntity userCertifyInfo = userCertifyInfoService.findByUserCode(userCode);
        double limit = 0.98;
        if (userCertifyInfo != null) {
            String url = userCertifyInfo.getAccountOcr();
            double score = linkFaceService.preventHack(userCode, url);
            if (score == ERROR_SCORE_CODE) {
                return new AuditResponseEvent(userCode, false, "调用LINKFACE出错");
            } else {
                return new AuditResponseEvent(userCode, score <= limit, score <= limit ? "" : "验证用户照片是否为活体照片,HACK可能性高于" + limit);
            }
        }
        return new AuditResponseEvent(userCode, false, "查询不到用户" + userCode + "数据");
    }

    @Value("${risk.calllog.limit}")
    private int callLogLimit;

    public AuditResponseEvent callLogRule(String userCode) {
        Long days90ts = 90 * 24 * 60 * 60 * 1000L;
        int limit = callLogLimit;
        List<TRiskCallLog> callLogList = riskCallLogRepository.getCallLogByUid(userCode);
        int count = 0;
        long currentTimeMillis = System.currentTimeMillis();
        for (TRiskCallLog tRiskCallLog : callLogList) {
            try {
                Long timestamp = Long.valueOf(tRiskCallLog.getCallTime()) * 1000;
                if (currentTimeMillis - days90ts <= timestamp) {
                    count++;
                }
            } catch (Exception e) {
            }
        }
        return new AuditResponseEvent(userCode, count >= limit, count >= limit ? "" : "用户最近90天通话记录少于" + limit);
    }

    public AuditResponseEvent threeElementCheck(String userCode) {
        UserEntity user = userService.findByUserCode(userCode);
        try {
            if (user != null && StringUtils.isNotBlank(user.getMobile())) {
                String url = readEndpoint + "/" + secondDomain + "/" + sockpuppet + "-" + user.getMobile() + "-report.json";

                //String url = ossFileUpload.buildFileURL(sockpuppet + "-" + user.getMobile() + "-report.json");
                logger.info("读取报告" + url);
                OkHttpClient okHttpClient = new OkHttpClient();
                Response response = okHttpClient.newCall(new Request.Builder().get().url(url).build()).execute();
                if (response.code() == HTTP_OK) {
                    String json = response.body().string();
                    JSONObject jsonObject = JSON.parseObject(json);
                    Integer status = jsonObject.getInteger("status");
                    Integer checkResult = null;
                    if (status == 0) {
                        if (jsonObject.containsKey("data")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            if (data.containsKey("user_info")) {
                                JSONObject userInfo = data.getJSONObject("user_info");
                                if (userInfo.containsKey("conclusion_of_3_elements_check")) {
                                    checkResult = userInfo.getInteger("conclusion_of_3_elements_check");
                                }
                            }
                        }
                    }
                    if (checkResult == null) {
                        return new AuditResponseEvent(userCode, false, userCode + "[" + user.getMobile() + "]报告出错");
                    } else {
                        return new AuditResponseEvent(userCode, checkResult == 1 || checkResult == 5 || checkResult == 6, (checkResult == 1 || checkResult == 5 || checkResult == 6) ? "" : "实名三要素未通过");
                    }
                } else {
                    return new AuditResponseEvent(userCode, false, "报告不存在");
                }
            } else {
                return new AuditResponseEvent(userCode, false, "查询不到该用户，或者该用户手机号为空");
            }
        } catch (Exception e) {
            logger.error(userCode + "检查报告出错", e);
            return new AuditResponseEvent(userCode, false, "致命问题！！检查运营商报告出错");
        }
    }
}
