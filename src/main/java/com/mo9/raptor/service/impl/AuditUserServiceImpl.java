package com.mo9.raptor.service.impl;



import com.mo9.raptor.entity.AuditUserEntity;
import com.mo9.raptor.enums.AuditLevelEnum;
import com.mo9.raptor.repository.AuditUserRepository;
import com.mo9.raptor.service.AuditUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author zma
 * @date 2018/10/17
 */
@Service
public class AuditUserServiceImpl implements AuditUserService {

    @Autowired
    private AuditUserRepository auditUserRepository ;


    @Override
    public AuditUserEntity findByLoginNameAndPassword(String userName, String password) {
        return auditUserRepository.findByLoginNameAndPasswordAndDeleted(userName,password,false);
    }

    @Override
    public List<AuditUserEntity> findByDeleted(Boolean b) {
        return auditUserRepository.findByDeleted(false);
    }

    @Override
    public List<AuditUserEntity> findNormalUser() {
        return auditUserRepository.findByLevelAndDeleted(AuditLevelEnum.NORMAL.name(),false);
    }
}
