package com.mo9.raptor.repository;

import com.mo9.raptor.entity.BankFourElementVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by xtgu on 2018/9/12.
 * @author xtgu
 */
public interface BankFourElementVerificationRepository extends JpaRepository<BankFourElementVerificationEntity,Long> {
    /**
     * 根据银行卡查询
     * @param bankNo
     * @return
     */
    BankFourElementVerificationEntity findByBankNo(String bankNo);
}
