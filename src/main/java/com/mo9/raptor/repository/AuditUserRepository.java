package com.mo9.raptor.repository;

import com.mo9.raptor.entity.AuditUserEntity;
import com.mo9.raptor.enums.AuditLevelEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditUserRepository extends JpaRepository<AuditUserEntity, Long> {
    AuditUserEntity findByLoginNameAndPassword(String userName, String password);

    List<AuditUserEntity> findByLevelAndDeleted(String normal,boolean b);

    AuditUserEntity findByLoginNameAndPasswordAndDeleted(String userName, String password, boolean b);

    List<AuditUserEntity> findByDeleted(boolean b);
}
