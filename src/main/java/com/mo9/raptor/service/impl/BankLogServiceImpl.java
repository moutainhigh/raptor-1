package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.BankLogEntity;
import com.mo9.raptor.repository.BankLogRepository;
import com.mo9.raptor.repository.BankRepository;
import com.mo9.raptor.service.BankLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xtgu on 2018/9/20.
 * @author xtgu
 */
@Service
public class BankLogServiceImpl implements BankLogService {

    @Autowired
    private BankLogRepository bankLogRepository ;

    @Override
    public void create(String bankNo, String cardId, String userName, String mobile, String bankName, String userCode, Integer cardStartCount, Integer cardSuccessCount, Integer cardFailCount, String status) {
        Long time = System.currentTimeMillis() ;
        BankLogEntity bankLogEntity = new BankLogEntity();
        bankLogEntity.setBankNo(bankNo);
        bankLogEntity.setCardId(cardId);
        bankLogEntity.setMobile(mobile);
        bankLogEntity.setUserName(userName);
        bankLogEntity.setCreateTime(time) ;
        bankLogEntity.setBankName(bankName);
        bankLogEntity.setUserCode(userCode);
        bankLogEntity.setCardStartCount( cardStartCount);
        bankLogEntity.setCardSuccessCount( cardSuccessCount);
        bankLogEntity.setCardFailCount( cardFailCount);
        bankLogEntity.setStatus(status);
        bankLogRepository.save(bankLogEntity) ;
    }

    @Override
    public List<BankLogEntity> findByMobileAndBankNoAndIdCardAndUserNameAndStatus(String mobile, String bankNo, String cardId, String userName , String status) {
        return bankLogRepository.findByMobileAndBankNoAndIdCardAndUserNameAndStatus(  mobile,   bankNo,   cardId,   userName , status);
    }
}
