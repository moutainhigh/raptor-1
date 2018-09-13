package com.mo9.raptor.repository;

import com.mo9.raptor.entity.BankEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

/**
 * Created by xtgu on 2018/9/12.
 * @author xtgu
 */
public interface BankRepository extends JpaRepository<BankEntity,Long> {

    /**
     * 根据银行卡查询 放款银行卡
     * @param bankNo
     * @return
     */
    @Query(value = "select t from BankEntity t where t.bankNo = ?1 and t.type = 'LOAN'")
    BankEntity findByBankNoByLoan(String bankNo);

    /**
     * 根据银行卡 , 类型 , 渠道查询
     * @param bankNo
     * @param type
     * @param channel
     * @return
     */
    @Query(value = "select t from BankEntity t where t.bankNo = ?1 and t.type = ?2 and t.channel = ?3")
    BankEntity findByBankNoAndTypeAndChannel(String bankNo, BankEntity.Type type, String channel);
}
