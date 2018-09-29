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

    @Query(value = "select * from t_raptor_user_contacts t where t.user_code = ?1 order by t.id desc limit 1",nativeQuery = true)
    UserContactsEntity getByUserCode(String userCode);
}
