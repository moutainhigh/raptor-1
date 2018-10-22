package com.mo9.raptor.repository;

import com.mo9.raptor.entity.ShixinEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by jyou on 2018/10/22.
 *
 * @author jyou
 */
public interface ShixinRepository extends JpaRepository<ShixinEntity, Long> {

    @Query(value = "select count(*) from shixin where cardNum = ?1 and iname = ?2", nativeQuery = true)
    long findByCardNumAndIname(String cardNum, String iname);
}
