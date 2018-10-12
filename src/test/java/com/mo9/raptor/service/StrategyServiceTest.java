package com.mo9.raptor.service;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.RaptorApplicationTest;
import com.mo9.raptor.bean.condition.StrategyCondition;
import com.mo9.raptor.enums.ResCodeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * Created by jyou on 2018/10/6.
 *
 * @author jyou
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RaptorApplicationTest.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StrategyServiceTest {

    @Resource
    private StrategyService strategyService;

    @Test
    public void test(){
        //检查银行卡是否支持
        StrategyCondition condition = new StrategyCondition(true);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(StrategyCondition.BANK_NAME_CONDITION, "中国工商银行");
        condition.setCondition(jsonObject);
        ResCodeEnum resCodeEnum = strategyService.loanOrderStrategy(condition);
        System.out.println(resCodeEnum);
    }


}
