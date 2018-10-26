package com.mo9.raptor.risk.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.req.risk.CallLog;
import com.mo9.raptor.engine.enums.AuditResultEnum;
import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;
import com.mo9.raptor.entity.IpEntity;
import com.mo9.raptor.entity.UserCertifyInfoEntity;
import com.mo9.raptor.entity.UserContactsEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.repository.UserRepository;
import com.mo9.raptor.risk.black.BlackExecute;
import com.mo9.raptor.risk.entity.TRiskCallLog;
import com.mo9.raptor.risk.entity.TRiskContractInfo;
import com.mo9.raptor.risk.repo.RiskCallLogRepository;
import com.mo9.raptor.risk.repo.RiskContractInfoRepository;
import com.mo9.raptor.risk.service.LinkFaceService;
import com.mo9.raptor.risk.service.RiskAuditService;
import com.mo9.raptor.risk.service.RiskRuleEngineService;
import com.mo9.raptor.risk.service.RiskWordService;
import com.mo9.raptor.riskdb.repo.RiskThirdBlackListRepository;
import com.mo9.raptor.service.*;
import com.mo9.raptor.utils.IdCardUtils;
import com.mo9.raptor.utils.IpUtils;
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
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

/**
 * @author yngong
 */
@Service("riskAuditService")
public class RiskAuditServiceImpl implements RiskAuditService {

    private class AuditTask {
        private Function<String, AuditResponseEvent> callFunc;
        private String ruleName;
        private boolean whiteListUserSkip;

        public AuditTask(Function<String, AuditResponseEvent> callFunc, String ruleName, boolean whiteListUserSkip) {
            this.callFunc = callFunc;
            this.ruleName = ruleName;
            this.whiteListUserSkip = whiteListUserSkip;
        }

        public Function<String, AuditResponseEvent> getCallFunc() {
            return callFunc;
        }

        public void setCallFunc(Function<String, AuditResponseEvent> callFunc) {
            this.callFunc = callFunc;
        }

        public String getRuleName() {
            return ruleName;
        }

        public void setRuleName(String ruleName) {
            this.ruleName = ruleName;
        }

        public boolean isWhiteListUserSkip() {
            return whiteListUserSkip;
        }

        public void setWhiteListUserSkip(boolean whiteListUserSkip) {
            this.whiteListUserSkip = whiteListUserSkip;
        }
    }

    @Value("${raptor.sockpuppet}")
    private String sockpuppet;

    @Value("${raptor.oss.read-endpoint}")
    private String readEndpoint;

    @Value("${raptor.oss.catalog.callLogReport}")
    private String secondDomain;

    @Value("${risk.calllog.limit}")
    private int callLogLimit;

    @Value("${risk.score.url}")
    private String scoreUrl;

    @Value("${risk.score}")
    private Double riskScoreLimit;

    @Resource
    private UserContactsService userContactsService;

    private static final Logger logger = LoggerFactory.getLogger(RiskAuditServiceImpl.class);

    @Resource
    private RiskCallLogRepository riskCallLogRepository;

    @Resource
    private RiskThirdBlackListRepository riskThirdBlackListRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private LinkFaceService linkFaceService;

    @Resource
    private UserCertifyInfoService userCertifyInfoService;

    @Resource
    private UserService userService;

    @Resource
    private RuleLogService ruleLogService;

    @Resource
    private RiskWordService riskWordService;

    @Resource
    private RiskRuleEngineService riskRuleEngineService;

    @Resource
    private RiskScoreService riskScoreService;

    @Resource
    private RiskContractInfoRepository riskContractInfoRepository;

    @Resource
    private IpService ipService;

    @Resource
    private ShixinService shixinService;

    private static final String WHITE_LIST = "WHITE";

    private static final String ORIGN_CALL = "%主叫%";

    private static final  List<String> ipLimit = Arrays.asList("广西南宁", "福建莆田", "山东潍坊", "甘肃酒泉", "广东汕尾");

