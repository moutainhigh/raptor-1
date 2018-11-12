package com.mo9.raptor.service.impl;

import com.mo9.raptor.bean.condition.CashAccountLogCondition;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    public ResCodeEnum recharge(String userCode, BigDecimal amount , String businessNo, BusinessTypeEnum businessTypeEnum) {
        String lockKey = CommonValues.CASH_ACCOUNT + userCode ;
        //锁定用户现金账户
        boolean lock = redisService.lock(lockKey , lockKey , 30*1000L , TimeUnit.MILLISECONDS);
        if(lock){
            try {
                return this.repayInside(userCode , amount , businessNo , businessTypeEnum);
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
    private ResCodeEnum repayInside(String userCode, BigDecimal amount , String businessNo , BusinessTypeEnum businessTypeEnum) {
        CashAccountEntity cashAccountEntity = this.findByUserCode(userCode);
        if(cashAccountEntity == null){
            cashAccountEntity = this.create(userCode);
        }
        CashAccountLogEntity cashAccountLogEntity = cashAccountLogRepository.findByBusinessNoAndBusinessTypeAndBalanceType(businessNo , businessTypeEnum , BalanceTypeEnum.IN);
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
        createLog(userCode , amount , beforeBalance , afterBalance , BalanceTypeEnum.IN , businessTypeEnum , businessNo );
        return ResCodeEnum.SUCCESS;
    }


    @Override
    public ResCodeEnum entry(String userCode, BigDecimal amount , String businessNo, BusinessTypeEnum businessTypeEnum) {
        String lockKey = CommonValues.CASH_ACCOUNT + userCode ;
        //锁定用户现金账户
        boolean lock = redisService.lock(lockKey , lockKey , 30*1000L , TimeUnit.MILLISECONDS);
        if(lock){
            try {
                return this.entryInside(userCode , amount , businessNo , businessTypeEnum);
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
    private ResCodeEnum entryInside(String userCode, BigDecimal amount , String businessNo, BusinessTypeEnum businessTypeEnum) {
        CashAccountEntity cashAccountEntity = this.findByUserCode(userCode);
        if(cashAccountEntity == null){
            cashAccountEntity = this.create(userCode);
        }
        CashAccountLogEntity cashAccountLogEntity = cashAccountLogRepository.findByBusinessNoAndBusinessTypeAndBalanceType(businessNo , businessTypeEnum,BalanceTypeEnum.OUT);
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
        createLog(userCode , amount , beforeBalance , afterBalance , BalanceTypeEnum.OUT , businessTypeEnum , businessNo );
        return ResCodeEnum.SUCCESS;
    }

    @Override
    public CashAccountEntity findByUserCode(String userCode) {
        return cashAccountRepository.findByUserCode(userCode);
    }

    @Override
    public Page<CashAccountLogEntity> findLogByCondition(final CashAccountLogCondition condition) {
        //规格定义
        Specification<CashAccountLogEntity> specification = new Specification<CashAccountLogEntity>() {
            @Override
            public Predicate toPredicate(Root<CashAccountLogEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>(10);
                list.add(cb.equal(root.get("userCode").as(String.class),condition.getUserCode()));

                if(condition.getFromDate() != null){
                    list.add(cb.greaterThanOrEqualTo(root.get("createTime").as(Date.class), condition.getFromDate()));
                }
                if(condition.getToDate() != null){
                    list.add(cb.lessThanOrEqualTo(root.get("createTime").as(Date.class) , condition.getToDate()));
                }
                Predicate inBusinessType = null ;
                if(condition.getInType() != null && condition.getInType().size() > 0){
                    List<BusinessTypeEnum> inTypes = condition.getInType() ;
                    CriteriaBuilder.In<BusinessTypeEnum> in = cb.in(root.get("businessType").as(BusinessTypeEnum.class));
                    for (BusinessTypeEnum type : inTypes) {
                        in.value(type);
                    }
                    inBusinessType = cb.and(in , cb.equal(root.get("balanceType").as(BalanceTypeEnum.class),BalanceTypeEnum.IN));
                }

                Predicate outBusinessType = null ;
                if(condition.getOutType() != null && condition.getOutType().size() > 0){
                    List<BusinessTypeEnum> inTypes = condition.getInType() ;
                    CriteriaBuilder.In<BusinessTypeEnum> out = cb.in(root.get("businessType").as(BusinessTypeEnum.class));
                    for (BusinessTypeEnum type : inTypes) {
                        out.value(type);
                    }
                    outBusinessType = cb.and(out , cb.equal(root.get("balanceType").as(BalanceTypeEnum.class),BalanceTypeEnum.OUT));
                }
                if(inBusinessType != null && outBusinessType != null ){
                    //同时不为null
                    list.add(cb.or(inBusinessType , outBusinessType));
                }else if(inBusinessType != null){
                    list.add(inBusinessType);
                }else if(outBusinessType != null){
                    list.add(outBusinessType);
                }

                Predicate[] predicates = new Predicate[list.size()];
                predicates = list.toArray(predicates);
                return cb.and(predicates);
            }
        };
        //分页信息
        Pageable pageable = PageRequest.of(condition.getPageNumber()-1, condition.getPageSize(), Sort.Direction.DESC, "id");
        //查询
        return cashAccountLogRepository.findAll(specification , pageable);
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
