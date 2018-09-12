package com.mo9.raptor.service;

import com.mo9.raptor.entity.BankFourElementVerificationEntity;

/**
 * Created by xtgu on 2018/9/12.
 * @author xtgu
 */
public interface BankFourElementVerificationService {

    /**
     * 根据银行卡号查询
     * @param bankNo
     * @return
     */
    BankFourElementVerificationEntity findByBankNo(String bankNo);

    /**
     * 存储
     * @param bankFourElementVerification
     */
    void save(BankFourElementVerificationEntity bankFourElementVerification);
}
