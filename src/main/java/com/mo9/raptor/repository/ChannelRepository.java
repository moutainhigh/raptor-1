package com.mo9.raptor.repository;

import com.mo9.raptor.entity.ChannelEntity;
import com.mo9.raptor.entity.PayOrderLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by xzhang on 2018/9/12.
 */
public interface ChannelRepository extends JpaRepository<ChannelEntity,Long> {

    /**
     * 根据渠道名获取所有渠道
     * @param channel       渠道名
     * @param channelType   渠道类型
     * @return         logs
     */
    @Query(value = "select * from t_raptor_channel where channel = ?1 and channel_type = ?2 and deleted = false", nativeQuery = true)
    ChannelEntity getChannelByType(String channel, String channelType);

    /**
     * 获取所有可用渠道
     * @return            还款订单
     */
    @Query(value = "select * from t_raptor_channel where deleted = false", nativeQuery = true)
    List<ChannelEntity> listAllAvailableChannels();

    /**
     * 根据类型获取渠道
     * @param channelType   渠道类型
     * @return         渠道
     */
    @Query(value = "select * from t_raptor_channel where channel_type = ?1 and deleted = false", nativeQuery = true)
    List<ChannelEntity> listByChannelType(String channelType);

    /**
     * 根据渠道名获取所有渠道
     * @param channelId       渠道id
     * @return         渠道
     */
    @Query(value = "select * from t_raptor_channel where id = ?1 and deleted = false", nativeQuery = true)
    ChannelEntity getByChannelId(Integer channelId);
}
