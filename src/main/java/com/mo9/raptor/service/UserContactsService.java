package com.mo9.raptor.service;

import com.mo9.raptor.entity.UserContactsEntity;

import java.util.List;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 */
public interface UserContactsService {

    /**
     * 提交通讯录
     * @param data 通讯录数据
     * @param userCode
     * @param clientId
     * @param clientVersion
     */
    void submitMobileContacts(String data, String userCode ,String clientId, String clientVersion);

    /**
     * 根据userCode查询当前用户通讯录总记录数
     * @param userCode
     * @return
     */
    long findMobileContactsCount(String userCode);

    /**
     * 根据用户获得通讯录
     * @param userCode
     * @return
     */
    UserContactsEntity getByUserCode(String userCode);

    List<UserContactsEntity> findByLimit(int startLimit, int endLimit);

    /**
     * 获取最近一条通讯录
     * @param userCode
     * @return
     */
    UserContactsEntity findLatelyUserContactByUserCode(String userCode);
}
