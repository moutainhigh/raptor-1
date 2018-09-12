package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.BankFourElementVerificationEntity;
import com.mo9.raptor.repository.BankFourElementVerificationRepository;
import com.mo9.raptor.service.BankFourElementVerificationService;
import org.springframework.stereotype.Service;

/**
 * Created by xtgu on 2018/9/12.
 * @author xtgu
 */
@Service
public class BankFourElementVerificationServiceImpl implements BankFourElementVerificationService {

    private BankFourElementVerificationRepository bankFourElementVerificationRepository ;

    @Override
    public BankFourElementVerificationEntity findByBankNo(String bankNo) {
        return bankFourElementVerificationRepository.findByBankNo(bankNo) ;
    }

    @Override
    public void save(BankFourElementVerificationEntity bankFourElementVerification) {
        bankFourElementVerificationRepository.save(bankFourElementVerification) ;
    }
}
