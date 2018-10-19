package com.mo9.raptor.service.impl;



import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.entity.AuditOperationRecordEntity;
import com.mo9.raptor.entity.AuditUserEntity;
import com.mo9.raptor.enums.AuditLevelEnum;
import com.mo9.raptor.enums.AuditModeEnum;
import com.mo9.raptor.repository.AuditOperationRecordRepository;
import com.mo9.raptor.repository.AuditUserRepository;
import com.mo9.raptor.service.AuditOperationRecordService;
import com.mo9.raptor.service.AuditUserService;
import com.mo9.raptor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author zma
 * @date 2018/10/17
 */
@Service
public class AuditOperationRecordServiceImpl implements AuditOperationRecordService {

    @Autowired
    private AuditOperationRecordRepository auditOperationRecordRepository;

    @Autowired
    private UserService userService;


    @Override
    public List<AuditOperationRecordEntity> findByStatusAndOperateId(String status, String operateId) {
        return auditOperationRecordRepository.findByStatusAndOperateId(status,operateId);
    }
    @Override
    public List<AuditOperationRecordEntity> findByOperateId(String operateId) {
        return auditOperationRecordRepository.findByOperateId(operateId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void distributeUser(Integer limit, String operateId,String distributeId) throws Exception {
        List<Map<String,String>> manualAuditUser =  auditOperationRecordRepository.findManualAuditUser(limit);
        List<AuditOperationRecordEntity> data = new ArrayList<>();
        //批量存储
        for(Map<String,String> map : manualAuditUser) {
            if(data.size() == 100) {
                auditOperationRecordRepository.saveAll(data);
                data.clear();
            }
            AuditOperationRecordEntity auditOperationRecordEntity = new AuditOperationRecordEntity();
            auditOperationRecordEntity.setDistributeId(distributeId);
            auditOperationRecordEntity.setOperateId(operateId);
            auditOperationRecordEntity.setStatus(AuditModeEnum.MANUAL.name());
            auditOperationRecordEntity.setUserCode(map.get("userCode"));
            auditOperationRecordEntity.setUpdateTime(System.currentTimeMillis());
            auditOperationRecordEntity.setCreateTime(System.currentTimeMillis());
            data.add(auditOperationRecordEntity);
        }
        if(!data.isEmpty()) {
            auditOperationRecordRepository.saveAll(data);
        }
    }

    @Override
    public List<Map<String, Object>> getAuditRecord(Long id) {
        return auditOperationRecordRepository.getAuditRecord(id);
    }

    @Override
    public AuditOperationRecordEntity findByOperateIdAndUserCode(Long id, String userCode) {
        return auditOperationRecordRepository.findByOperateIdAndUserCode(id.toString(),userCode);
    }

    @Override
    public void save(AuditOperationRecordEntity auditOperationRecordEntity) {
        auditOperationRecordRepository.save(auditOperationRecordEntity);
    }

    @Override
    public Long countByStatus(StatusEnum status) {
        Long total = auditOperationRecordRepository.countByStatus(status.name());
        if (total == null){
            total = 0L;
        }
        return total;
    }
}
