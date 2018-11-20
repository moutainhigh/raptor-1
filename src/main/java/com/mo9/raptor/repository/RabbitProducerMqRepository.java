package com.mo9.raptor.repository;

import com.mo9.raptor.entity.RabbitProducerMqEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by xtgu on 2018/7/13.
 * @author xtgu
 */
public interface RabbitProducerMqRepository extends JpaRepository<RabbitProducerMqEntity,Long> {
    /**
     * 根据messageKey 查询
     * @param messageKey
     * @return
     */
    List<RabbitProducerMqEntity> findByMessageKey(String messageKey);

    /**
     * 根据状态查询
     * @param failed
     * @return
     */
    List<RabbitProducerMqEntity> findByStatus(RabbitProducerMqEntity.ProducerMqEntityStatus failed);
}
