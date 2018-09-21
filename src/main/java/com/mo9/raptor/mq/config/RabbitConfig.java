package com.mo9.raptor.mq.config;

import com.mo9.raptor.mq.listen.ConfirmCallbackListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : xjding
 * @date :   2018-07-10 13:51
 */
//@Configuration
//@ConfigurationProperties(value = "spring.rabbitmq")
public class RabbitConfig {

    /**
     *  使用一个
     */
    private String mqExchange;

    public String getMqExchange() {
        return mqExchange;
    }

    public void setMqExchange(String mqExchange) {
        this.mqExchange = mqExchange;
    }

    /*@Bean*/
    public RabbitTemplate rabbitTemplate(ConfirmCallbackListener confirmCallbackListener, ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setConfirmCallback(confirmCallbackListener);
        return rabbitTemplate;
    }

}
