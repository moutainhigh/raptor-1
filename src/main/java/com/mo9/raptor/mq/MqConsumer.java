package com.mo9.raptor.mq;

import com.aliyun.openservices.ons.api.Consumer;

import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.PropertyValueConst;
import com.mo9.mqclient.IMqMsgListener;
import com.mo9.mqclient.MqSubscription;
import com.mo9.mqclient.impl.aliyun.AliyunMqConsumer;
import com.mo9.mqclient.impl.aliyun.AliyunMqMsgListener;
import com.mo9.raptor.mq.listen.LoanMo9mqListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Properties;

/**
 * mq监听器配置
 * @author xtgu
 */
@Component
public class MqConsumer extends AliyunMqConsumer {

  /**
   * 消费者ID
   */
  @Value("${aliyun.mq.consumer.id}")
  private String consumerId;

  /**
   * 阿里云身份验证accessKey，在阿里云服务器管理控制台创建
   */
  @Value("${aliyun.mq.access.key}")
  private String accessKey;

  /**
   * 阿里云身份验证secretKey，在阿里云服务器管理控制台创建
   */
  @Value("${aliyun.mq.secret.key}")
  private String secretKey;

  /**
   * 阿里云服务地址
   */
  @Value("${aliyun.mq.ons.addr}")
  private String onsAddr;

  /**
   * 监听的topic - 先玩后付
   */
  @Value("${aliyun.mq.gateway.topic}")
  private String gatewayTopic;

  /**
   * 消费者最大线程数,默认50
   */
  private int consumeThreadNums = 50;

  /**
   * 消费者
   */
  private Consumer consumer;

  @Override
  @PostConstruct
  public void start() {

    Properties producerProperties = new Properties();
    producerProperties.setProperty(PropertyKeyConst.ConsumerId, this.consumerId);
    producerProperties.setProperty(PropertyKeyConst.AccessKey, this.accessKey);
    producerProperties.setProperty(PropertyKeyConst.SecretKey, this.secretKey);
    producerProperties.setProperty(PropertyKeyConst.ONSAddr, this.onsAddr);
    producerProperties.setProperty(PropertyKeyConst.ConsumeThreadNums, Integer.toString(this.consumeThreadNums));
    producerProperties.setProperty(PropertyKeyConst.MessageModel, PropertyValueConst.CLUSTERING);

    MqSubscription mqSubscription = new MqSubscription() ;
    mqSubscription.setExpression("*");
    mqSubscription.setTopic(this.gatewayTopic);
    LoanMo9mqListener loanMo9mqListener = new LoanMo9mqListener() ;

    this.consumer = ONSFactory.createConsumer(producerProperties);
    this.subscribe(mqSubscription , loanMo9mqListener);

    while (true) {
      if (this.consumer.isClosed()) {
        this.consumer.start();
        return;
      }

    }

  }

  @Override
  @PreDestroy
  public void shutdown() {

    if (this.consumer != null) {
      this.consumer.shutdown();
    }

  }

  @Override
  public boolean isStarted() {
    if (this.consumer != null) {
      return this.isStarted();
    } else {
      return false;
    }

  }

  @Override
  public boolean isClosed() {

    if (this.consumer != null) {
      return this.isClosed();
    } else {
      return true;
    }
  }

  @Override
  public void subscribe(MqSubscription subscription, IMqMsgListener mqMsgListener) {
    if (this.consumer != null) {
      AliyunMqMsgListener aliyunMQMsgListener = new AliyunMqMsgListener(mqMsgListener);
      aliyunMQMsgListener.setConsumerId(this.consumerId);
      this.consumer
              .subscribe(subscription.getTopic(), subscription.getExpression(), aliyunMQMsgListener);
    }
  }

  @Override
  public void unsubscribe(String topic) {
    if (this.consumer != null) {
      this.consumer.unsubscribe(topic);
    }
  }

}