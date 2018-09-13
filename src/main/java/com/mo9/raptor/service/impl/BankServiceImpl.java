package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.BankEntity;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.repository.BankRepository;
import com.mo9.raptor.service.BankService;
import com.mo9.raptor.utils.GatewayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public BankEntity findByBankNo(String bankNo) {
        return bankRepository.findByBankNo(bankNo) ;
    }

    /**
     * 易联四要素验证
     * @param bankNo
     * @param cardId
     * @param userName
     * @param mobile
     * @return
     */
    @Override
    public ResCodeEnum verify(String bankNo , String cardId , String userName , String mobile){
        BankEntity bankEntity = this.findByBankNo(bankNo) ;
        if(bankEntity != null){
            //判断本地数据四要素正确情况
            if(!(cardId.equals(bankEntity.getCardId()) && userName.equals(bankEntity.getUserName()) && mobile.equals(bankEntity.getMobile()))){
                logger.error("本地 四要素验证失败" + bankNo + " - " + cardId + " - " + userName + " - " + mobile);
                return ResCodeEnum.BANK_VERIFY_ERROR ;
            }
        }
        ResCodeEnum resCodeEnum = gatewayUtils.verifyBank( bankNo ,  cardId ,  userName ,  mobile) ;
        if(ResCodeEnum.SUCCESS == resCodeEnum){
            this.create( bankNo , cardId , userName , mobile , BankEntity.Type.LOAN) ;
        }
        return resCodeEnum ;
    }

    @Override
    public void create(String bankNo , String cardId , String userName , String mobile , BankEntity.Type type){
        //验证成功
        Long time = System.currentTimeMillis() ;
        BankEntity bankEntity = new BankEntity();
        bankEntity.setBankNo(bankNo);
        bankEntity.setCardId(cardId);
        bankEntity.setMobile(mobile);
        bankEntity.setUserName(userName);
        bankEntity.setType(type);
        bankEntity.setCreateTime(time) ;
        bankEntity.setUpdateTime(time) ;
        //存储四要素信息
        bankRepository.save(bankEntity);
    }

}
