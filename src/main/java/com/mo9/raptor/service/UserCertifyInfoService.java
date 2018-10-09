package com.mo9.raptor.service;

import com.mo9.raptor.bean.req.ModifyCertifyReq;
import com.mo9.raptor.entity.UserCertifyInfoEntity;
import com.mo9.raptor.entity.UserEntity;

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
     * @param userEntity
     * @param userCertifyInfoEntity
     * @param modifyCertifyReq
     */
    void modifyCertifyInfo(UserEntity userEntity, UserCertifyInfoEntity userCertifyInfoEntity, ModifyCertifyReq modifyCertifyReq);

    /**
     * 根据身份证查询身份信息是否存在
     * @param idCard
     * @return
     */
    UserCertifyInfoEntity findByIdCard(String idCard);

    /**
     * 根据ocr身份证查询身份信息是否存在
     * @param ocrIdCard
     * @return
     */
    UserCertifyInfoEntity findByOcrIdCard(String ocrIdCard);
}
