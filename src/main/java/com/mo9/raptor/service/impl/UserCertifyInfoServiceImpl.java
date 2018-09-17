package com.mo9.raptor.service.impl;

import com.mo9.raptor.bean.req.ModifyCertifyReq;
import com.mo9.raptor.entity.UserCertifyInfoEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.repository.UserCertifyInfoRepository;
import com.mo9.raptor.service.UserCertifyInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 */
@Service
public class UserCertifyInfoServiceImpl implements UserCertifyInfoService {

    @Resource
    private UserCertifyInfoRepository userCertifyInfoRepository;

    @Override
    public UserCertifyInfoEntity findByUserCode(String userCode) {
        return userCertifyInfoRepository.findByUserCode(userCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyCertifyInfo(UserEntity userEntity, UserCertifyInfoEntity userCertifyInfoEntity, ModifyCertifyReq modifyCertifyReq) {
        UserCertifyInfoEntity entity = buildModifyCertifyEntity(userEntity, userCertifyInfoEntity, modifyCertifyReq);
        userCertifyInfoRepository.save(entity);
    }

    private UserCertifyInfoEntity buildModifyCertifyEntity(UserEntity userEntity, UserCertifyInfoEntity entity, ModifyCertifyReq modifyCertifyReq) {
        if(entity == null){
            entity = new UserCertifyInfoEntity();
            entity.setUserCode(userEntity.getUserCode());
        }
        if(StringUtils.isNotBlank(modifyCertifyReq.getRealName())){
            entity.setRealName(modifyCertifyReq.getRealName());
        }
        if(StringUtils.isNotBlank(modifyCertifyReq.getIdCard())){
            entity.setIdCard(modifyCertifyReq.getIdCard());
        }
        if(StringUtils.isNotBlank(modifyCertifyReq.getIssuingOrgan())){
            entity.setIssuingOrgan(modifyCertifyReq.getIssuingOrgan());
        }
        if(StringUtils.isNotBlank(modifyCertifyReq.getValidityStartPeriod())){
            entity.setValidityStartPeriod(modifyCertifyReq.getValidityStartPeriod());
        }
        if(StringUtils.isNotBlank(modifyCertifyReq.getValidityEndPeriod())){
            entity.setValidityEndPeriod(modifyCertifyReq.getValidityEndPeriod());
        }
        if(modifyCertifyReq.getType() != null){
            entity.setType(modifyCertifyReq.getType());
        }

        if(StringUtils.isNotBlank(modifyCertifyReq.getAccountFrontImg())){
            entity.setAccountFrontImg(modifyCertifyReq.getAccountFrontImg());
        }

        if(StringUtils.isNotBlank(modifyCertifyReq.getAccountBackImg())){
            entity.setAccountBackImg(modifyCertifyReq.getAccountBackImg());
        }
        if(StringUtils.isNotBlank(modifyCertifyReq.getAccountOcr())){
            entity.setAccountOcr(modifyCertifyReq.getAccountOcr());
        }
        if(StringUtils.isNotBlank(modifyCertifyReq.getOcrRealName())){
            entity.setOcrRealName(modifyCertifyReq.getOcrRealName());
        }
        if(StringUtils.isNotBlank(modifyCertifyReq.getOcrIdCard())){
            entity.setOcrIdCard(modifyCertifyReq.getOcrIdCard());
        }
        if(StringUtils.isNotBlank(modifyCertifyReq.getOcrIssueAt())){
            entity.setOcrIdCard(modifyCertifyReq.getOcrIssueAt());
        }

        if(StringUtils.isNotBlank(modifyCertifyReq.getOcrDurationStartTime())){
            entity.setOcrDurationStartTime(modifyCertifyReq.getOcrDurationStartTime());
        }
        if(StringUtils.isNotBlank(modifyCertifyReq.getOcrDurationEndTime())){
            entity.setOcrDurationEndTime(modifyCertifyReq.getOcrDurationEndTime());
        }
        if(StringUtils.isNotBlank(modifyCertifyReq.getOcrNationality())){
            entity.setOcrNationality(modifyCertifyReq.getOcrNationality());
        }
        if(StringUtils.isNotBlank(modifyCertifyReq.getOcrBirthday())){
            entity.setOcrBirthday(modifyCertifyReq.getOcrBirthday());
        }
        if(modifyCertifyReq.getOcrGender() != null){
            entity.setOcrGender(modifyCertifyReq.getOcrGender());
        }
        long now = System.currentTimeMillis();
        if(entity.getCreateTime() == null){
            entity.setCreateTime(now);
        }
        entity.setUpdateTime(now);
        return entity;
    }
}
