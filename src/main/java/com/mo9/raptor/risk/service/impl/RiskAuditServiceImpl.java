package com.mo9.raptor.risk.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;
import com.mo9.raptor.entity.UserCertifyInfoEntity;
import com.mo9.raptor.entity.UserContactsEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.SourceEnum;
import com.mo9.raptor.risk.entity.TRiskCallLog;
import com.mo9.raptor.risk.repo.RiskCallLogRepository;
import com.mo9.raptor.risk.service.LinkFaceService;
import com.mo9.raptor.risk.service.RiskAuditService;
import com.mo9.raptor.service.RuleLogService;
import com.mo9.raptor.service.UserCertifyInfoService;
import com.mo9.raptor.service.UserContactsService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.MobileUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
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

    @Value("${risk.calllog.limit}")
    private int callLogLimit;

    @Resource
    private UserContactsService userContactsService;

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

    private static final String WHITE_LIST = "WHITE";

    private static final String ORIGN_CALL = "主叫";

    @Override
    public AuditResponseEvent audit(String userCode) {
        UserEntity user = userService.findByUserCodeAndDeleted(userCode, false);
        if (!user.getReceiveCallHistory()) {
            return null;
        }

        AuditResponseEvent finalResult = null;
        AuditResponseEvent res;

        if (!SourceEnum.WHITE.equals(user.getSource())) {
            logger.info(userCode + "开始运行规则[ContactsRule]");
            res = contactsRule(userCode);
            ruleLogService.create(userCode, "ContactsRule", res.isPass(), true, res.getExplanation());
            if (!res.isPass()) {
                finalResult = res;
            }
        } else {
            ruleLogService.create(userCode, "ContactsRule", null, false, "");
        }

        if (finalResult == null) {
            logger.info(userCode + "开始运行规则[CallLogRule]");
            res = callLogRule(userCode);
            ruleLogService.create(userCode, "CallLogRule", res.isPass(), true, res.getExplanation());
            if (!res.isPass()) {
                finalResult = res;
            }
        } else {
            ruleLogService.create(userCode, "CallLogRule", null, false, "");
        }

        if (finalResult == null) {
            logger.info(userCode + "开始运行规则[ThreeElementCheck]");
            res = threeElementCheck(userCode);
            ruleLogService.create(userCode, "ThreeElementCheck", res.isPass(), true, res.getExplanation());
            if (!res.isPass()) {
                finalResult = res;
            }
        } else {
            ruleLogService.create(userCode, "ThreeElementCheck", null, false, "");
        }

        if (finalResult == null) {
            logger.info(userCode + "开始运行规则[AntiHackRule]");
            res = antiHackRule(userCode);
            ruleLogService.create(userCode, "AntiHackRule", res.isPass(), true, res.getExplanation());
            if (!res.isPass()) {
                finalResult = res;
            }
        } else {
            ruleLogService.create(userCode, "AntiHackRule", null, false, "");
        }

        if (finalResult == null) {
            logger.info(userCode + "开始运行规则[LivePicCompareRule]");
            res = livePicCompareRule(userCode);
            ruleLogService.create(userCode, "LivePicCompareRule", res.isPass(), true, res.getExplanation());
            if (!res.isPass()) {
                finalResult = res;
            }
        } else {
            ruleLogService.create(userCode, "LivePicCompareRule", null, false, "");
        }

        if (finalResult == null) {
            logger.info(userCode + "开始运行规则[IdPicCompareRule]");
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

    AuditResponseEvent contactsRule(String userCode) {
        int contactsLimit = 30;
        int orignCallLimit = 10;
        Long days180ts = 180 * 24 * 60 * 60 * 1000L;
        long currentTimeMillis = System.currentTimeMillis();
        UserEntity user = userService.findByUserCode(userCode);
        UserContactsEntity userContacts = userContactsService.getByUserCode(userCode);
        String json = userContacts.getContactsList();
        try {
            JSONArray jsonArray;
            if (json.startsWith("{")) {
                jsonArray = JSON.parseObject(json).getJSONArray("contact");
            } else {
                jsonArray = JSON.parseArray(json);
            }
            HashSet<String> allMobileSet = new HashSet<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                String mobile = MobileUtil.processMobile(jsonArray.getJSONObject(i).getString("contact_mobile"));
                if (StringUtils.isNotBlank(mobile)) {
                    allMobileSet.add(mobile);
                }
            }
            if (allMobileSet.size() < contactsLimit) {
                return new AuditResponseEvent(userCode, false, "通讯录数量小于30个");
            }
            int count = 0;
            HashSet<String> inListMobiles = new HashSet<>();
            //MYCAI限制1000条
            List<TRiskCallLog> allCallLog = new ArrayList<>();
            Long lastId = 0L;
            while (true) {
                List<TRiskCallLog> list = riskCallLogRepository.getCallLogByMobileAfterTimestamp(user.getMobile(), (currentTimeMillis - days180ts) / 1000, ORIGN_CALL, lastId);
                allCallLog.addAll(list);
                if (list.size() == 0) {
                    break;
                } else {
                    lastId = list.get(list.size() - 1).getId();
                }
            }
            for (TRiskCallLog tRiskCallLog : allCallLog) {
                if (ORIGN_CALL.equals(tRiskCallLog.getCallMethod()) && allMobileSet.contains(tRiskCallLog.getCallTel())) {
                    count++;
                    inListMobiles.add(tRiskCallLog.getCallTel());
                }
            }
            StringBuilder stringBuilder = new StringBuilder(userCode + "," + user.getMobile() + "在主叫列表里[");
            for (String inListMobile : inListMobiles) {
                stringBuilder.append(inListMobile + ",");
            }
            stringBuilder.append("]");
            logger.info(stringBuilder.toString());
            return new AuditResponseEvent(userCode, count >= orignCallLimit, count >= orignCallLimit ? "" : "180天主动拨打通讯录号码小于10次(共" + count + "次)");
        } catch (Exception e) {
            logger.error(userCode + "解析联系人出错", e);
            logger.info(userCode + json);
            return new AuditResponseEvent(userCode, false, "解析联系人出错！");
        }
    }

    AuditResponseEvent idPicCompareRule(String userCode) {
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

    AuditResponseEvent livePicCompareRule(String userCode) {
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

    AuditResponseEvent antiHackRule(String userCode) {
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

    AuditResponseEvent callLogRule(String userCode) {
        Long days180ts = 180 * 24 * 60 * 60 * 1000L;
        long currentTimeMillis = System.currentTimeMillis();
        UserEntity user = userService.findByUserCode(userCode);
        int count = riskCallLogRepository.getCallLogCountAfterTimestamp(user.getMobile(), (currentTimeMillis - days180ts) / 1000);
        int limit = callLogLimit;
        return new AuditResponseEvent(userCode, count >= limit, count >= limit ? "" : "用户最近180天通话记录少于" + limit);
    }

    AuditResponseEvent threeElementCheck(String userCode) {
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
