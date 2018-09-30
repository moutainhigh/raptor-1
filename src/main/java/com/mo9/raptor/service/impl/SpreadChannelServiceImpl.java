package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.SpreadChannelEntity;
import com.mo9.raptor.repository.SpreadChannelRepository;
import com.mo9.raptor.service.SpreadChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zma
 * @date 2018/9/29
 */
@Service
public class SpreadChannelServiceImpl implements SpreadChannelService{

    @Autowired
    private SpreadChannelRepository spreadChannelRepository;

    @Override
    public SpreadChannelEntity findByLoginNameAndPassword(String userName, String password) {
        return spreadChannelRepository.findByLoginNameAndPassword(userName,password);
    }
}
