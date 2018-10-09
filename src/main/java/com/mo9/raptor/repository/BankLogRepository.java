package com.mo9.raptor.repository;

import com.mo9.raptor.entity.BankLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by xtgu on 2018/9/20.
 * @author xtgu
 */
public interface BankLogRepository extends JpaRepository<BankLogEntity,Long> {


    /**
     * 查询银行卡错误日志
     * @param mobile
     * @param bankNo
     * @param cardId
     * @param userName
     * @param status
     * @return
     */
    @Query(value = "select t from BankLogEntity t where t.mobile = ?1 and t.bankNo = ?2 and t.cardId = ?3 and t.userName = ?4 and t.status = ?5 ")
    List<BankLogEntity> findByMobileAndBankNoAndIdCardAndUserNameAndStatus(String mobile, String bankNo, String cardId, String userName, String status);
}
