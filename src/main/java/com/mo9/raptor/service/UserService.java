package com.mo9.raptor.service;

import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.BankAuthStatusEnum;

/**
 * @author zma
 * @date 2018/9/13
 */
public interface UserService {


    UserEntity findByUserCode(String userCode);
    /**
     * 根据userCode查询是否禁用的用户
     * @param userCode
     * @param isDelete
     * @return
     */
    UserEntity findByUserCodeAndDeleted(String userCode, boolean isDelete);
    /**
     * 根据mobile查询是否禁用的用户
     * @param mobile
     * @param isDelete
     * @return
     */
    UserEntity findByMobileAndDeleted(String mobile, boolean isDelete);

    /**
     * 根据绑定手机号查询用户是否存在
     * @param mobile
     * @return
     */
    UserEntity findByMobile(String mobile);

    UserEntity save (UserEntity userEntity);

    /**
     * 根据userCode和状态查询用户
     * @param userCode   用户
     * @param status     状态
     * @return
     */
    UserEntity findByUserCodeAndStatus(String userCode, StatusEnum status);

    /**
     * 修改通话记录状态
     * @param userEntity
     * @param b
     */
    void updateCallHistory(UserEntity userEntity, boolean b) throws Exception;

    /**
     * 修改身份信息状态
     * @param userEntity
     * @param b
     */
    void updateCertifyInfo(UserEntity userEntity, boolean b) throws Exception;

    /**
     * 修改通讯录状态
     * @param userEntity
     * @param b
     */
    void updateMobileContacts(UserEntity userEntity, boolean b) throws Exception;
    /**
     * 修改是否收到通讯录数据
     * @param userEntity
     * @param b
     */
    void updateReceiveCallHistory(UserEntity userEntity, boolean b) throws Exception;

    /**
     * 修改银行卡认证状态
     * @param userEntity
     * @param statusEnum
     */
    void updateBankAuthStatus(UserEntity userEntity, BankAuthStatusEnum statusEnum) throws Exception;

    /**
     * 检查身份基本信息认证，银行卡认证，手机通讯录认证，手机通话记录是否上传，是否收到通讯录数据
     * @param userEntity
     * @return
     */
    void checkAuditStatus(UserEntity userEntity) throws Exception;

}
