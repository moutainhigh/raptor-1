package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.RabbitProducerMqEntity;
import com.mo9.raptor.repository.RabbitProducerMqRepository;
import com.mo9.raptor.service.RabbitProducerMqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by xtgu on 2018/7/26.
 * @author xtgu
 */
@Service
public class RabbitProducerMqServiceImpl implements RabbitProducerMqService {

    @Autowired
    private RabbitProducerMqRepository rabbitProducerMqRepository ;

    @Override
    @Transactional(rollbackFor = Exception.class , propagation= Propagation.REQUIRES_NEW)
    public void createProducerMq(String loanKey, String messageKey, String messageStr, RabbitProducerMqEntity.ProducerMqEntityStatus status) {
        RabbitProducerMqEntity producerMqEntity = new RabbitProducerMqEntity(loanKey , messageKey, messageStr , status);
        rabbitProducerMqRepository.save(producerMqEntity) ;
    }

    @Override
    public void save(RabbitProducerMqEntity rabbitProducerMqEntity) {
        rabbitProducerMqRepository.save(rabbitProducerMqEntity) ;
    }

    @Override
    public RabbitProducerMqEntity findByMessageKey(String messageKey) {
        List<RabbitProducerMqEntity> list = rabbitProducerMqRepository.findByMessageKey( messageKey);
        if(list != null && list.size() > 0){
            return list.get(0);
        }else{
            return null ;
        }
    }


}
