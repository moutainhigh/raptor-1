package com.mo9.raptor.repository;

import com.mo9.raptor.entity.BankEntity;
import com.mo9.raptor.entity.CashAccountEntity;
import com.mo9.raptor.entity.CashAccountLogEntity;
import com.mo9.raptor.enums.BusinessTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by xtgu on 2018/9/12.
 * @author xtgu
 */
public interface CashAccountRepository extends JpaRepository<CashAccountEntity,Long> {

    /**
     * 根据用户id查询
     * @param userCode
     * @return
     */
    CashAccountEntity findByUserCode(String userCode);
}
