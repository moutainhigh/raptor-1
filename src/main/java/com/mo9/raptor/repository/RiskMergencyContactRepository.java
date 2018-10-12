package com.mo9.raptor.repository;

import com.mo9.raptor.entity.RiskMergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author wtwei .
 * @date 2018/10/8 .
 * @time 14:44 .
 */
public interface RiskMergencyContactRepository extends JpaRepository<RiskMergencyContact, Long> {
    
    RiskMergencyContact findByMobileAndContractTel(String mobile, String contactTel);
}
