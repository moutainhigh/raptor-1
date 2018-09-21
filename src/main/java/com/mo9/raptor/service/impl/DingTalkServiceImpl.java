package com.mo9.raptor.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.service.DingTalkService;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.httpclient.bean.HttpResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by ycheng on 2018/7/6.
 *
 * @author ycheng
 */
@Service(value = "dingTalkService")
public class DingTalkServiceImpl implements DingTalkService {

    @Value(value = "${suona.dingtalk.notice.url}")
    private String dingTalkNoticeUrl;

    @Value(value = "${suona.dingtalk.notice.send}")
    private boolean dingTalkNoticeSend;

    @Value(value = "${suona.dingtalk.notice.hook}")
    private String dingTalkNoticeHook;

    @Value(value = "${raptor.environment}")
    private String environment;

    @Autowired
    private HttpClientApi httpClientApi;

    /**
     * 发送钉钉
     *
     * @param title
     * @param message
     * @return
     */
    @Override
    public void sendNotice(String title, String message) {

        if (!dingTalkNoticeSend) {
            return;
        }

        JSONObject params = new JSONObject();
        JSONArray target = new JSONArray();

        String content =  title  + message;

        params.put("mediaType", "link");
        params.put("hook", dingTalkNoticeHook);
        params.put("message", content);
        params.put("title", "【" + environment + "】" + title);
        params.put("target", target);

        try {
            HttpResult result = httpClientApi.doPostJson(dingTalkNoticeUrl, String.valueOf(params));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
