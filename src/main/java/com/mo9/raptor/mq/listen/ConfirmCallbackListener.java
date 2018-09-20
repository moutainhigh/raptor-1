package com.mo9.raptor.mq.listen;

import com.mo9.raptor.entity.RabbitProducerMqEntity;
import com.mo9.raptor.service.RabbitProducerMqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xtgu
 * @date :   2018-07-10 14:38
 */
//@Component
public class ConfirmCallbackListener implements RabbitTemplate.ConfirmCallback {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmCallbackListener.class);

    @Autowired
    private RabbitProducerMqService rabbitProducerMqService;

    @Override
    public void confirm(CorrelationData correlationData, boolean status, String cause) {
        if (correlationData != null) {
            String messageKey = correlationData.getId();
            logger.info("收到消息回调 , mq流水号 : " + messageKey + " 状态 : " + status);
            RabbitProducerMqEntity producerMqEntity = rabbitProducerMqService.findByMessageKey(messageKey);
            if (status) {
                producerMqEntity.setStatus(RabbitProducerMqEntity.ProducerMqEntityStatus.SUCCESS);
            } else {
                producerMqEntity.setStatus(RabbitProducerMqEntity.ProducerMqEntityStatus.FAILED);
            }
            rabbitProducerMqService.save(producerMqEntity) ;
        }
    }

}
