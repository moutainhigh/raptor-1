package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.repository.UserRepository;
import com.mo9.raptor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zma
 * @date 2018/9/13
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserEntity findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }


    @Override
    public UserEntity findByUserIdAndDeleted(String userId, boolean isDelete) {
        return userRepository.findByUserIdAndDeleted(userId,isDelete);
    }

    @Override
    public UserEntity findByMobile(String mobile) {
        return userRepository.findByMobile(mobile);
    }
}
