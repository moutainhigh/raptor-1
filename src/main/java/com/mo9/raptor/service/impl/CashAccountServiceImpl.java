package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.CashAccountEntity;
import com.mo9.raptor.entity.CashAccountLogEntity;
import com.mo9.raptor.enums.BalanceTypeEnum;
import com.mo9.raptor.enums.BusinessTypeEnum;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.lock.RedisService;
import com.mo9.raptor.repository.CashAccountLogRepository;
import com.mo9.raptor.repository.CashAccountRepository;
import com.mo9.raptor.service.CashAccountService;
import com.mo9.raptor.utils.CommonValues;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by xtgu on 2018/11/1.
 * @author xtgu  --- 此现金钱包不做独立事务
 */
@Service
@Transactional(rollbackFor = Exception.class )
public class CashAccountServiceImpl implements CashAccountService {

    private static Logger logger = Log.get();

    @Autowired
    private CashAccountRepository cashAccountRepository ;

    @Autowired
    private RedisService redisService ;

    @Autowired
    private CashAccountLogRepository cashAccountLogRepository ;


    @Override
    public ResCodeEnum repay(String userCode, BigDecimal amount , String businessNo) {
        String lockKey = CommonValues.CASH_ACCOUNT + userCode ;
        //锁定用户现金账户
        boolean lock = redisService.lock(lockKey , lockKey , 30*1000L , TimeUnit.MILLISECONDS);
        if(lock){
            try {
                return this.repayInside(userCode , amount , businessNo);
            } catch (Exception e) {
                Log.error(logger , new RuntimeException("用户还款锁定现金账户 获取锁异常") ,userCode + "用户还款锁定现金账户 获取锁异常 : " + businessNo);
                return ResCodeEnum.CASH_ACCOUNT_EXCEPTION ;
            }finally {
                //释放锁
                redisService.unlock(lockKey);
            }
        }else{
            Log.error(logger , new RuntimeException("用户还款锁定现金账户 获取锁异常") ,userCode + "用户还款锁定现金账户 获取锁异常 : " + businessNo);
            return ResCodeEnum.CASH_ACCOUNT_LOCK_FAILED ;
        }

    }

    /**
     * 还款内部方法
     * @param userCode
     * @param amount
     * @param businessNo
     * @return
     */
    private ResCodeEnum repayInside(String userCode, BigDecimal amount , String businessNo) {
        CashAccountEntity cashAccountEntity = this.findByUserCode(userCode);
        if(cashAccountEntity == null){
            cashAccountEntity = this.create(userCode);
        }
        CashAccountLogEntity cashAccountLogEntity = cashAccountLogRepository.findByBusinessNoAndBusinessType(businessNo , BusinessTypeEnum.REPAY);
        if(cashAccountLogEntity != null){
            logger.info("还款 " + businessNo + " 流水号已处理 ");
            return ResCodeEnum.CASH_ACCOUNT_BUSINESS_NO_IS_EXIST ;
        }
        BigDecimal beforeBalance = cashAccountEntity.getBalance() ;
        BigDecimal afterBalance = beforeBalance.add(amount) ;
        //进行数据操作
        cashAccountEntity.setBalance(afterBalance);
        cashAccountRepository.save(cashAccountEntity);
        //记录日志
        createLog(userCode , amount , beforeBalance , afterBalance , BalanceTypeEnum.IN , BusinessTypeEnum.REPAY , businessNo );
        return ResCodeEnum.SUCCESS;
    }


