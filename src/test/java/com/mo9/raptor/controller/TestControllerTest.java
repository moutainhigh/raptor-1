package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mo9.raptor.bean.BaseResponse;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class TestControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(TestControllerTest.class);

    @Test
    public void add() {
        try {
            String address = "http://riskclone.mo9.com/raptorApi";
//            String address = "http://localhost/raptorApi";
            String orderRear = "/test/push_lent_mq";
            HttpHeaders headers = new HttpHeaders();
            headers.add("Account-Code", "596348FB29F87DEBF950EF0AF1755126");
            headers.add("client-id", "503");
            headers.add("content-type", "application/json; charset=UTF-8");
            headers.add("access-token", "2e3ee5f2d61841339efc901935825b06");
            String sign = "28B21099FBDD85467CC01E7B80146FF0";
            List<String> orderIds = new ArrayList<String>();
            orderIds.add("TTYQ-231075083962159104");
            orderIds.add("TTYQ-234680600181739520");
            orderIds.add("TTYQ-234686043453001728");
            orderIds.add("TTYQ-232290517998772224");
            orderIds.add("TTYQ-235006226461757440");
            orderIds.add("TTYQ-235009452087054336");
            orderIds.add("TTYQ-235010458006982656");
            orderIds.add("TTYQ-235023084904648704");
            orderIds.add("TTYQ-235023658358280192");
            orderIds.add("TTYQ-235026996290260992");
            orderIds.add("TTYQ-235027373043617792");
            orderIds.add("TTYQ-235028037324902400");
            orderIds.add("TTYQ-235030827426254848");
            orderIds.add("TTYQ-235032014305234944");
            orderIds.add("TTYQ-235033123459567616");
            orderIds.add("TTYQ-235033710884425728");
            orderIds.add("TTYQ-235035045520347136");
            orderIds.add("TTYQ-235037452094541824");
            orderIds.add("TTYQ-235037753371398144");
            orderIds.add("TTYQ-235039709410234368");
            orderIds.add("TTYQ-235040228761538560");
            orderIds.add("TTYQ-235041111104688128");
            orderIds.add("TTYQ-235043560905379840");
            orderIds.add("TTYQ-235045371968098304");
            orderIds.add("TTYQ-235046622160097280");
            orderIds.add("TTYQ-235048148547014656");
            orderIds.add("TTYQ-235049075832131584");
            orderIds.add("TTYQ-235049403256279040");
            orderIds.add("TTYQ-235050336816074752");
            orderIds.add("TTYQ-235050575761379328");
            orderIds.add("TTYQ-235051790226296832");
            orderIds.add("TTYQ-235054807839281152");
            orderIds.add("TTYQ-235055233905070080");
            orderIds.add("TTYQ-235057890354925568");
            orderIds.add("TTYQ-235058816599855104");
            orderIds.add("TTYQ-235060176951054336");
            orderIds.add("TTYQ-235060708268707840");



            Map<String, String> params = new HashMap<String, String>();
            params.put("capital", "1000");
            params.put("period", "7");
            HttpEntity<String> requestEntity = new HttpEntity<String>(JSONObject.toJSONString(params), headers);

            for (String orderId : orderIds) {
                String orderUrl = address + orderRear + "?sign=" + sign + "&orderId=" + orderId;
                ResponseEntity<String> result = new RestTemplate().getForEntity(orderUrl, String.class);
                logger.info("返回结果:"+ JSONObject.toJSONString(result.getBody(), SerializerFeature.PrettyFormat));
            }
        } catch (Exception e) {
            logger.error("错误", e);
        }
    }
}