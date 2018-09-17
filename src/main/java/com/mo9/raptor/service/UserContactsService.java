package com.mo9.raptor.service;

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
}
