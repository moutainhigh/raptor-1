package com.mo9.raptor.service.impl;

import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.state.event.impl.AuditLaunchEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.BankAuthStatusEnum;
import com.mo9.raptor.repository.UserRepository;
import com.mo9.raptor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zma
 * @date 2018/9/13
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IEventLauncher loanEventLauncher;
    @Override
    public UserEntity findByUserCode(String userCode) {
        return userRepository.findByUserCode(userCode);
    }


    @Override
    public UserEntity findByUserCodeAndDeleted(String userCode, boolean isDelete) {
        return userRepository.findByUserCodeAndDeleted(userCode,isDelete);
    }

    @Override
    public UserEntity findByMobileAndDeleted(String mobile, boolean isDelete) {
        return userRepository.findByMobileAndDeleted(mobile,isDelete);
    }

    @Override
    public UserEntity findByMobile(String mobile) {
        return userRepository.findByMobile(mobile);
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity findByUserCodeAndStatus(String userCode, StatusEnum status) {
        return userRepository.findByUserCodeAndStatus(userCode, status.name());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCallHistory(UserEntity userEntity, boolean b) throws Exception {
        userEntity.setCallHistory(b);
        userRepository.save(userEntity);
        checkAuditStatus(userEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCertifyInfo(UserEntity userEntity, boolean b) throws Exception {
        userEntity.setCertifyInfo(b);
        userRepository.save(userEntity);
        checkAuditStatus(userEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMobileContacts(UserEntity userEntity, boolean b) throws Exception {
        userEntity.setMobileContacts(b);
        userRepository.save(userEntity);
        checkAuditStatus(userEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReceiveCallHistory(UserEntity userEntity, boolean b) throws Exception {
        userEntity.setReceiveCallHistory(b);
        userRepository.save(userEntity);
        checkAuditStatus(userEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBankAuthStatus(UserEntity userEntity, BankAuthStatusEnum statusEnum) throws Exception {
        userEntity.setBankAuthStatus(statusEnum.name());
        userRepository.save(userEntity);
        checkAuditStatus(userEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkAuditStatus(UserEntity userEntity) throws Exception {
        String bankAuthStatus = userEntity.getBankAuthStatus();
        Boolean certifyInfo = userEntity.getCertifyInfo();
        Boolean mobileContacts = userEntity.getMobileContacts();
        Boolean callHistory = userEntity.getCallHistory();
        Boolean receiveCallHistory = userEntity.getReceiveCallHistory();
        if (callHistory && certifyInfo && mobileContacts && receiveCallHistory && BankAuthStatusEnum.SUCCESS.name().equals(bankAuthStatus)) {
            // 信息采集完成 发起审核
            AuditLaunchEvent auditLaunchEvent = new AuditLaunchEvent(userEntity.getUserCode(),userEntity.getUserCode());
            loanEventLauncher.launch(auditLaunchEvent);
        }
    }
}
