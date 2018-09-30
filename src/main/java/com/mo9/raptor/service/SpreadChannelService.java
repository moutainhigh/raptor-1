package com.mo9.raptor.service;

import com.mo9.raptor.entity.SpreadChannelEntity;

/**
 * @author zma
 * @date 2018/9/29
 */
public interface SpreadChannelService {
    /**
     * 查找用户
     * @param userName
     * @param password
     * @return
     */
    SpreadChannelEntity findByLoginNameAndPassword(String userName, String password);
}
