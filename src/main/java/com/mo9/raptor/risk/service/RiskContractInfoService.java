package com.mo9.raptor.risk.service;

import com.mo9.raptor.entity.UserEntity;

/**
 * Created by jyou on 2018/10/18.
 *
 * @author jyou
 */
public interface RiskContractInfoService {

    void createAll(String contractData, UserEntity userEntity);

}
