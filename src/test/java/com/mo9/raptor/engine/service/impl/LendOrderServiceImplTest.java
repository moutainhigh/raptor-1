package com.mo9.raptor.engine.service.impl;

import com.mo9.raptor.RaptorApplicationTest;
import com.mo9.raptor.engine.service.ILendOrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.junit.Assert.*;

@EnableAspectJAutoProxy
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RaptorApplicationTest.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LendOrderServiceImplTest {

    @Autowired
    private ILendOrderService lendOrderService;

    @Test
    public void getByOrderId() {
    }

    @Test
    public void save() {
    }

    @Test
    public void getDailyLendAmount() {
        BigDecimal dailyLendAmount = lendOrderService.getDailyLendAmount();
        System.out.println(dailyLendAmount.toPlainString());
    }
}