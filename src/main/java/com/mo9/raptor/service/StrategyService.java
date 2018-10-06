package com.mo9.raptor.service;

import com.mo9.raptor.bean.condition.StrategyCondition;
import com.mo9.raptor.enums.ResCodeEnum;

/**
 * Created by jyou on 2018/10/6.
 *
 * @author jyou
 *
 * 策略服务service
 */
public interface StrategyService {

    ResCodeEnum loanOrderStrategy(StrategyCondition strategyCondition);
}
