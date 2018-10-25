package com.mo9.raptor.risk.black.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.risk.black.BlackChannel;
import com.mo9.raptor.utils.log.Log;
import okhttp3.*;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by jyou on 2018/10/25.
 *
 * @author jyou
 */
public class KeMi implements BlackChannel{

    private static Logger logger = Log.get();

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
                return new AuditResponseEvent(userEntity.getUserCode(), true, "");
            }
            String str = response.body().string();
            JSONObject jsonObject = (JSONObject) JSON.parse(str);
            String code = jsonObject.getString("code");
            if(code == null || !code.equals("0")){
                logger.info("调用kemi返回code={}，直接通过,userCode={}", code, userEntity.getUserCode());
                return new AuditResponseEvent(userEntity.getUserCode(), true, "");
            }
            JSONObject result = jsonObject.getJSONObject("result");
            String isBan = result.getString("isBan");
            if(isBan.equals("0")){
                logger.info("调用kemi未命中黑名单，直接通过,userCode={}", userEntity.getUserCode());
                return new AuditResponseEvent(userEntity.getUserCode(), true, "");
            }
            JSONArray reports = result.getJSONArray("reports");
            if(reports != null || reports.size() > 0){
                for(Object o : reports){
                    JSONObject obj = (JSONObject) o;
                    String historyDueTime = obj.getString("historyDueTime");
                    if(historyDueTime != null){
                        return new AuditResponseEvent(userEntity.getUserCode(), false, "用户历史逾期不符合要求，直接拒绝");
                    }
                }
            }
        } catch (IOException e) {
            logger.error("调用kemi出现异常，规则直接通过userCode={}", userEntity.getUserCode(), e);
        }
        return new AuditResponseEvent(userEntity.getUserCode(), true, "");
    }

    @Override
    public boolean channelIsOpen() {
        return isOpen;
    }


}