    private Double score(String userCode, String mobile) throws IOException {
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            String jsonResult = okHttpClient.newCall(new Request.Builder().url(scoreUrl + "?mobile=" + mobile).build()).execute().body().string();
            JSONObject jsonObject = JSONObject.parseObject(jsonResult);
            if (jsonObject.getInteger("status") != 1) {
                riskScoreService.create(userCode, mobile, null, jsonResult);
                return null;
            } else {
                Double d = jsonObject.getDouble("score");
                riskScoreService.create(userCode, mobile, d, jsonResult);
                return d;
            }
        } catch (Exception e) {
            riskScoreService.create(userCode, mobile, -1d, "");
            throw e;
        }
    }

    /**
     * 审核方法
     *
     * @param userCode
     * @return
     */
    @Override
    public AuditResponseEvent audit(final String userCode) {
        UserEntity user = userService.findByUserCodeAndDeleted(userCode, false);
        //没收到通话记录则先跳过
        if (!user.getReceiveCallHistory()) {
            return null;
        }
//
//        if (1 == 1) {
//            return new AuditResponseEvent(userCode, "", AuditResultEnum.MANUAL);
//        }

        AuditResponseEvent finalResult = null;
        AuditResponseEvent res = null;
        ArrayList<AuditTask> taskList = new ArrayList<>();
        taskList.add(new AuditTask((u) -> callLogRule(u), "CallLogRule", false));


        taskList.add(new AuditTask((u) -> ipCheckRule(u), "IpCheckRule", true));
        taskList.add(new AuditTask((u) -> shixinCheckRule(u), "ShixinCheckRule", true));
        taskList.add(new AuditTask((u) -> chaseDebtRule(u), "ChaseDebtRule", true));
        taskList.add(new AuditTask((u) -> blackListRule(u), "BlackListRule", true));
        taskList.add(new AuditTask((u) -> riskWordRule(u), "RiskWordRule", true));
        taskList.add(new AuditTask((u) -> riskRuleEngineService.mergencyCallTimesRule(u), "MergencyCallTimesRule", true));
        taskList.add(new AuditTask((u) -> riskRuleEngineService.mergencyHadNoDoneOrderRule(u), "MergencyHadNoDoneOrderRule", true));
        taskList.add(new AuditTask((u) -> riskRuleEngineService.calledTimesByOneLoanCompanyRule(u), "CalledTimesByOneLoanCompanyRule", true));
        taskList.add(new AuditTask((u) -> riskRuleEngineService.calledTimesByDifferentLoanCompanyRule(u), "CalledTimesByDifferentLoanCompanyRule", true));
        taskList.add(new AuditTask((u) -> riskRuleEngineService.mergencyInJHJJBlackListRule(u), "MergencyInJHJJBlackListRule", true));
        taskList.add(new AuditTask((u) -> riskRuleEngineService.openDateRule(u), "OpenDateRule", true));
        taskList.add(new AuditTask((u) -> idCardRule(u), "IdCardRule", true));
        taskList.add(new AuditTask((u) -> ageRule(u), "AgeRule", true));
        taskList.add(new AuditTask((u) -> contactsRule(u), "ContactsRule", true));
//        taskList.add(new AuditTask((u) -> callLogRule(u), "CallLogRule", false));
        taskList.add(new AuditTask((u) -> threeElementCheck(u), "ThreeElementCheck", false));
        taskList.add(new AuditTask((u) -> antiHackRule(u), "AntiHackRule", false));
        taskList.add(new AuditTask((u) -> livePicCompareRule(u), "LivePicCompareRule", false));
        taskList.add(new AuditTask((u) -> idPicCompareRule(u), "IdPicCompareRule", false));
        taskList.add(new AuditTask((u) -> idPicCompareRule(u), "IdPicCompareRule", false));
        /**调用第三方黑名单检查 */
        taskList.add(new AuditTask((u) -> blaceExecute(u), "BlaceExecute", true));

        boolean isWhiteListUser = WHITE_LIST.equals(user.getSource());

        for (AuditTask auditTask : taskList) {
            if (auditTask.whiteListUserSkip && isWhiteListUser) {
                ruleLogService.create(userCode, auditTask.ruleName, null, false, "");
            } else {
                if (finalResult == null) {
                    logger.info(userCode + "开始运行规则[" + auditTask.ruleName + "]");
                    res = auditTask.callFunc.apply(userCode);
                    ruleLogService.create(userCode, auditTask.ruleName, res.isPass(), true, res.getExplanation());
                    if (!res.isPass()) {
                        finalResult = res;
                    }
                } else {
                    ruleLogService.create(userCode, auditTask.ruleName, null, false, "");
                }
            }
        }

        if (finalResult == null) {
            finalResult = res;
        }

        if (!finalResult.isPass()) {
            return new AuditResponseEvent(userCode, finalResult.getExplanation(), AuditResultEnum.REJECTED);
        } else {
            try {
                Double score = score(userCode, user.getMobile());
                if (score == null) {
                    return new AuditResponseEvent(userCode, "评分出错", AuditResultEnum.MANUAL);
                }
                if (score >= riskScoreLimit) {
                    return new AuditResponseEvent(userCode, "通过", AuditResultEnum.PASS);
                } else {
                    return new AuditResponseEvent(userCode, "评分过低[" + score + "]", AuditResultEnum.MANUAL);
                }
            } catch (Exception e) {
                logger.error(userCode + "[" + user.getMobile() + "]评分出错", e);
                return new AuditResponseEvent(userCode, "评分出错", AuditResultEnum.MANUAL);
            }
        }
    }

    private static final int HTTP_OK = 200;
    private static final int ERROR_SCORE_CODE = -1;

    /**
     * 第三方黑名单检查
     * @param userCode
     * @return
     */
    private AuditResponseEvent blaceExecute(String userCode) {
        UserEntity user = userService.findByUserCode(userCode);
        if(user == null){
            logger.warn("用户不存在,userCode={}", userCode);
            return new AuditResponseEvent(userCode, false, "用户不存在");
        }
        try{
            AuditResponseEvent execute = new BlackExecute(user).execute();
            logger.info("用户第三方黑名单检查, userCode={},返回结果isPass={}, res={}", userCode, execute.isPass(), execute.getExplanation());
            return execute;
        }catch (Exception e){
            return new AuditResponseEvent(user.getUserCode(), true, "");
        }
    }

    /**
     * ip范围检查
     * @param userCode
     * @return
     */
    private AuditResponseEvent ipCheckRule(String userCode) {
        UserEntity user = userService.findByUserCode(userCode);
        if(user == null || StringUtils.isBlank(user.getUserIp())){
            logger.warn("用户不存在，或ip不存在，userCode={}", userCode);
            return new AuditResponseEvent(userCode, false, "用户不存在，或ip不存在");
        }
        List<IpEntity> list=  ipService.findByIpNum(IpUtils.ipToLong(user.getUserIp()));
        if(list == null){
            return new AuditResponseEvent(userCode, true, "");
        }
        IpEntity ipEntity = list.get(0);
        String province = ipEntity.getProvince();
        String city = ipEntity.getCity();
        boolean contains = ipLimit.contains(province + city);
        if(contains){
            logger.warn("用户包含在不允许ip中，userCode={},ip={}", userCode);
            return new AuditResponseEvent(userCode, false, "用户包含在不允许ip中");
        }
        return new AuditResponseEvent(userCode, true, "");
    }

    /**
     * 失信记录检查
     * @param userCode
     * @return
     */
    private AuditResponseEvent shixinCheckRule(String userCode) {
        UserEntity user = userService.findByUserCode(userCode);
        String idCard = user.getIdCard();
        String realName = user.getRealName();
        if(user == null || StringUtils.isBlank(idCard) || StringUtils.isBlank(realName)){
            logger.warn("用户不存在，或身份证姓名不存在，userCode={}", userCode);
            return new AuditResponseEvent(userCode, false, "用户不存在，或身份证姓名不存在");
        }
        if(idCard.length() == 18){
            StringBuffer buffer = new StringBuffer();
            int length = idCard.length();
            String cardStart = idCard.substring(0, length - 7);
            String cardEnd = idCard.substring(cardStart.length() + 3, length);
            idCard = buffer.append(cardStart).append("****").append(cardEnd).toString();
        }
        long count = shixinService.findByCardNumAndIname(idCard, realName);
        if(count > 0){
            logger.warn("用户存在失信列表中，userCode={}", userCode);
            return new AuditResponseEvent(userCode, false, "用户存在失信列表中");
        }
        return new AuditResponseEvent(userCode, true, "");
    }

    AuditResponseEvent chaseDebtRule(String userCode) {
        return new AuditResponseEvent(userCode, true, "");
/*
        UserEntity user = userService.findByUserCode(userCode);
        try {
            if (user != null && StringUtils.isNotBlank(user.getMobile())) {
                String url = readEndpoint + "/" + secondDomain + "/" + sockpuppet + "-" + user.getMobile() + "-report.json";
                logger.info("读取报告" + url);
                OkHttpClient okHttpClient = new OkHttpClient();
                Response response = okHttpClient.newCall(new Request.Builder().get().url(url).build()).execute();
                if (response.code() == HTTP_OK) {
                    String json = response.body().string();
                    JSONObject jsonObject = JSON.parseObject(json);
                    Integer status = jsonObject.getInteger("status");
                    if (status == 0) {
                        if (jsonObject.containsKey("data")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            if (data.containsKey("cuishou_risk_detection")) {
                                JSONObject cuishouRiskDetection = data.getJSONObject("cuishou_risk_detection");
                                if (cuishouRiskDetection.containsKey("detection_result") && cuishouRiskDetection.containsKey("yisicuishou")) {
                                    Integer detectionResult = cuishouRiskDetection.getInteger("detection_result");
                                    Integer cuishou30dayTimes = cuishouRiskDetection.getJSONObject("yisicuishou").getInteger("30day_times");
                                    if ((cuishou30dayTimes > 0) && (detectionResult == 2 || detectionResult == 3)) {
                                        return new AuditResponseEvent(userCode, false, "30天内有催收电话并且风险检测催收大于等于中度(" + detectionResult + ")");
                                    } else {
                                        return new AuditResponseEvent(userCode, true, "");
                                    }
                                }
                            }
                        }
                    }
                    logger.info(userCode + "催收字段不存在");
                    return new AuditResponseEvent(userCode, true, "");

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
*/
    }

    AuditResponseEvent blackListRule(String userCode) {
        UserEntity user = userService.findByUserCodeAndDeleted(userCode, false);
        String idCard = user.getIdCard();
        String mobile = user.getMobile();
        Boolean isIn = userRepository.inBlackList(idCard) > 0 || userRepository.inBlackList(mobile) > 0 ||
                riskThirdBlackListRepository.isInBlackList(idCard) > 0 || riskThirdBlackListRepository.isInBlackList(mobile) > 0;
        return new AuditResponseEvent(userCode, !isIn, isIn ? "用户手机号或者身份证在黑名单中" : "");
    }

    AuditResponseEvent riskWordRule(String userCode) {
        int limit = 15;
        UserContactsEntity userContacts = userContactsService.getByUserCode(userCode);
        String json = userContacts.getContactsList();
        JSONArray jsonArray;
        //有2种JSON格式...
        if (json.startsWith("{")) {
            JSONObject jsonObject = JSON.parseObject(json);
            if (!jsonObject.containsKey("contact")) {
                return new AuditResponseEvent(userCode, false, "没有通讯录");
            }
            jsonArray = jsonObject.getJSONArray("contact");
        } else {
            jsonArray = JSON.parseArray(json);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < jsonArray.size(); i++) {
            String name = jsonArray.getJSONObject(i).getString("contact_name");
            stringBuilder.append(name + "|");
        }
        int hitCount = riskWordService.filter(stringBuilder.toString());
        return new AuditResponseEvent(userCode, hitCount <= limit, !(hitCount <= limit) ? "通信录风险词大于15" : "");

    }

    AuditResponseEvent ageRule(String userCode) {
        UserEntity user = userService.findByUserCode(userCode);
        String idCard = user.getIdCard();
        if (idCard == null) {
            return new AuditResponseEvent(userCode, false, "获取身份证失败！");
        }
        if (idCard.length() == 15) {
            idCard = IdCardUtils.conver15CardTo18(idCard);
        }
        int age = IdCardUtils.getAgeByIdCard(idCard);
        boolean pass = age >= 18 && age <= 45;
        return new AuditResponseEvent(userCode, pass, !pass ? "年龄大于45或者小于18" : "");

    }

    AuditResponseEvent idCardRule(String userCode) {
        UserEntity user = userService.findByUserCode(userCode);
        String idCard = user.getIdCard();
        if (idCard == null) {
            return new AuditResponseEvent(userCode, false, "获取身份证失败！");
        }
        if (idCard.length() == 15) {
            idCard = IdCardUtils.conver15CardTo18(idCard);
        }
        try {
            String province = IdCardUtils.getProvinceByIdCard(idCard);
            if ("新疆".equals(province) || "西藏".equals(province) || (province != null && province.contains("内蒙"))) {
                return new AuditResponseEvent(userCode, false, "西藏-新疆-年龄规则");
            }
            return new AuditResponseEvent(userCode, true, "");
        } catch (Exception e) {
            return new AuditResponseEvent(userCode, false, "身份证非法");
        }
    }


    /**
     * 通讯录规则【通讯录数量大于15条小于1000条，180天内通讯录中通话3次及以上】
     *
     * @param userCode
     * @return
     */
    AuditResponseEvent contactsRule(String userCode) {
        int contactsLimit = 15;
        int contactsLimitUpper = 1000;
        int orignCallLimit = 3;

        Long days180ts = 180 * 24 * 60 * 60 * 1000L;
        long currentTimeMillis = System.currentTimeMillis();
        UserEntity user = userService.findByUserCode(userCode);
        UserContactsEntity userContacts = userContactsService.getByUserCode(userCode);
        String json = userContacts.getContactsList();
        try {
            JSONArray jsonArray;
            //有2种JSON格式...
            if (json.startsWith("{")) {
                jsonArray = JSON.parseObject(json).getJSONArray("contact");
            } else {
                jsonArray = JSON.parseArray(json);
            }
            //通讯录电话HASHSET
            HashSet<String> allMobileSet = new HashSet<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                String mobile = MobileUtil.processMobile(jsonArray.getJSONObject(i).getString("contact_mobile"));
                String name = jsonArray.getJSONObject(i).getString("contact_name");
                if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(mobile) && mobile.length() >= 11) {
                    allMobileSet.add(mobile);
                }
            }
            if (allMobileSet.size() < contactsLimit) {
                return new AuditResponseEvent(userCode, false, "通讯录数量小于15个");
            }

            if (allMobileSet.size() > contactsLimitUpper) {
                return new AuditResponseEvent(userCode, false, "通讯录数量大于1000个");
            }

            int count = 0;
            HashSet<String> inListMobiles = new HashSet<>();
            //MYCAI限制1000条 所以这边有个分页
            List<TRiskCallLog> allCallLog = new ArrayList<>();
            Long lastId = 0L;
            while (true) {
                List<TRiskCallLog> list = riskCallLogRepository.getCallLogByMobileAfterTimestamp(user.getMobile(), (currentTimeMillis - days180ts) / 1000, lastId);
                allCallLog.addAll(list);
                if (list.size() == 0) {
                    break;
                } else {
                    lastId = list.get(list.size() - 1).getId();
                }
            }
            logger.info(user.getMobile() + "拉取到数据" + allCallLog.size());
            for (TRiskCallLog tRiskCallLog : allCallLog) {
                // 在通讯录内
                if (allMobileSet.contains(tRiskCallLog.getCallTel())) {
                    count++;
                    inListMobiles.add(tRiskCallLog.getCallTel());
                }
            }
            logger.info("开始进行通讯录表的匹配修改，需要更改的数据条数mobile={},num={}", user.getMobile(), inListMobiles == null ? 0 : inListMobiles.size());
            /** 匹配通讯录表，修改标识,注意用mobile修改，不要用usercode*/
            if(inListMobiles.size() > 0){
                List<String> list = new ArrayList<>(inListMobiles);
                riskContractInfoRepository.updateMatchingMobile(user.getMobile(), list);
            }
            StringBuilder stringBuilder = new StringBuilder(userCode + "," + user.getMobile() + "在主叫列表里[");
            for (String inListMobile : inListMobiles) {
                stringBuilder.append(inListMobile + ",");
            }
            stringBuilder.append("]");
            logger.info(stringBuilder.toString());
            return new AuditResponseEvent(userCode, count >= orignCallLimit, count >= orignCallLimit ? "" : "180天与通讯录通话号码小于3次(共" + count + "次)");
        } catch (Exception e) {
            logger.error(userCode + "解析联系人出错", e);
            logger.info(userCode + json);
            return new AuditResponseEvent(userCode, false, "解析联系人出错！");
        }
    }

    /**
     * 身份证照片与公安照比对
     *
     * @param userCode
     * @return
     */
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

    /**
     * 活体照与公安照片对比
     *
     * @param userCode
     * @return
     */
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

    /**
     * 照片防HACK
     *
     * @param userCode
     * @return
     */
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

    /**
     * 通话记录规则
     * 1：用户最近180天通话记录少于100条，拒绝
     * 2：最近一个月通话时长小于30min，拒绝
     * 3：最近一个月通话次数小于10次，拒绝
     * 4：最近一个月接听次数大于600次，拒绝
     * 5：最近一个月呼出次数大于600次，拒绝
     * 6：近三个月互通号码小于8个，拒绝
     * 7：通话记录前10和通讯录（去重）匹配数小于3个，拒绝
     * @param userCode
     * @return
     */
    private int limitCallLong = 30 * 60;//最近一个月通话时长
    private int limitCallCounts = 10;//最近一个月通话次数
    private int limitZjCounts = 600;//最近一个月呼出次数
    private int limitBjCounts = 600;//最近一个月接听次数
    private int limitCallEachCounts = 8;//近三个月互通号码个数,主叫被叫都要存在
    private int limitContactMatchCounts = 3;//通话记录前10和通讯录（去重）匹配数，按照通话时长排序前十

    AuditResponseEvent callLogRule(String userCode) {
        Long days180ts = 180 * 24 * 60 * 60 * 1000L;
        Long days30ts = 30 * 24 * 60 * 60 * 1000L;
        long currentTimeMillis = System.currentTimeMillis();
        UserEntity user = userService.findByUserCode(userCode);

        int count = riskCallLogRepository.getCallLogCountAfterTimestamp(user.getMobile(), (currentTimeMillis - days180ts) / 1000);
        if(count < callLogLimit){
            logger.warn("通话记录规则-->查询最近3个月通话记录少于180条,mobile={}", user.getMobile());
            return new AuditResponseEvent(userCode, false, "最近3个月通话记录少于180条");
        }
        //查询近一个月所有通话记录
        List<TRiskCallLog> list = riskCallLogRepository.getCallLogByMobileAfterTimestamp(user.getMobile(), (currentTimeMillis - days30ts) / 1000);
        if(list == null || list.size() < 0){
            logger.warn("通话记录规则-->查询最近一个月通话记录为空,mobile={}", user.getMobile());
            return new AuditResponseEvent(userCode, false, "最近一个月通话记录不存在");
        }
        int callLong = 0;
        int callCounts = 0;
        int zjCounts = 0;
        int bjCounts = 0;
        for(TRiskCallLog callLog : list){
            String callDuration = callLog.getCallDuration();
            if(StringUtils.isNotBlank(callDuration)){
                callLong = callLong + Integer.valueOf(callDuration);
            }
            String callMethod = callLog.getCallMethod();
            if("被叫".equals(callMethod)){
                bjCounts = bjCounts + 1;
            }
            if("主叫".equals(callMethod)){
                zjCounts = zjCounts + 1;
            }
        }
        callCounts = list.size();
        if(callLong < limitCallLong){
            logger.warn("通话记录规则-->查询最近一个月通话时长小于30min,mobile={},callLong={}", user.getMobile(), callLong);
            return new AuditResponseEvent(userCode, false, "最近一个月通话时长小于30min");
        }
        if(callCounts < limitCallCounts){
            logger.warn("通话记录规则-->查询最近一个月通话时长小于10次,mobile={},callCounts={}", user.getMobile(), callCounts);
            return new AuditResponseEvent(userCode, false, "最近一个月通话时长小于10次");
        }
        if(zjCounts > limitZjCounts){
            logger.warn("通话记录规则-->查询最近一个月接听次数大于600次,mobile={},zjCounts={}", user.getMobile(), zjCounts);
            return new AuditResponseEvent(userCode, false, "最近一个月接听次数大于600次");
        }
        if(bjCounts > limitBjCounts){
            logger.warn("通话记录规则-->查询最近一个月被叫次数大于600次,mobile={},bjCounts={}", user.getMobile(), bjCounts);
            return new AuditResponseEvent(userCode, false, "最近一个月被叫次数大于600次");
        }

        //查询最近三个月通话号码个数，主叫被叫都要存在,是否少于8个
        List<Object[]> days180List = riskCallLogRepository.getDistinctCallLogByMobileAfterTimestamp(user.getMobile(), (currentTimeMillis - days180ts) / 1000);
        Map<String, Integer> map = new HashMap<>();
        int callEachCounts = 0;
        if(days180List == null || days180List.size() == 0){
            logger.warn("通话记录规则-->查询最近三个月通话记录为空,mobile={}", user.getMobile());
            return new AuditResponseEvent(userCode, false, "最近三个月通话记录不存在");
        }
        for(Object[] arr : days180List){
            String callTel = (String) arr[0];
            if(map.get(callTel) == null){
                map.put(callTel, 1);
            }else{
                map.put(callTel, map.get(callTel) + 1);
                callEachCounts += 1;
            }
        }
        if(callEachCounts < limitCallEachCounts){
            logger.warn("通话记录规则-->近三个月互通号码小于8个,mobile={},callEachCounts={}", user.getMobile(), callEachCounts);
            return new AuditResponseEvent(userCode, false, "近三个月互通号码小于8个");
        }
        //查询最近10个通话记录号码，和通讯录匹配比对
        List<String> callDurationList  = riskCallLogRepository.getDistinctCallLogBySumCallDuration(user.getMobile(), 10);
        if(callDurationList == null || callDurationList.size() < limitContactMatchCounts){
            logger.warn("通话记录规则-->最近通话记录不足3个,mobile={},contactMatchCounts={}", user.getMobile(), callDurationList.size());
            return new AuditResponseEvent(userCode, false, "最近通话记录不足3个");
        }
        List<String> contractInfoList = riskContractInfoRepository.findDistinctByContractMobilesList(user.getMobile(), callDurationList);
        if(contractInfoList == null || contractInfoList.size() < limitContactMatchCounts){
            logger.warn("通话记录规则-->最近前10通话记录和通讯录匹配少于3个,mobile={},contactMatchCounts={}", user.getMobile(), contractInfoList.size());
            return new AuditResponseEvent(userCode, false, "最近通话记录不足3个");
        }
        logger.info("通话记录规则-->通话记录匹配通过,mobile={}", user.getMobile());
        return new AuditResponseEvent(userCode, true, "");
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
                    String reportIdcard = null;
                    if (status == 0) {
                        if (jsonObject.containsKey("data")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            if (data.containsKey("user_info")) {
                                JSONObject userInfo = data.getJSONObject("user_info");
                                if (userInfo.containsKey("conclusion_of_3_elements_check")) {
                                    checkResult = userInfo.getInteger("conclusion_of_3_elements_check");
                                }
                                if (userInfo.containsKey("user_idcard")) {
                                    reportIdcard = userInfo.getString("user_idcard");
                                }
                            }
                        }
                    }
                    if (checkResult == null) {
                        return new AuditResponseEvent(userCode, false, userCode + "[" + user.getMobile() + "]报告出错");
                    } else {
                        //1代表匹配
                        if (checkResult == 1) {
                            if (user.getIdCard() == null || reportIdcard == null || !user.getIdCard().toLowerCase().equals(reportIdcard.toLowerCase())) {
                                return new AuditResponseEvent(userCode, false, "用户填的身份证号码与报告匹配不上");
                            }
                        }
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
