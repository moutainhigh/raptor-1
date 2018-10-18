package com.mo9.raptor.repository;

import com.mo9.raptor.entity.AuditUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditUserRepository extends JpaRepository<AuditUserEntity, Long> {
    AuditUserEntity findByLoginNameAndPassword(String userName, String password);
}
