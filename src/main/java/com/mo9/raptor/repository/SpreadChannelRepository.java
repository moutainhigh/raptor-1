package com.mo9.raptor.repository;

import com.mo9.raptor.entity.SpreadChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpreadChannelRepository extends JpaRepository<SpreadChannelEntity, Long> {

    @Query(value = "select t from SpreadChannelEntity t where t.loginName=?1 and t.password = ?2 and t.isDelete = false")
    SpreadChannelEntity findByLoginNameAndPasswordNotDelete(String userName, String password);

    @Query(value = "select t from SpreadChannelEntity t where  t.isDelete = false")
    List<SpreadChannelEntity> findAllNotDelete();
}
