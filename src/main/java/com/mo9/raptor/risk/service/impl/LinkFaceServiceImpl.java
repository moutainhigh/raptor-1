package com.mo9.raptor.risk.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.entity.LinkfaceLogEntity;
import com.mo9.raptor.service.LinkfaceLogService;
import okhttp3.*;
import okio.BufferedSink;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author yngong
 */
@Service("linkFaceService")
public class LinkFaceServiceImpl implements com.mo9.raptor.risk.service.LinkFaceService {

    private static final Logger logger = LoggerFactory.getLogger(LinkFaceServiceImpl.class);

    @Value("${linkface.apiId}")
    private String apiId;
    @Value("${linkface.apiSecret}")
    private String apiSecret;

    private OkHttpClient okHttpClient = new OkHttpClient();

    private static final int HTTP_OK = 200;
    private static final double SCORE_ERROR_CODE = -1;
    private static final String FIELD_SCORE = "score";
    private static final String FIELD_CONFIDENCE= "confidence";


    @Resource
    private LinkfaceLogService linkfaceLogService;


    /**
     * 防hack接口，验证用户照片是否为活体照片
     *
     * @return
     */
    @Override
    public double preventHack(String userCode, String imageUrl) {
        String callUrl = "https://cloudapi.linkface.cn/hackness/selfie_hack_detect";
        HashMap<String, Object> callParams = new HashMap<>(2);
        callParams.put("callUrl", callUrl);
        callParams.put("file", imageUrl);
        LinkfaceLogEntity linkfaceLogEntity = linkfaceLogService.create(userCode, JSONObject.toJSONString(callParams), "INIT", "");

        try {
            // 先下载图片，调用接口上传图片，获取数据
            Response response = okHttpClient.newCall(new Request.Builder().get().url(imageUrl).build()).execute();
            if (response.code() == HTTP_OK) {

                final byte[] imgByte = response.body().bytes();
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.addFormDataPart("api_id", apiId);
                builder.addFormDataPart("api_secret", apiSecret);
                builder.addFormDataPart("file", "file", new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return MediaType.parse("multipart/form-data");
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {
                        sink.write(imgByte);
                    }
                });


                linkfaceLogEntity.setStatus("SEND");
                linkfaceLogService.update(linkfaceLogEntity);
                // 返回json字符串
                String res = okHttpClient.newCall(new Request.Builder().post(builder.build()).url(callUrl).build()).execute().body().string();
                linkfaceLogEntity.setStatus("RECEIVE");
                linkfaceLogEntity.setCallResult(res);
                linkfaceLogService.update(linkfaceLogEntity);
                //{"request_id":"TID7c205d05fa6b4df099ad9fd1f38d76a2","status":"OK","score":0.5303437113761902,"image_id":"7c6649688d054507b6819f97b9d5d7a4"}
                JSONObject jsonObject = JSONObject.parseObject(res);
                if (jsonObject.containsKey(FIELD_SCORE)) {
                    linkfaceLogEntity.setStatus("FINISH");
                    linkfaceLogService.update(linkfaceLogEntity);
                    return jsonObject.getDoubleValue(FIELD_SCORE);
                } else {
                    linkfaceLogEntity.setStatus("RESULT_NOT_OK");
                    linkfaceLogService.update(linkfaceLogEntity);
                }
            } else {
                linkfaceLogEntity.setStatus("IMAGE_NOT_FOUND");
                linkfaceLogService.update(linkfaceLogEntity);
            }
        } catch (Exception e) {
            logger.error("防hack接口调用出错", e);
            linkfaceLogEntity.setStatus("ERROR");
            linkfaceLogEntity.setRemark(e.getMessage());
            linkfaceLogService.update(linkfaceLogEntity);
        }
        return SCORE_ERROR_CODE;
    }

    /**
     * 活体照与公安库身份证照比对,判断是否为同一个人
     *
     * @param idNumber
     * @param name
     * @return
     */
    @Override
    public double judgeOnePerson(String userCode, String imageUrl, String idNumber, String name) {
        String callUrl = "https://cloudapi.linkface.cn/identity/selfie_idnumber_verification";
        HashMap<String, Object> callParams = new HashMap<>(2);
        callParams.put("callUrl", callUrl);
        callParams.put("name", name);
        callParams.put("id_number", idNumber);
        callParams.put("selfie_auto_rotate", "true");
        callParams.put("selfie_file", imageUrl);
        LinkfaceLogEntity linkfaceLogEntity = linkfaceLogService.create(userCode, JSONObject.toJSONString(callParams), "INIT", "");

        try {
            if (StringUtils.isNotBlank(idNumber) && StringUtils.isNotBlank(name)) {
                Response response = okHttpClient.newCall(new Request.Builder().get().url(imageUrl).build()).execute();
                if (response.code() == HTTP_OK) {
                    final byte[] imgByte = response.body().bytes();
                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.addFormDataPart("api_id", apiId);
                    builder.addFormDataPart("api_secret", apiSecret);
                    builder.addFormDataPart("name", name);
                    builder.addFormDataPart("id_number", idNumber);
                    builder.addFormDataPart("selfie_auto_rotate", "true");
                    builder.addFormDataPart("selfie_file", "selfie_file", new RequestBody() {
                        @Override
                        public MediaType contentType() {
                            return MediaType.parse("multipart/form-data");
                        }

                        @Override
                        public void writeTo(BufferedSink sink) throws IOException {
                            sink.write(imgByte);
                        }
                    });

                    linkfaceLogEntity.setStatus("SEND");
                    linkfaceLogService.update(linkfaceLogEntity);
                    String res = okHttpClient.newCall(new Request.Builder().post(builder.build()).url(callUrl).build()).execute().body().string();
                    linkfaceLogEntity.setStatus("RECEIVE");
                    linkfaceLogEntity.setCallResult(res);
                    linkfaceLogService.update(linkfaceLogEntity);
                    JSONObject jsonObject = JSONObject.parseObject(res);
                    if (jsonObject.containsKey(FIELD_CONFIDENCE)) {
                        linkfaceLogEntity.setStatus("FINISH");
                        linkfaceLogService.update(linkfaceLogEntity);
                        return jsonObject.getDoubleValue(FIELD_CONFIDENCE);
                    } else {
                        linkfaceLogEntity.setStatus("RESULT_NOT_OK");
                        linkfaceLogService.update(linkfaceLogEntity);
                    }
                } else {
                    linkfaceLogEntity.setStatus("IMAGE_NOT_FOUND");
                    linkfaceLogService.update(linkfaceLogEntity);
                }
            }
        } catch (Exception e) {
            logger.error("身份证与活体照片对比接口调用出错", e);
            linkfaceLogEntity.setStatus("ERROR");
            linkfaceLogEntity.setRemark(e.getMessage());
            linkfaceLogService.update(linkfaceLogEntity);
        }
        return SCORE_ERROR_CODE;
    }

    /**
     * 身份证正面与公安照正面对比，判断是否为同一个人
     *
     * @return
     */
    @Override
    public double judgeIdCardPolice(String userCode, String imageUrl, String idNumber, String name) {
        String callUrl = "https://cloudapi.linkface.cn/identity/selfie_idnumber_verification";

        HashMap<String, Object> callParams = new HashMap<>(5);
        callParams.put("callUrl", callUrl);
        callParams.put("name", name);
        callParams.put("id_number", idNumber);
        callParams.put("selfie_auto_rotate", "true");
        callParams.put("selfie_file", imageUrl);
        LinkfaceLogEntity linkfaceLogEntity = linkfaceLogService.create(userCode, JSONObject.toJSONString(callParams), "INIT", "");
        try {
            if (StringUtils.isNotBlank(idNumber) && StringUtils.isNotBlank(name)) {
                Response response = okHttpClient.newCall(new Request.Builder().get().url(imageUrl).build()).execute();
                if (response.code() == HTTP_OK) {
                    final byte[] imgByte = response.body().bytes();
                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.addFormDataPart("api_id", apiId);
                    builder.addFormDataPart("api_secret", apiSecret);
                    builder.addFormDataPart("name", name);
                    builder.addFormDataPart("id_number", idNumber);
                    builder.addFormDataPart("selfie_auto_rotate", "true");
                    builder.addFormDataPart("selfie_file", "selfie_file", new RequestBody() {
                        @Override
                        public MediaType contentType() {
                            return MediaType.parse("multipart/form-data");
                        }

                        @Override
                        public void writeTo(BufferedSink sink) throws IOException {
                            sink.write(imgByte);
                        }
                    });
                    // 返回json字符串
                    linkfaceLogEntity.setStatus("SEND");
                    linkfaceLogService.update(linkfaceLogEntity);
                    String res = okHttpClient.newCall(new Request.Builder().post(builder.build()).url(callUrl).build()).execute().body().string();
                    linkfaceLogEntity.setStatus("RECEIVE");
                    linkfaceLogEntity.setCallResult(res);
                    linkfaceLogService.update(linkfaceLogEntity);
                    JSONObject jsonObject = JSONObject.parseObject(res);
                    if (jsonObject.containsKey(FIELD_CONFIDENCE)) {
                        linkfaceLogEntity.setStatus("FINISH");
                        linkfaceLogService.update(linkfaceLogEntity);
                        return jsonObject.getDoubleValue(FIELD_CONFIDENCE);
                    } else {
                        linkfaceLogEntity.setStatus("RESULT_NOT_OK");
                        linkfaceLogService.update(linkfaceLogEntity);
                    }
                } else {
                    linkfaceLogEntity.setStatus("IMAGE_NOT_FOUND");
                    linkfaceLogService.update(linkfaceLogEntity);
                }
            }
        } catch (Exception e) {
            logger.error("身份证与活体照片对比接口调用出错", e);
            linkfaceLogEntity.setStatus("ERROR");
            linkfaceLogEntity.setRemark(e.getMessage());
            linkfaceLogService.update(linkfaceLogEntity);
        }
        return SCORE_ERROR_CODE;
    }
}
