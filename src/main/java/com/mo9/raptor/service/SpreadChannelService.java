package com.mo9.raptor.service;

import com.mo9.raptor.entity.SpreadChannelEntity;

import java.util.List;

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

    /**
     * 查询所有推广渠道
     * @return
     */
    List<SpreadChannelEntity> findAll();

    /**
     * 判断渠道是否在列表里面支持
     * @param source
     * @return
     */
    boolean checkSourceIsAllow(String source);
}
