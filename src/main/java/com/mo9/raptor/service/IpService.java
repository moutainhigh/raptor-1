package com.mo9.raptor.service;

import com.mo9.raptor.entity.IpEntity;

import java.util.List;

/**
 * Created by jyou on 2018/10/22.
 *
 * @author jyou
 */
public interface IpService {

    /**
     * 根据ip查询指定start和end范围内数据
     * @param ipNum
     * @return
     */
    List<IpEntity> findByIpNum(Long ipNum);
}
