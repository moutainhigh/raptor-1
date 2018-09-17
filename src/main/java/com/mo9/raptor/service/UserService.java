package com.mo9.raptor.service;

import com.mo9.raptor.entity.UserEntity;

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
}
