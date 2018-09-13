package com.mo9.raptor.utils;

import com.mo9.raptor.entity.BankFourElementVerificationEntity;
import com.mo9.raptor.service.BankFourElementVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by xtgu on 2018/9/12.
 * @author xtgu
 * 银行卡验证
 */
@Component
public class BankVerificationUtil {

    private static final Logger log = LoggerFactory.getLogger(GatewayUtils.class);

    @Autowired
    private BankFourElementVerificationService bankFourElementVerificationService ;

    /**
     * 验证银行卡四要素
     * @param bankNo
     * @param cardId
     * @param userName
     * @param mobile
     * @param pass -- 强制放行 , 此数据为true时强制放行不做验证
     * @return
     */
    public boolean verification(String bankNo , String cardId , String userName , String mobile , boolean pass , boolean updateBankData){
        //根据银行卡号查询数据库数据
        BankFourElementVerificationEntity bankFourElementVerification = bankFourElementVerificationService.findByBankNo(bankNo);
        if(bankFourElementVerification != null ){
            //数据库有存在数据进行数据验证
            //数据库存在成功数据
            if( BankFourElementVerificationEntity.VerificationStatus.success == bankFourElementVerification.getStatus()){
                if(cardId.equals(bankFourElementVerification.getCardId())  && userName.equals(bankFourElementVerification.getUserName()) && mobile.equals(bankFourElementVerification.getMobile())){
                    //验证通过
                    log.info("银行卡四要素验证成功 -- mo9" + bankNo +"|"+cardId+"|"+userName+"|"+mobile);
                    return true ;
                }else{
                    //验证失败
                    log.info("银行卡四要素验证失败 -- mo9" + bankNo +"|"+cardId+"|"+userName+"|"+mobile);
                    return false ;
                }
            }
            //根据数据库数据进行参数对比验证
            if( BankFourElementVerificationEntity.VerificationStatus.mobileError == bankFourElementVerification.getStatus()){
                //手机号错误 , 判断手机号不一样 , 其他信息保持不变 , 不满足直接返回失败
                if(mobile.equals(bankFourElementVerification.getMobile()) || !(cardId.equals(bankFourElementVerification.getCardId())) || !(userName.equals(bankFourElementVerification.getUserName())) ){
                    log.info("银行卡四要素验证失败 -- mo9" + bankNo +"|"+cardId+"|"+userName+"|"+mobile);
                    return false ;
                }
            }
            if( BankFourElementVerificationEntity.VerificationStatus.userNameError == bankFourElementVerification.getStatus()){
                //用户名错误 , 判断用户名不一样 , 其他信息保持不变 , 不满足直接返回失败
                if(userName.equals(bankFourElementVerification.getUserName()) || !(cardId.equals(bankFourElementVerification.getCardId())) || !(mobile.equals(bankFourElementVerification.getMobile())) ){
                    log.info("银行卡四要素验证失败 -- mo9" + bankNo +"|"+cardId+"|"+userName+"|"+mobile);
                    return false ;
                }
            }
            if( BankFourElementVerificationEntity.VerificationStatus.cardIdError == bankFourElementVerification.getStatus()){
                //身份证错误 , 判断身份证不一样 , 其他信息保持不变 , 不满足直接返回失败
                if(cardId.equals(bankFourElementVerification.getCardId()) || !(userName.equals(bankFourElementVerification.getUserName())) || !(mobile.equals(bankFourElementVerification.getMobile())) ){
                    log.info("银行卡四要素验证失败 -- mo9" + bankNo +"|"+cardId+"|"+userName+"|"+mobile);
                    return false ;
                }
            }
            //如果错误信息为failed , 表示验证错误信息不止一个
            if( BankFourElementVerificationEntity.VerificationStatus.failed == bankFourElementVerification.getStatus()){
                //根据remark数据判断数据错误信息
                String errorMsg = bankFourElementVerification.getErrorMsg() ;
                //判断包含的错误信息
                if(errorMsg.contains(BankFourElementVerificationEntity.VerificationStatus.mobileError.name()) && mobile.equals(bankFourElementVerification.getMobile())){
                    //手机号错误 , 判断手机号是否一样 , 一样返回false
                    log.info("银行卡四要素验证失败 -- mo9" + bankNo +"|"+cardId+"|"+userName+"|"+mobile);
                    return false ;
                }
                if(errorMsg.contains(BankFourElementVerificationEntity.VerificationStatus.userNameError.name()) && userName.equals(bankFourElementVerification.getUserName())){
                    //用户名错误 , 判断用户名是否一样 , 一样返回false
                    log.info("银行卡四要素验证失败 -- mo9" + bankNo +"|"+cardId+"|"+userName+"|"+mobile);
                    return false ;
                }
                if(errorMsg.contains(BankFourElementVerificationEntity.VerificationStatus.cardIdError.name()) && cardId.equals(bankFourElementVerification.getCardId())){
                    //身份证错误 , 判断身份证是否一样 , 一样返回false
                    log.info("银行卡四要素验证失败 -- mo9" + bankNo +"|"+cardId+"|"+userName+"|"+mobile);
                    return false ;
                }
            }
        }
        //如果数据库数据未匹配 ,或者数据库没有数据 , 进行易联四要素验证
        return yilianVerification(bankNo ,  cardId ,  userName ,  mobile , bankFourElementVerification);
    }

    /**
     * 易联四要素验证
     * @param bankNo
     * @param cardId
     * @param userName
     * @param mobile
     * @param bankFourElementVerification
     * @return
     */
    private boolean yilianVerification(String bankNo , String cardId , String userName , String mobile , BankFourElementVerificationEntity bankFourElementVerification){
        //判断数据库是否存在数据
        if(bankFourElementVerification == null){
            //数据库不存在数据 -- 新建
            bankFourElementVerification = new BankFourElementVerificationEntity();
            bankFourElementVerification.setCreateTime(new Date()) ;
        }
        bankFourElementVerification.setBankNo(bankNo);
        bankFourElementVerification.setCardId(cardId);
        bankFourElementVerification.setMobile(mobile);
        bankFourElementVerification.setUserName(userName);
        bankFourElementVerification.setUpdateTime(new Date()) ;

        //存储四要素信息
        bankFourElementVerificationService.save(bankFourElementVerification);
        return true ;
    }

}
