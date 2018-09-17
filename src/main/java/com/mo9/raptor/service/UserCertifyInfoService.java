package com.mo9.raptor.service;

import com.mo9.raptor.bean.req.ModifyCertifyReq;
import com.mo9.raptor.entity.UserCertifyInfoEntity;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 */
public interface UserCertifyInfoService {

    /**
     * 根据userCode查询用户身份信息
     * @param userCode
     * @return
     */
    UserCertifyInfoEntity findByUserCode(String userCode);


    /**
     * 修改用户身份信息
     * @param userCertifyInfoEntity
     * @param modifyCertifyReq
     */
    void modifyCertifyInfo(UserCertifyInfoEntity userCertifyInfoEntity, ModifyCertifyReq modifyCertifyReq);
}
