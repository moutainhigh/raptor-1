package com.mo9.raptor.repository;

import com.mo9.raptor.entity.UserCertifyInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 */
public interface UserCertifyInfoRepository extends JpaRepository<UserCertifyInfoEntity,Long> {

    /**
     * 根据userCode查询用户身份信息
     * @param userCode
     * @return
     */
    UserCertifyInfoEntity findByUserCode(String userCode);

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
