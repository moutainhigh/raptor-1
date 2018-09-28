package com.mo9.raptor.repository;

import com.mo9.raptor.bean.vo.CommonUserInfo;
import com.mo9.raptor.entity.DictDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by xtgu on 2018/9/25.
 */
public interface CommonRepository extends JpaRepository<DictDataEntity,Long> {

    /**
     * 查询用户相关信息
     * @param nowStr
     * @return
     */
    @Query(value = "select \n" +
            "(select count(*)  from t_raptor_user) as 'userNumber' ,\n" +
            "(SELECT count(*)  from t_raptor_user where DATE(FROM_UNIXTIME(last_login_time/1000)) = CURDATE() )  as 'userLoginNumber',\n" +
            "(select count(*)  from t_raptor_user where certify_info = 1) as 'userCardNumber' ,  \n" +
            "(select count(*)  from t_raptor_user where mobile_contacts = 1) as 'userPhoneNumber' ,  \n" +
            "(select count(*)  from t_raptor_user where call_history = 1) as 'userCallHistoryNumber',\n" +
            "(select count(*)  from t_raptor_user where bank_card_set = 1) as 'userBankNumber' ", nativeQuery = true)
    Map<String , Integer> findUserInfo(String nowStr);

    /**
     * 查询放款相关数据
     * @param nowStr
     * @return
     */
    @Query(value = "select \n" +
            "(select name  from t_raptor_dict_data where dict_data_no = date(CURDATE()) ) as 'maxAmount' ,\n" +
            "(select count(*)  from t_raptor_loan_order where `status` in ('LENT' , 'PAYOFF')  and DATE(FROM_UNIXTIME(create_time/1000)) = CURDATE() ) as 'loanNumber' ,\n" +
            "(select SUM(lent_number)  from t_raptor_loan_order where `status` in ('LENT' , 'PAYOFF') and DATE(FROM_UNIXTIME(create_time/1000)) = CURDATE() ) as 'loanAmount' ;", nativeQuery = true)
    Map<String,Integer> findLoanInfo(String nowStr);

    /**
     * 查询还款相关信息
     * @param nowStr
     * @return
     */
    @Query(value = "select \n" +
            "(select COUNT(*)  from t_raptor_pay_order where `status` = 'ENTRY_DONE' and type in ( 'REPAY_AS_PLAN' , 'REPAY_IN_ADVANCE' , 'REPAY_OVERDUE' ) and DATE(FROM_UNIXTIME(create_time/1000)) = CURDATE() ) as 'repayNumber',\n" +
            "(select SUM(apply_number) from t_raptor_pay_order where `status` = 'ENTRY_DONE' and type in ( 'REPAY_AS_PLAN' , 'REPAY_IN_ADVANCE' , 'REPAY_OVERDUE' ) and DATE(FROM_UNIXTIME(create_time/1000)) = CURDATE() ) as 'repayAmount',\n" +
            "(select COUNT(*)  from t_raptor_pay_order where `status` = 'ENTRY_DONE' and type = 'REPAY_POSTPONE' and DATE(FROM_UNIXTIME(create_time/1000)) = CURDATE() ) as 'postponeNumber',\n" +
            "(select  SUM(apply_number) from t_raptor_pay_order where `status` = 'ENTRY_DONE' and type = 'REPAY_POSTPONE' and DATE(FROM_UNIXTIME(create_time/1000)) = CURDATE() ) as 'postponeAmount',\n" +
            "(select count(*)  from t_raptor_loan_order where `status` = 'LENT' and DATE(FROM_UNIXTIME(repayment_date/1000)) < CURDATE()  ) as 'overdueNumber'", nativeQuery = true)
    Map<String,Integer> findRepayInfo(Long nowStr);
}
