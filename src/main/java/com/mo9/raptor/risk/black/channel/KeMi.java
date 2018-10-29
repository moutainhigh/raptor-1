package com.mo9.raptor.risk.black.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;
import com.mo9.raptor.entity.ThridBlackEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.risk.black.BlackChannel;
import com.mo9.raptor.service.ThridBlackService;
import com.mo9.raptor.utils.MyApplicationContextUtil;
import okhttp3.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by jyou on 2018/10/25.
 *
 * @author jyou
 */
public class KeMi implements BlackChannel{

    private static Logger logger = LoggerFactory.getLogger(KeMi.class);

    private String appKey = "mo9";

    private String signature = "82b4600ba165465ab34c2d34795c0355";

    private String url = "https://wcapi.fin-market.com.cn/kemi/api/black-list";

    @Override
    public AuditResponseEvent doBlackCheck(UserEntity userEntity) {

        try {
            JSONObject json = new JSONObject();
            json.put("appKey", appKey);
            json.put("signature", signature);
            json.put("idCardName", userEntity.getRealName());
            json.put("idCardNo", userEntity.getIdCard());
            json.put("phoneNo", userEntity.getMobile());
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(mediaType, json.toJSONString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            if(response.code() != HTTP_OK){
                saveResult(null, success, userEntity);
                return new AuditResponseEvent(userEntity.getUserCode(), true, "");
            }
            String str = response.body().string();
            if(StringUtils.isBlank(str)){
                saveResult(null, success, userEntity);
                return new AuditResponseEvent(userEntity.getUserCode(), true, "");
            }
            JSONObject jsonObject = (JSONObject) JSON.parse(str);
            String code = jsonObject.getString("code");
            if(code == null || !code.equals("0")){
                logger.info("调用kemi返回code={}，直接通过,userCode={}", code, userEntity.getUserCode());
                saveResult(str, success, userEntity);
                return new AuditResponseEvent(userEntity.getUserCode(), true, "");
            }
            JSONObject result = jsonObject.getJSONObject("result");
            String isBan = result.getString("isBan");
            if(isBan.equals("0")){
                logger.info("调用kemi未命中黑名单，直接通过,userCode={}", userEntity.getUserCode());
                saveResult(str, success, userEntity);
                return new AuditResponseEvent(userEntity.getUserCode(), true, "");
            }
            JSONArray reports = result.getJSONArray("reports");
            if(reports != null || reports.size() > 0){
                for(Object o : reports){
                    JSONObject obj = (JSONObject) o;
                    String currentDueTime = obj.getString("currentDueTime");
                    if(StringUtils.isNotBlank(currentDueTime) && currentDueTime.trim() != null){
                        logger.info("调用kemi命中黑名单，拒绝,userCode={},currentDueTime={}", userEntity.getUserCode(), currentDueTime);
                        saveResult(str, failed, userEntity);
                        return new AuditResponseEvent(userEntity.getUserCode(), false, "用户历史逾期不符合要求，直接拒绝");
                    }
                }
            }
            saveResult(str, success, userEntity);
        } catch (IOException e) {
            logger.error("调用kemi出现异常，规则直接通过userCode={}", userEntity.getUserCode(), e);
            saveResult(null, success, userEntity);
        }
        return new AuditResponseEvent(userEntity.getUserCode(), true, "");
    }

    @Override
    public boolean channelIsOpen() {
        return isOpen;
    }


    private void saveResult(String thridRes, String result, UserEntity userEntity){
        ThridBlackService thridBlackService = MyApplicationContextUtil.getContext().getBean(ThridBlackService.class);
        ThridBlackEntity thridBlack = new ThridBlackEntity();
        thridBlack.setCreateTime(System.currentTimeMillis());
        thridBlack.setUserCode(userEntity.getUserCode());
        thridBlack.setChannel("KeMi");
        thridBlack.setThridRes(thridRes);
        thridBlack.setResult(result);
        thridBlackService.save(thridBlack);
    }
}
