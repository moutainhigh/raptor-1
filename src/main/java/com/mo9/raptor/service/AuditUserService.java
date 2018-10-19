package com.mo9.raptor.service;


import com.mo9.raptor.entity.AuditUserEntity;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author zma
 * @date 2018/10/17
 */

public interface AuditUserService {
    /**
     * 根据帐号密码查询
     * @param userName
     * @param password
     * @return
     */
    AuditUserEntity findByLoginNameAndPassword(String userName, String password);

    List<AuditUserEntity> findByDeleted(Boolean b);

    List<AuditUserEntity> findNormalUser();
}
