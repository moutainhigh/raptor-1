package com.mo9.raptor.service;

import com.mo9.raptor.bean.req.BankReq;
import com.mo9.raptor.entity.BankEntity;
import com.mo9.raptor.entity.BankLogEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.ResCodeEnum;

import java.util.List;

/**
 * Created by xtgu on 2018/9/12.
 * @author xtgu
 */
public interface BankLogService {

    /**
     * 存储
     * @param bankNo
     * @param cardId
     * @param userName
     * @param mobile
     * @param bankName
     * @param userCode
     * @param cardStartCount
     * @param cardSuccessCount
     * @param cardFailCount
     * @param status
     */
    void create(String bankNo , String cardId , String userName , String mobile , String bankName , String userCode ,
                Integer cardStartCount , Integer cardSuccessCount , Integer cardFailCount , String status);

    /**
     *查询银行卡错误日志
     * @param mobile
     * @param bankNo
     * @param cardId
     * @param userName
     * @param failed
     * @return
     */
    List<BankLogEntity> findByMobileAndBankNoAndIdCardAndUserNameAndStatus(String mobile, String bankNo, String cardId, String userName, String failed);
}
