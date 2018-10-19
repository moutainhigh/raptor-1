package com.mo9.raptor.repository;

import com.mo9.raptor.entity.AuditOperationRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface AuditOperationRecordRepository extends JpaRepository<AuditOperationRecordEntity, Long> {

    List<AuditOperationRecordEntity> findByStatusAndOperateIdAndDistributeId(String status, String operateId, String distributeId);

    List<AuditOperationRecordEntity> findByStatusAndOperateId(String status, String operateId);

    List<AuditOperationRecordEntity> findByOperateId(String operateId);

    @Query(value = "select u.user_code userCode from t_raptor_user u LEFT JOIN t_audit_operation_record a ON a.user_code = u.user_code  where a.user_code IS NULL and u.`status` = 'MANUAL' ORDER BY u.auth_time asc LIMIT ?1", nativeQuery = true)
    List<Map<String,String>> findManualAuditUser(Integer limit);

    @Query(value = "select  all_audit_num ,operate_id,login_name,name,ifnull(num2,0) AS manual_audit_num FROM (select count(*) all_audit_num ,operate_id o_id,status,login_name,name from t_audit_operation_record r  LEFT JOIN t_audit_user u on u.id = r.operate_id where distribute_id = ?1 GROUP BY operate_id) AS a " +
            "LEFT JOIN " +
            "(select count(*) num2 ,operate_id from t_audit_operation_record r  LEFT JOIN t_audit_user u on u.id = r.operate_id where `status`= 'MANUAL' and distribute_id = ?1 GROUP BY operate_id) b " +
            "ON b.operate_id = a.o_id;", nativeQuery = true)
    List<Map<String,Object>> getAuditRecord(Long id);

    AuditOperationRecordEntity findByOperateIdAndUserCode(String id, String userCode);

    Long countByStatus(String name);
}
