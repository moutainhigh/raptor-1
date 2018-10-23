package com.mo9.raptor.repository;

import com.mo9.raptor.entity.IpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by jyou on 2018/10/22.
 *
 * @author jyou
 */
public interface IpRepository extends JpaRepository<IpEntity, Long> {

    @Query(value = "select * from ip where ip_start_num <= ?1 and ip_end_num >= ?1", nativeQuery = true)
    List<IpEntity> findByIpNum(Long ipNum);
}
