package com.mo9.raptor.task;

import com.mo9.raptor.entity.RabbitProducerMqEntity;
import com.mo9.raptor.mq.producer.RabbitProducer;
import com.mo9.raptor.repository.RabbitProducerMqRepository;
import com.mo9.raptor.utils.CommonValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by xtgu on 2018/3/2.
 * @author xtgu
 */
//@Component
public class RabbitProducerMqTask {

	private static final Logger logger = LoggerFactory.getLogger(RabbitProducerMqTask.class);

//	@Autowired
	private RabbitProducer rabbitProducer ;

	@Autowired
	private RabbitProducerMqRepository rabbitProducerMqRepository;

	@Value("${task.open}")
	private String taskOpen ;

	/**
	 * 发送失败的信息再次发送
	 */
//	@Scheduled(cron = "0 0/10 * * * ?")
	public void sendFailedMessageAgain(){
		if(CommonValues.TRUE.equals(taskOpen)){
			logger.info("mq补救接口开始 -- " );
			//查询所有失败信息
			List<RabbitProducerMqEntity> rabbitProducerMqEntities = rabbitProducerMqRepository.findByStatus(RabbitProducerMqEntity.ProducerMqEntityStatus.FAILED);
			for (RabbitProducerMqEntity rabbitProducerMqEntity:rabbitProducerMqEntities ) {
				try {
					rabbitProducer.sendMessageRemedy(rabbitProducerMqEntity);
				} catch (Exception e) {
					logger.info("mq补救接口异常 -- messageKey为 : " + rabbitProducerMqEntity.getMessageKey());
				}
			}
			logger.info("mq补救接口结束 , 总计处理 "+rabbitProducerMqEntities.size()+" 条数据");
		}

	}
}
