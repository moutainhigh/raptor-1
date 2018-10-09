package com.mo9.raptor.service;

import com.mo9.raptor.entity.RabbitProducerMqEntity;

/**
 * Created by xtgu on 2018/7/26.
 * @author xtgu
 * rabbitmq service
 */
public interface RabbitProducerMqService {

    /**
     * 创建mq信息
     * @param loanKey
     * @param messageKey
     * @param messageStr
     * @param status
     */
    void createProducerMq(String loanKey, String messageKey, String messageStr, RabbitProducerMqEntity.ProducerMqEntityStatus status);

    /**
     * 保存
     * @param rabbitProducerMqEntity
     */
    void save(RabbitProducerMqEntity rabbitProducerMqEntity);

    /**
     * 根据messageKey查询mq信息
     * @param messageKey
     * @return
     */
    RabbitProducerMqEntity findByMessageKey(String messageKey);
}
