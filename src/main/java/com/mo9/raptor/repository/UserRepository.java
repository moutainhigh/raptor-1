package com.mo9.raptor.repository;

import com.mo9.raptor.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
     * 根据绑定手机号查询所有用户是否存在
     * @param mobile
     * @return
     */
    UserEntity findByMobile(String mobile);

    /**
     * 根据用户手机号和是否删除 查询
     * @param mobile
     * @param isDelete
     * @return
     */
    UserEntity findByMobileAndDeleted(String mobile, boolean isDelete);

    /**
     * 根据userCode和状态查询用户
     * @param userCode   用户
     * @param status     状态
     * @return
     */
    @Query(value = "select t from UserEntity t where t.userCode = ?1 and t.status = ?2 and t.deleted = 0")
    UserEntity findByUserCodeAndStatus(String userCode, String status);
}
