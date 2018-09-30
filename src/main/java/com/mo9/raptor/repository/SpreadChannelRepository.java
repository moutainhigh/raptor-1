package com.mo9.raptor.repository;

import com.mo9.raptor.entity.SpreadChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpreadChannelRepository extends JpaRepository<SpreadChannelEntity, Long> {
    SpreadChannelEntity findByLoginNameAndPassword(String userName, String password);
}
