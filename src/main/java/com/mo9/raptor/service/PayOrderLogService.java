package com.mo9.raptor.service;

import com.mo9.raptor.entity.PayOrderLogEntity;
import org.springframework.transaction.annotation.Transactional;

/**
 * 还款订单service
 * Created by xzhang on 2018/9/13.
 */
public interface PayOrderLogService {

    @Transactional(rollbackFor = Exception.class)
    void save(PayOrderLogEntity payOrderLog);
}
