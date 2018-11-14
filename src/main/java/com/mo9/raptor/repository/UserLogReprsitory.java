package com.mo9.raptor.repository;

import com.mo9.raptor.entity.UserLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jyou on 2018/10/30.
 *
 * @author jyou
 */
public interface UserLogReprsitory extends JpaRepository<UserLogEntity,Long> {
}
