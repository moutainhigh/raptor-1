package com.mo9.raptor.service.impl;

import com.mo9.raptor.bean.condition.StrategyCondition;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.service.StrategyService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Created by jyou on 2018/10/6.
 *
 * @author jyou
 */
@Service
public class StrategyServiceImpl implements StrategyService {

    private Logger logger = LoggerFactory.getLogger(StrategyServiceImpl.class);

    @Override
    public ResCodeEnum loanOrderStrategy(StrategyCondition strategyCondition) {
        ResCodeEnum resCodeEnum = ResCodeEnum.SUCCESS;
        if(strategyCondition.isLoanBankSupport()){
            resCodeEnum = ckeckLoanBank(strategyCondition.getCondition().getString(StrategyCondition.BANK_NAME_CONDITION));
        }
        return resCodeEnum;
    }

    /**
     * 校验银行卡列表是否支持
     * @param bankName
     * @return
     */
    private ResCodeEnum ckeckLoanBank(String bankName){
        if(StringUtils.isBlank(bankName)){
            return ResCodeEnum.BANK_VERIFY_ERROR;
        }
        String[] supportBank = StrategyCondition.SUPPORT_BANK;
        boolean contains = Arrays.asList(supportBank).contains(bankName);
        if(!contains){
            logger.warn("校验银行卡是否支持-->返回结果不支持bank={}", bankName);
            return ResCodeEnum.LOAN_BANK_LIST_NOT_SUPPORT;
        }
        return ResCodeEnum.SUCCESS;
    }
}
