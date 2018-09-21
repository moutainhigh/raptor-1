package com.mo9.raptor.repository;

import com.mo9.raptor.entity.DianHuaBangApiLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author wtwei .
 * @date 2018/9/20 .
 * @time 18:59 .
 */
public interface DianHuaBangApiLogRepository extends JpaRepository<DianHuaBangApiLogEntity, Long>{
    
    @Query(value = "select * from DianHuaBangApiLogEntity where sid = ?1", nativeQuery = true)
    DianHuaBangApiLogEntity findBySid(String sid);
}
