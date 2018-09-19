package com.mo9.raptor.service.impl;

import com.mo9.raptor.bean.req.BankReq;
import com.mo9.raptor.entity.BankEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.BankAuthStatusEnum;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.repository.BankRepository;
import com.mo9.raptor.service.BankService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.GatewayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xtgu on 2018/9/12.
 * @author xtgu
 */
@Service
public class BankServiceImpl implements BankService {
    private static final Logger logger = LoggerFactory.getLogger(BankServiceImpl.class);

    @Autowired
    private BankRepository bankRepository ;

    @Autowired
    private GatewayUtils gatewayUtils ;

    @Autowired
    private UserService userService;

    @Override
    public BankEntity findByMobileLastOne(String mobile) {
        List<BankEntity> bankEntityList = bankRepository.findByMobile(mobile);
        if(bankEntityList != null && bankEntityList.size() > 0){
            return bankEntityList.get(0);
        }
        return null;
    }

    @Override
    public BankEntity findByUserCodeLastOne(String userCode) {
        return bankRepository.findByUserCodeLastOne(userCode);
    }

    @Override
    public BankEntity findByBankNo(String bankNo) {
        return bankRepository.findByBankNo(bankNo) ;
    }

    @Override
    public ResCodeEnum verify(BankReq bankReq, UserEntity userEntity){
        String bankNo = bankReq.getCard() ;
        String mobile = bankReq.getCardMobile() ;
        String bankName = bankReq.getBankName() ;
        /**银行卡扫描开始计数*/
        Integer cardStartCount = bankReq.getCardStartCount();
        /**银行卡扫描成功计数*/
        Integer cardSuccessCount = bankReq.getCardSuccessCount() ;
        /**银行卡扫描失败计数*/
        Integer cardFailCount = bankReq.getCardFailCount() ;
        BankEntity bankEntity = this.findByBankNo(bankNo) ;
        String cardId = userEntity.getIdCard();
        String userName = userEntity.getRealName();
        String userCode = userEntity.getUserCode();
        if(bankEntity != null){
            //数量增加
            /**银行卡扫描开始计数*/
            Integer cardStartCountUser = bankEntity.getCardStartCount();
            /**银行卡扫描成功计数*/
            Integer cardSuccessCountUser = bankEntity.getCardSuccessCount() ;
            /**银行卡扫描失败计数*/
            Integer cardFailCountUser = bankEntity.getCardFailCount() ;
            bankEntity.setCardStartCount(cardStartCountUser + cardStartCount);
            bankEntity.setCardSuccessCount(cardSuccessCountUser + cardSuccessCount);
            bankEntity.setCardFailCount(cardFailCountUser + cardFailCount);
            bankEntity.setUpdateTime(System.currentTimeMillis());
            bankRepository.save(bankEntity) ;

            //判断本地数据四要素正确情况
            if(!(cardId.equals(bankEntity.getCardId()) && userName.equals(bankEntity.getUserName()) && mobile.equals(bankEntity.getMobile()))){
                logger.error("本地 四要素验证失败" + bankNo + " - " + cardId + " - " + userName + " - " + mobile);
                return ResCodeEnum.BANK_VERIFY_ERROR ;
            }else{
                try {
                    userService.updateBankAuthStatus(userEntity,BankAuthStatusEnum.SUCCESS);
                } catch (Exception e) {
                    logger.error("更新银行卡状态,系统内部异常",e);
                    return ResCodeEnum.EXCEPTION_CODE;
                }
                return ResCodeEnum.SUCCESS ;
            }
        }
        ResCodeEnum resCodeEnum = gatewayUtils.verifyBank( bankNo ,  cardId ,  userName ,  mobile) ;
        if(ResCodeEnum.SUCCESS == resCodeEnum){
            this.create( bankNo , cardId , userName , mobile , bankName , userCode , cardStartCount , cardSuccessCount , cardFailCount) ;
            try {
                userService.updateBankAuthStatus(userEntity,BankAuthStatusEnum.SUCCESS);
            } catch (Exception e) {
                logger.error("更新银行卡状态,系统内部异常",e);
                return ResCodeEnum.EXCEPTION_CODE;
            }
        }
        return resCodeEnum ;
    }

    @Override
    public void createOrUpdateBank(String bankNo, String cardId, String userName, String mobile, String channel, String bankName , String userCode) {
        BankEntity bankEntity = this.findByBankNo(bankNo) ;
        if(bankEntity == null){
            this.create( bankNo , cardId , userName , mobile , bankName,userCode , 0 , 0 , 0) ;
        }else{
            //更新update时间
            bankEntity.setUpdateTime(System.currentTimeMillis());
            bankRepository.save(bankEntity) ;
        }
    }

    /**
     * 创建
     * @param bankNo
     * @param cardId
     * @param userName
     * @param mobile
     * @param bankName
     * @param userCode
     */
    private void create(String bankNo , String cardId , String userName , String mobile , String bankName , String userCode ,
                        Integer cardStartCount , Integer cardSuccessCount , Integer cardFailCount){
        //验证成功
        Long time = System.currentTimeMillis() ;
        BankEntity bankEntity = new BankEntity();
        bankEntity.setBankNo(bankNo);
        bankEntity.setCardId(cardId);
        bankEntity.setMobile(mobile);
        bankEntity.setUserName(userName);
        bankEntity.setCreateTime(time) ;
        bankEntity.setBankName(bankName);
        bankEntity.setUpdateTime(time) ;
        bankEntity.setUserCode(userCode);
        bankEntity.setCardStartCount( cardStartCount);
        bankEntity.setCardSuccessCount( cardSuccessCount);
        bankEntity.setCardFailCount( cardFailCount);
        //存储四要素信息
        bankRepository.save(bankEntity);
    }

}
