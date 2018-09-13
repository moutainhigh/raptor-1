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
    BankEntity findByBankNo(String bankNo);

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
     * 创建
     * @param bankNo
     * @param cardId
     * @param userName
     * @param mobile
     * @param type
     */
    public void create(String bankNo , String cardId , String userName , String mobile , BankEntity.Type type) ;
}
