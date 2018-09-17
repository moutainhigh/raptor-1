package com.mo9.raptor.repository;

import com.mo9.raptor.entity.MessageNotifyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author zma
 * @date 2018/9/17
 */
public interface MessageNotifyRepository extends JpaRepository<MessageNotifyEntity,Long> {

    List<MessageNotifyEntity> findByStatus(MessageNotifyEntity.MessageSendStatus status);

}
