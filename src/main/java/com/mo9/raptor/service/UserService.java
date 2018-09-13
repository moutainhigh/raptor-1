package com.mo9.raptor.service;

import com.mo9.raptor.entity.UserEntity;

/**
 * @author zma
 * @date 2018/9/13
 */
public interface UserService {


    UserEntity findByUserId(String userId);
    /**
     * 根据userId查询是否禁用的用户
     * @param userId
     * @param isDelete
     * @return
     */
    UserEntity findByUserIdAndDeleted(String userId, boolean isDelete);

    /**
     * 根据绑定手机号查询用户是否存在
     * @param mobile
     * @return
     */
    UserEntity findByMobile(String mobile);
}
