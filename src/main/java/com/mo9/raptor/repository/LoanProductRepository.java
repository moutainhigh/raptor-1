package com.mo9.raptor.repository;

import com.mo9.raptor.entity.LoanProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by xtgu on 2018/9/16.
 * @author xtgu
 */
public interface LoanProductRepository extends JpaRepository<LoanProductEntity,Long> {

    /**
     * 查询正在使用的
     * @return
     */
    @Query(value = "select t from LoanProductEntity t where t.isDelete = false ")
    List<LoanProductEntity> findNotDelete();

    /**
     * 根据金额查询
     * @param amount
     * @return
     */
    @Query(value = "select t from LoanProductEntity t where t.amount = ?1 ")
    List<LoanProductEntity> findByAmount(BigDecimal amount);
}
