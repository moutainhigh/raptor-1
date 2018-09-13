package com.mo9.raptor.mq;

import com.mo9.mqclient.IMqMsgListener;
import com.mo9.mqclient.MqSubscription;
import com.mo9.mqclient.impl.aliyun.AliyunMessageModel;
import com.mo9.mqclient.impl.aliyun.AliyunMqConsumer;
import com.mo9.raptor.mq.listen.LoanMo9mqListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sun on 2017/8/1.
 */
@ConditionalOnProperty(name = "mq.enable", havingValue = "true")
@Configuration
public class MQConfig {

  private static final Logger logger = LoggerFactory.getLogger(MQConfig.class);
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


  @Resource
  private LoanMo9mqListener loanMo9mqListener;

  /**
   * 订单驱动事件消费者
   */
  @Bean(initMethod = "start", destroyMethod = "shutdown")
  public AliyunMqConsumer orderDriverConsumer() {
    logger.info("开始初始化订单驱动事件消费端");
    AliyunMqConsumer consumer = new AliyunMqConsumer();
    consumer.setConsumerId(this.consumerId);
    consumer.setMessageModel(AliyunMessageModel.CLUSTERING);
    consumer.setOnsAddr(this.onsAddr);
    consumer.setAccessKey(this.accessKey);
    consumer.setSecretKey(this.secretKey);

    MqSubscription orderEventTag = new MqSubscription();
    orderEventTag.setTopic(this.gatewayTopic);
    orderEventTag.setExpression("*");

    Map<MqSubscription, IMqMsgListener> map = new HashMap<MqSubscription, IMqMsgListener>();
    map.put(orderEventTag, this.loanMo9mqListener);
    consumer.setSubscriptionMap(map);
    return consumer;
  }
}
