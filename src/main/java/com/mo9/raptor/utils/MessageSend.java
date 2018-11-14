package com.mo9.raptor.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.vo.MessageNotifyRequestVo;
import com.mo9.raptor.entity.MessageNotifyEntity;
import com.mo9.raptor.enums.AreaCodeEnum;
import com.mo9.raptor.enums.MessageNotifyEventEnum;
import com.mo9.raptor.enums.MessageNotifyModelEnum;
import com.mo9.raptor.enums.MessageNotifyTypeEnum;
import com.mo9.raptor.repository.MessageNotifyRepository;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.httpclient.bean.HttpResult;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * @author zma
 * @date 2018/7/11
 */
@Component("messageSend")
public class MessageSend {

    private static Logger logger = Log.get();

    @Autowired
    private ResourceBundleMessageSource messageSources;

    @Autowired
    private HttpClientApi httpClientApi;

    @Autowired
    private IDWorker idWorker;

    @Autowired
    private MessageNotifyRepository messageNotifyRepository;

    @Value("${suona.send.message.url}")
    private String messageSendUrl;

    @Value("${loan.name.en}")
    private String systemCode;


    public Boolean sendMobileSmsCN(String mobile, MessageNotifyEventEnum event,JSONObject variableValues) {
        return sendMobileSms(mobile,event, AreaCodeEnum.CN,variableValues);
    }

    /**
     * 发送短信
     *
     * @param mobile         手机号
     * @param event          事件名称
     * @param language       地区
     * @param variableValues 模版变量
     * @return
     */
    public Boolean sendMobileSms(String mobile, MessageNotifyEventEnum event, AreaCodeEnum language, JSONObject variableValues) {
        return sendMessage(MessageNotifyTypeEnum.SMS, mobile, event, language, variableValues);
    }

    public Boolean sendMessage(MessageNotifyTypeEnum type, String receiver, MessageNotifyEventEnum event, AreaCodeEnum language, JSONObject variableValues) {
        MessageNotifyRequestVo requestVo = new MessageNotifyRequestVo();
        //添加收件人，不支持同时发送邮件至多个邮箱
        List<String> list = new ArrayList<>();
        list.add(receiver);
        //根据不同的模版事件和语言选择不同的模版
        MessageNotifyModelEnum messageNotifyModelEnum = confirmMessageNotifyModel(type, event, language.getLanguageCode());
        requestVo.setTemplateCode(messageNotifyModelEnum.name());
        requestVo.setReceiverArea(language.name());
        requestVo.setReceivers(list);
        requestVo.setTransMode(type);
        requestVo.setBusinessCode(event);
        requestVo.setVariableValues(variableValues);
        requestVo.setSystemCode(systemCode);
        return sendMessageInside(requestVo);
    }


    /**
     * 发送消息的基础实现方法，内部调用
     *
     * @param requestVo
     * @return 是否发送成功
     */
    private Boolean sendMessageInside(MessageNotifyRequestVo requestVo) {

        //设置消息id
        String messageId = systemCode+"_" + idWorker.nextId();
        requestVo.setMessageId(messageId);
        //设置短信或邮件发送
        //TODO 短信网关待配置
        MessageNotifyTypeEnum transMode = requestVo.getTransMode();
        if (transMode == MessageNotifyTypeEnum.MAIL) {
            requestVo.setGatewayCode("DEFAULT_MAIL");
        }
        if (transMode == MessageNotifyTypeEnum.SMS) {
            requestVo.setGatewayCode("CAPTCHA");
        }
        MessageNotifyEntity messageNotifyEntity = new MessageNotifyEntity();
        Boolean sendRes = false;
        //请求发送消息解析返回参数
        try {
            HttpResult result = httpClientApi.doPostJson(messageSendUrl, JSON.toJSONString(requestVo));
            logger.info("发送信息 id : " + messageId + "返回参数 : " + result.getData());
            JSONObject data = JSON.parseObject(result.getData());
            String code = data.getString("code");
            //发送成功
            if ("0".equals(code) || "11120001".equals(code)) {
                sendRes = true;
                messageNotifyEntity.setStatus(MessageNotifyEntity.MessageSendStatus.SUCCESS.name());
            } else {
                messageNotifyEntity.setStatus(MessageNotifyEntity.MessageSendStatus.FAILED.name());
            }
        } catch (IOException e) {
            messageNotifyEntity.setStatus(MessageNotifyEntity.MessageSendStatus.FAILED.name());
        }
        messageNotifyEntity.setTemplateCode(requestVo.getTemplateCode());
        messageNotifyEntity.setLanguage(requestVo.getReceiverArea());
        messageNotifyEntity.setMessageId(messageId);
        messageNotifyEntity.setType(requestVo.getTransMode().name());
        messageNotifyEntity.setRemark(requestVo.getVariableValues().toJSONString());
        messageNotifyEntity.setReceivers(requestVo.getReceivers().toString());
        messageNotifyEntity.setBusinessCode(requestVo.getBusinessCode().name());
        Long time = System.currentTimeMillis();
        messageNotifyEntity.setCreateTime(time);
        messageNotifyEntity.setUpdateTime(time);
        messageNotifyEntity.setSendTime(time);
        messageNotifyRepository.save(messageNotifyEntity);
        return sendRes;
    }

    /**
     * 根据事件和语言，选择不同模版
     *
     * @param event
     * @param language
     * @retur
     */
    private MessageNotifyModelEnum confirmMessageNotifyModel(MessageNotifyTypeEnum type, MessageNotifyEventEnum event, Locale language) throws NoSuchMessageException {
        String message = null;
        try {
            message = messageSources.getMessage(event.name(), null, language);
            //读取resources中的模版配置
            String[] messageSplit = message.split("/");
            if (messageSplit != null && messageSplit.length > 1) {
                if (MessageNotifyTypeEnum.MAIL == type) {
                    message = messageSplit[0].trim();
                } else {
                    message = messageSplit[1].trim();
                }
            }
            MessageNotifyModelEnum messageNotifyModelEnum = MessageNotifyModelEnum.valueOf(message);
            return messageNotifyModelEnum;
        } catch (IllegalArgumentException e) {
            Log.error( logger , e , "根据事件获取消息模版失败，[MessageNotifyModelEnum]中没有添加此枚举 message={}", message);
        }
        throw new NoSuchMessageException(event.name());
    }

}


