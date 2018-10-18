package com.mo9.raptor.service;


import com.mo9.raptor.entity.AuditOperationRecordEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * @author zma
 * @date 2018/10/17
 */
public interface AuditOperationRecordService {
    /**
     * 根据状态和操作人查找待审核用户
     * @param status
     * @param operateId
     * @return
     */
    List<AuditOperationRecordEntity> findByStatusAndOperateId(String status, String operateId);
    /**
     * 操作人查找待审核用户
     * @param operateId
     * @return
     */
    List<AuditOperationRecordEntity> findByOperateId(String operateId);

    /**
     * 分配待审核用户
     */
    void distributeUser(Integer limit,String operateId,String distributeId) throws Exception;

    /**
     * 根据主管id查询 操作员已审核 和未审核案件总数
     * @param id
     * @return
     */
    List<Map<String,Object>> getAuditRecord(Long id);

    /**
     * 根据userCode和操作员id查询唯一案件
     * @param id
     * @param userCode
     * @return
     */
    AuditOperationRecordEntity findByOperateIdAndUserCode(Long id, String userCode);

    void save(AuditOperationRecordEntity auditOperationRecordEntity);
}
