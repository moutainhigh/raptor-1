package com.mo9.raptor.service;

import com.mo9.raptor.entity.BankEntity;
import com.mo9.raptor.enums.ResCodeEnum;

/**
 * Created by xtgu on 2018/9/12.
 * @author xtgu
 */
public interface BankService {

    /**
     * 根据银行卡号查询
     * @param bankNo
     * @return
     */
    BankEntity findByBankNoByLoan(String bankNo);

    /**
     * 验证四要素
     * @param bankNo
     * @param cardId
     * @param userName
     * @param mobile
     * @return
     */
    public ResCodeEnum verify(String bankNo , String cardId , String userName , String mobile);

    /**
     * 创建还款渠道
     * @param bankNo
     * @param cardId
     * @param userName
     * @param mobile
     * @param channel
     * @param bankName
     */
    public void createRepayBank(String bankNo , String cardId , String userName , String mobile, String channel , String bankName);

    /**
     * 根据银行卡 , 类型 , 渠道查询
     * @param bankNo
     * @param type
     * @param channel
     * @return
     */
    BankEntity findByBankNoAndTypeAndChannel(String bankNo, BankEntity.Type type, String channel);
}
