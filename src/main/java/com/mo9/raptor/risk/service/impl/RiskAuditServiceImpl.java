package com.mo9.raptor.risk.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.entity.UserCertifyInfoEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.linkface.LinkFaceService;
import com.mo9.raptor.risk.entity.TRiskCallLog;
import com.mo9.raptor.risk.repo.RiskCallLogRepository;
import com.mo9.raptor.risk.service.RiskAuditService;
import com.mo9.raptor.service.UserCertifyInfoService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.oss.OSSFileUpload;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yngong
 */
@Service("riskAuditService")
public class RiskAuditServiceImpl implements RiskAuditService {

    @Resource
    private RiskCallLogRepository riskCallLogRepository;

    @Resource
    private LinkFaceService linkFaceService;

    @Resource
    private UserCertifyInfoService userCertifyInfoService;

    @Resource
    private UserService userService;

    @Override
    public void audit(String userCode) {
        if (!callLogRule(userCode)) {

        }
    }

    private static final int HTTP_OK = 200;
    private static final int ERROR_SCORE_CODE = -1;

    private boolean idPicCompareRule(String userCode) {
        UserCertifyInfoEntity userCertifyInfo = userCertifyInfoService.findByUserCode(userCode);
        if (userCertifyInfo != null) {
            String url = userCertifyInfo.getAccountFrontImg();
            double score = linkFaceService.judgeOnePerson(userCode, url, userCertifyInfo.getIdCard(), userCertifyInfo.getRealName());
            if (score == ERROR_SCORE_CODE) {
                return false;
            } else {
                return score >= 0.7;
            }
        }
        return false;
    }

    private boolean livePicCompareRule(String userCode) {
        UserCertifyInfoEntity userCertifyInfo = userCertifyInfoService.findByUserCode(userCode);
        if (userCertifyInfo != null) {
            String url = userCertifyInfo.getAccountFrontImg();
            double score = linkFaceService.judgeIdCardPolice(userCode, url, userCertifyInfo.getIdCard(), userCertifyInfo.getRealName());
            if (score == ERROR_SCORE_CODE) {
                return false;
            } else {
                return score >= 0.7;
            }
        }
        return false;
    }


    private boolean antiHackRule(String userCode) {
        UserCertifyInfoEntity userCertifyInfo = userCertifyInfoService.findByUserCode(userCode);
        if (userCertifyInfo != null) {
            String url = userCertifyInfo.getAccountOcr();
            double score = linkFaceService.preventHack(userCode, url);
            if (score == ERROR_SCORE_CODE) {
                return false;
            } else {
                return score >= 0.98;
            }
        }
        return false;
    }

    private boolean callLogRule(String userCode) {
        Long days90ts = 90 * 24 * 60 * 60 * 1000L;
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
        return count >= 100;
    }

    @Resource
    private OSSFileUpload ossFileUpload;


    @Value("${raptor.sockpuppet}")
    private String sockpuppet;

    private boolean phoneRegNameEqualIdcardName(String userCode) {
        UserEntity user = userService.findByUserCode(userCode);
        try {
            if (user != null && user.getMobile() != null) {
                String url = ossFileUpload.buildFileURL(sockpuppet + "-" + user.getMobile() + "-report.json");
                OkHttpClient okHttpClient = new OkHttpClient();
                Response response = okHttpClient.newCall(new Request.Builder().get().url(url).build()).execute();
                if (response.code() == HTTP_OK) {
                    String json = response.body().string();
                    JSONObject jsonObject = JSON.parseObject(json);

                } else {
                    return false;
                }
            }
        } catch (Exception e) {

        }
        return false;
    }
}
