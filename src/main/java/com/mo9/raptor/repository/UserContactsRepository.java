package com.mo9.raptor.repository;

import com.mo9.raptor.entity.UserContactsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 */
public interface UserContactsRepository extends JpaRepository<UserContactsEntity,Long> {
    @Query("select count(*) from UserContactsEntity t where t.userCode = ?1")
    long findMobileContactsCount(String userCode);

    @Query("select t from UserContactsEntity t where t.userCode = ?1")
    UserContactsEntity getByUserCode(String userCode);
}
