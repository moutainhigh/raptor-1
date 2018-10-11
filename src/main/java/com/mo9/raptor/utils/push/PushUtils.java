package com.mo9.raptor.utils.push;

import com.alibaba.fastjson.JSON;
import com.mo9.raptor.utils.EncryptionUtils;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by jyou on 2018/10/11.
 *
 * @author jyou
 */
@Component
public class PushUtils {

    private Logger logger = LoggerFactory.getLogger(PushUtils.class);

    @Value("${push.rul}")
    private String pushUrl;

    @Resource
    private HttpClientApi httpClientApi;

    /**
     * 单播推送消息
     * @param pushBean
     */
    public void push(PushBean pushBean){
        Map<String, String> params = new HashMap<String, String>();
        params.put("pushNo" , pushBean.getPushNo());
        params.put("ticker" , pushBean.getTicker());
        params.put("title" , pushBean.getTitle());
        params.put("text" , pushBean.getText());
        params.put("channel", pushBean.getChannel());
        params.put("client", pushBean.getClient());

        Map<String, String> headers = new HashMap<>();
        long time = System.currentTimeMillis();
        String accessToken = UUID.randomUUID().toString();
        headers.put("Timestamp", String.valueOf(time));
        headers.put("Access-Token", accessToken);
        String sign = EncryptionUtils.md5Encode(JSON.toJSONString(params) + generateTimestamp(time) + "rtsDDcogZcPCu!NYkfgfjQq6O;~2Brtr" + accessToken);
        headers.put("Sign", sign);

        try {
            httpClientApi.doPostJson(pushUrl + "/push/unicastPushByPushNo", JSON.toJSONString(params), headers);
        } catch (IOException e) {
            logger.error("推送消息出现异常，pushNo={}", pushBean.getPushNo(), e);
        }
    }

    private String generateTimestamp(long timestamp) {
        String sTimestamp = String.valueOf(timestamp);
        return timestamp % 2 == 0 ? sTimestamp.substring(0, 10) : sTimestamp.substring(sTimestamp.length() - 10);
    }
}
