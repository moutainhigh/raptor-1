package com.mo9.raptor.service;

import com.mo9.raptor.entity.ChannelEntity;

import java.util.List;

/**
 * Created by xzhang on 2018/9/16.
 */
public interface ChannelService {

    /**
     * 根据渠道名获取所有渠道
     * @param channel       渠道名
     * @param channelType   渠道类型
     * @return         渠道
     */
    ChannelEntity getChannelByType(String channel, String channelType);

    /**
     * 根据渠道名获取所有渠道
     * @param channelId       渠道id
     * @return         渠道
     */
    ChannelEntity getByChannelId(Integer channelId);

    /**
     * 根据类型获取渠道
     * @param channelType   渠道类型
     * @return         渠道
     */
    List<ChannelEntity> listByChannelType(String channelType);

    /**
     * 获取所有可用渠道
     * @return            渠道
     */
    List<ChannelEntity> listAllAvailableChannels();
}
