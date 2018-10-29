package com.mo9.raptor.repository;

import com.mo9.raptor.entity.SpreadChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpreadChannelRepository extends JpaRepository<SpreadChannelEntity, Long> {

    SpreadChannelEntity findByLoginNameAndPassword(String userName, String password);

    @Query(value = "select t from SpreadChannelEntity t where t.isDelete = false")
    List<SpreadChannelEntity> findAllNotDelete();
}
