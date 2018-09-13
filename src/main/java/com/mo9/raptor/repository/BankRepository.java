package com.mo9.raptor.repository;

import com.mo9.raptor.entity.BankEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by xtgu on 2018/9/12.
 * @author xtgu
 */
public interface BankRepository extends JpaRepository<BankEntity,Long> {
    /**
     * 根据银行卡查询
     * @param bankNo
     * @return
     */
    BankEntity findByBankNo(String bankNo);
}
