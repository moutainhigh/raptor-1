package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.UserContactsEntity;
import com.mo9.raptor.repository.UserContactsRepository;
import com.mo9.raptor.repository.UserRepository;
import com.mo9.raptor.service.UserContactsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 */
@Service
public class UserContactsServiceImpl implements UserContactsService {

    @Value("${raptor.sockpuppet}")
    private String sockpuppet;

    @Resource
    private UserContactsRepository userContactsRepository;

    @Resource
    private UserRepository userRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitMobileContacts(String data, String userCode ,String clientId, String clientVersion) {
        UserContactsEntity entity = new UserContactsEntity();
        entity.setUserCode(userCode);
        entity.setContactsList(data);
        entity.setClientId(clientId);
        entity.setClientVersion(clientVersion);
        entity.setSockpuppet(sockpuppet);
        userContactsRepository.save(entity);
    }

    @Override
    public long findMobileContactsCount(String userCode) {
        return userContactsRepository.findMobileContactsCount(userCode);
    }

    @Override
    public UserContactsEntity getByUserCode(String userCode) {
        return userContactsRepository.getByUserCode(userCode);
    }

    @Override
    public List<UserContactsEntity> findByLimit(int startLimit, int endLimit) {
        return userContactsRepository.findByLimit(startLimit, endLimit);
    }
}
