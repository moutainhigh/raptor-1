package com.mo9.raptor.repository;

import com.mo9.raptor.entity.BankEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by xtgu on 2018/9/12.
 * @author xtgu
 */
public interface BankRepository extends JpaRepository<BankEntity,Long> {

    /**
     * 根据银行卡查询 放款银行卡
     * @param bankNo
     * @return
     */
    @Query(value = "select t from BankEntity t where t.bankNo = ?1 ")
    BankEntity findByBankNo(String bankNo);

    /**
     * 根据手机号查询
     * @param mobile
     * @return
     */
    @Query(value = "select t from BankEntity t where t.mobile = ?1 order by t.updateTime")
    List<BankEntity> findByMobile(String mobile);

    /**
     * 根据用户 和 类型查询最后一次银行卡号
     * @param userCode
     * @return
     */
    @Query(value = "select * from t_raptor_bank where user_code = ?1  ORDER BY update_time DESC limit 1", nativeQuery = true)
    BankEntity findByUserCodeLastOne(String userCode);

    /**
     * 根据用户查询银行卡类表
     * @param userCode
     * @return
     */
    @Query(value = "select * from t_raptor_bank where user_code = ?1  ORDER BY update_time DESC ", nativeQuery = true)
    List<BankEntity> findByUserCode(String userCode);
}
