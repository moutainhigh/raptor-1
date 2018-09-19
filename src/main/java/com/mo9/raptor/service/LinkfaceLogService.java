package com.mo9.raptor.service;

import com.mo9.raptor.entity.LinkfaceLogEntity;

public interface LinkfaceLogService {

    /**
     * 添加纪录
     *
     * @param callParams
     * @param status
     * @param remark
     */
    LinkfaceLogEntity create(String userCode, String callParams, String status, String remark);

    void update(LinkfaceLogEntity entity);
}
