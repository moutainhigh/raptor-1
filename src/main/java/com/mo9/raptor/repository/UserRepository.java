package com.mo9.raptor.repository;

import com.mo9.raptor.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author zma
 * @date 2018/9/13
 */
public interface UserRepository extends JpaRepository<UserEntity,Long> {

    UserEntity findByUserCode(String userCode);
    /**
     * 根据userCode查询是否禁用的用户
     * @param userCode
     * @param isDelete
     * @return
     */
    UserEntity findByUserCodeAndDeleted(String userCode, boolean isDelete);
    /**
     * 根据绑定手机号查询用户是否存在
     * @param mobile
     * @return
     */
    UserEntity findByMobile(String mobile);
}
