package com.mo9.raptor.repository;

import com.mo9.raptor.entity.UserContactsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 */
public interface UserContactsRepository extends JpaRepository<UserContactsEntity,Long> {
}
