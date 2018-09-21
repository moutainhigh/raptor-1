package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.DianHuaBangApiLogEntity;
import com.mo9.raptor.repository.DianHuaBangApiLogRepository;
import com.mo9.raptor.service.DianHuaBangApiLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wtwei .
 * @date 2018/9/20 .
 * @time 19:02 .
 */

@Service
public class DianHuaBangApiLogServiceImpl implements DianHuaBangApiLogService {
    
    @Resource
    private DianHuaBangApiLogRepository dianHuaBangApiLogRepository;
    
    @Override
    public DianHuaBangApiLogEntity create(DianHuaBangApiLogEntity dianHuaBangApiLogEntity){
        return dianHuaBangApiLogRepository.save(dianHuaBangApiLogEntity);
    }
}
