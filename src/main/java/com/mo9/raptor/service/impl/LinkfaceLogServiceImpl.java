package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.LinkfaceLogEntity;
import com.mo9.raptor.repository.LinkfaceLogRepository;
import com.mo9.raptor.service.LinkfaceLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LinkfaceLogServiceImpl implements LinkfaceLogService {

    @Resource
    private LinkfaceLogRepository linkfaceLogRepository;

    @Override
    public LinkfaceLogEntity create(String userCode, String callParams, String status, String remark) {
        LinkfaceLogEntity linkfaceLogEntity = new LinkfaceLogEntity();
        linkfaceLogEntity.setCallParams(callParams);
        linkfaceLogEntity.setUserCode(userCode);
        linkfaceLogEntity.setCreateTime(System.currentTimeMillis());
        linkfaceLogEntity.setUpdateTime(System.currentTimeMillis());
        linkfaceLogEntity.setRemark(remark);
        linkfaceLogEntity.setStatus(status);
        return linkfaceLogRepository.save(linkfaceLogEntity);
    }

    @Override
    public void update(LinkfaceLogEntity entity) {
        entity.setUpdateTime(System.currentTimeMillis());
        linkfaceLogRepository.saveAndFlush(entity);
    }

}
