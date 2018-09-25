package com.mo9.raptor.service;

import com.mo9.raptor.entity.LinkfaceLogEntity;
import com.mo9.raptor.entity.RuleLogEntity;

public interface RuleLogService {

    /**
     * 创建记录
     * @param userCode
     * @param ruleName
     * @param hit
     * @param call
     * @param remark
     * @return
     */
    RuleLogEntity create(String userCode, String ruleName, Boolean hit,Boolean call,String remark);

}