    @Override
    public ResCodeEnum entry(String userCode, BigDecimal amount , String businessNo) {
        String lockKey = CommonValues.CASH_ACCOUNT + userCode ;
        //锁定用户现金账户
        boolean lock = redisService.lock(lockKey , lockKey , 30*1000L , TimeUnit.MILLISECONDS);
        if(lock){
            try {
                return this.entryInside(userCode , amount , businessNo);
            } catch (Exception e) {
                Log.error(logger , new RuntimeException("用户入账锁定现金账户 获取锁异常") ,userCode + "用户入账锁定现金账户 获取锁异常 : " + businessNo);
                return ResCodeEnum.CASH_ACCOUNT_EXCEPTION ;
            }finally {
                //释放锁
                redisService.unlock(lockKey);
            }
        }else{
            Log.error(logger , new RuntimeException("用户入账锁定现金账户 获取锁异常") ,userCode + "用户入账锁定现金账户 获取锁异常 : " + businessNo);
            return ResCodeEnum.CASH_ACCOUNT_LOCK_FAILED ;
        }
    }


    /**
     * 入账内部方法
     * @param userCode
     * @param amount
     * @param businessNo
     * @return
     */
    private ResCodeEnum entryInside(String userCode, BigDecimal amount , String businessNo) {
        CashAccountEntity cashAccountEntity = this.findByUserCode(userCode);
        if(cashAccountEntity == null){
            cashAccountEntity = this.create(userCode);
        }
        CashAccountLogEntity cashAccountLogEntity = cashAccountLogRepository.findByBusinessNoAndBusinessType(businessNo , BusinessTypeEnum.ENTRY);
        if(cashAccountLogEntity != null){
            logger.info("还款 " + businessNo + " 流水号已处理 ");
            return ResCodeEnum.CASH_ACCOUNT_BUSINESS_NO_IS_EXIST ;
        }
        BigDecimal beforeBalance = cashAccountEntity.getBalance() ;
        BigDecimal afterBalance = beforeBalance.subtract(amount);
        if(afterBalance.compareTo(BigDecimal.ZERO) < 0){
            Log.error(logger , new RuntimeException("用户入账后可用金额小于零") ,userCode + "用户入账后可用金额小于零 : "
                    + businessNo + " 入账前金额 : " + beforeBalance + " 入账后金额 : " + afterBalance);
            return ResCodeEnum.CASH_ACCOUNT_BALANCE_LACK ;
        }
        //进行数据操作
        cashAccountEntity.setBalance(afterBalance);
        cashAccountRepository.save(cashAccountEntity);
        //记录日志
        createLog(userCode , amount , beforeBalance , afterBalance , BalanceTypeEnum.OUT , BusinessTypeEnum.ENTRY , businessNo );
        return ResCodeEnum.SUCCESS;
    }

    @Override
    public CashAccountEntity findByUserCode(String userCode) {
        return cashAccountRepository.findByUserCode(userCode);
    }

    private CashAccountEntity create(String userCode){
        CashAccountEntity cashAccountEntity = new CashAccountEntity() ;
        cashAccountEntity.setUserCode(userCode);
        cashAccountEntity.setBalance(BigDecimal.ZERO);
        Date date = new Date() ;
        cashAccountEntity.setCreateTime(date);
        cashAccountEntity.setUpdateTime(date);
        cashAccountEntity.setDeleted(false);
        return cashAccountRepository.save(cashAccountEntity);
    }

    private void createLog(String userCode , BigDecimal amount , BigDecimal beforeBalance , BigDecimal afterBalance ,
                                        BalanceTypeEnum balanceType , BusinessTypeEnum businessType , String businessNo){
        CashAccountLogEntity cashAccountLogEntity = new CashAccountLogEntity() ;
        Date date = new Date() ;
        cashAccountLogEntity.setUserCode(userCode);
        cashAccountLogEntity.setCreateTime(date);
        cashAccountLogEntity.setBalanceChange(amount);
        cashAccountLogEntity.setBeforeBalance(beforeBalance);
        cashAccountLogEntity.setAfterBalance(afterBalance);
        cashAccountLogEntity.setBalanceType(balanceType);
        cashAccountLogEntity.setBusinessType(businessType);
        cashAccountLogEntity.setBusinessNo(businessNo);
        cashAccountLogRepository.save(cashAccountLogEntity);
    }
}
