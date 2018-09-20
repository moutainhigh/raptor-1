package com.mo9.raptor.mq.producer;

import com.mo9.raptor.entity.RabbitProducerMqEntity;
import com.mo9.raptor.mq.config.RabbitConfig;
import com.mo9.raptor.service.RabbitProducerMqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

/**
 * @author xtgu
 * @date :   2018-07-10 14:30
 */
//@Component
public class RabbitProducer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitProducer.class);

//    @Resource
    private RabbitConfig rabbitConfig;

//    @Resource
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitProducerMqService rabbitProducerMqService;

    /**
     * 放款tag
     */
    @Value("${raptor.mq.tag.loan}")
    private String loanKey;

    /**
     * 还款tag
     */
    @Value("${raptor.mq.tag.repay}")
    private String repayKey;

    /**
     * 用户tag
     */
    @Value("${raptor.mq.tag.user}")
    private String userKey;

    /**
     * 放款mq
     * @param messageKey -- 唯一号
     * @param messageStr -- 消息体
     */
    public void sendMessageLoan( String messageKey, String messageStr) {
        try {
            //创建本地数据
            rabbitProducerMqService.createProducerMq( loanKey ,messageKey , messageStr ,  RabbitProducerMqEntity.ProducerMqEntityStatus.START);
            //发送mq
            sendMessageInside( loanKey , messageKey , messageStr );
        } catch (Exception e) {
            logger.error("放款mq发送失败" + messageKey + "消息体 " + messageStr , e);
            rabbitProducerMqService.createProducerMq( loanKey ,messageKey , messageStr , RabbitProducerMqEntity.ProducerMqEntityStatus.FAILED);
        }
    }

    /**
     * 还款mq
     * @param messageKey -- 唯一号
     * @param messageStr -- 消息体
     */
    public void sendMessageRepay( String messageKey, String messageStr) {
        try {
            //创建本地数据
            rabbitProducerMqService.createProducerMq( repayKey ,messageKey , messageStr ,  RabbitProducerMqEntity.ProducerMqEntityStatus.START);
            //发送mq
            sendMessageInside( repayKey , messageKey , messageStr );
        } catch (Exception e) {
            logger.error("还款mq发送失败" + messageKey + "消息体 " + messageStr , e);
            rabbitProducerMqService.createProducerMq( repayKey ,"RECHARGE-CLONE" , messageStr , RabbitProducerMqEntity.ProducerMqEntityStatus.FAILED);
        }
    }

    /**
     * 用户mq
     * @param messageKey -- 唯一号
     * @param messageStr -- 消息体
     */
    public void sendMessageUser( String messageKey, String messageStr) {
        try {
            //创建本地数据
            rabbitProducerMqService.createProducerMq( userKey ,messageKey , messageStr ,  RabbitProducerMqEntity.ProducerMqEntityStatus.START);
            //发送mq
            sendMessageInside( userKey , messageKey , messageStr );
        } catch (Exception e) {
            logger.error("用户mq发送失败" + messageKey + "消息体 " + messageStr , e);
            rabbitProducerMqService.createProducerMq( userKey ,messageKey , messageStr , RabbitProducerMqEntity.ProducerMqEntityStatus.FAILED);
        }
    }

    /**
     * mq补漏传递
     * @param producerMqEntity
     */
    public void sendMessageRemedy( RabbitProducerMqEntity producerMqEntity ) {
        try {
            //发送mq
            sendMessageInside( producerMqEntity.getTag() , producerMqEntity.getMessageKey() , producerMqEntity.getMessage() );
        } catch (Exception e) {
            logger.error("mq补漏发送失败" + producerMqEntity.getMessageKey() + "消息体 " + producerMqEntity.getMessage() , e);
        }
    }

    /**
     * 发送mq
     * @param repayKey
     * @param messageKey
     * @param messageStr
     */
    private void sendMessageInside(String repayKey, String messageKey, String messageStr) throws UnsupportedEncodingException {
        MessageProperties properties = new MessageProperties();
        properties.setMessageId(messageKey);
        Message message = new Message(messageStr.getBytes("UTF-8"), properties);
        CorrelationData correlationData = new CorrelationData(messageKey);
        rabbitTemplate.send(rabbitConfig.getMqExchange(), repayKey , message, correlationData);
    }
}
