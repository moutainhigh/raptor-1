package com.mo9.raptor.service;

import com.mo9.raptor.entity.BankEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.ResCodeEnum;

/**
 * Created by xtgu on 2018/9/12.
 * @author xtgu
 */
public interface BankService {

    /**
     * 根据手机号 和 类型查询最后一次银行卡号
     * @param mobile
     * @return
     */
    BankEntity findByMobileLastOne(String mobile);


    /**
     * 根据用户 和 类型查询最后一次银行卡号
     * @param userCode
     * @return
     */
    BankEntity findByUserCodeLastOne(String userCode);

    /**
     * 根据银行卡号查询
     * @param bankNo
     * @return
     */
    BankEntity findByBankNo(String bankNo);

    /**
     * 验证四要素
     * @param bankNo
     * @param mobile
     * @param bankName
     * @param userEntity
     * @return
     */
    public ResCodeEnum verify(String bankNo, String mobile, String bankName, UserEntity userEntity);

    /**
     * 创建或者修改银行卡信息
     * @param bankNo
     * @param cardId
     * @param userName
     * @param mobile
     * @param channel
     * @param bankName
     * @param userName
     */
    public void createOrUpdateBank(String bankNo , String cardId , String userName , String mobile, String channel , String bankName , String userCode);

}
