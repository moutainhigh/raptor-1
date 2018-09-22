package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.ChannelEntity;
import com.mo9.raptor.repository.ChannelRepository;
import com.mo9.raptor.service.ChannelService;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xzhang on 2018/9/16.
 */
@Service("channelServiceImpl")
public class ChannelServiceImpl implements ChannelService {

    private static Logger logger = Log.get();
    @Autowired
    private ChannelRepository channelRepository;

    @Override
    public ChannelEntity getChannelByType(String channel, String channelType) {
        return channelRepository.getChannelByType(channel, channelType);
    }

    @Override
    public ChannelEntity getByChannelId(Integer channelId) {
        return channelRepository.getByChannelId(channelId);
    }

    @Override
    public List<ChannelEntity> listByChannelType(String channelType) {
        return channelRepository.listByChannelType(channelType);
    }

    @Override
    public List<ChannelEntity> listAllAvailableChannels() {
        return channelRepository.listAllAvailableChannels();
    }
}
