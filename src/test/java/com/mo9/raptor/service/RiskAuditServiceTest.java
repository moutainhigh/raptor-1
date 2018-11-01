package com.mo9.raptor.service;

import com.mo9.raptor.RaptorApplicationTest;
import com.mo9.raptor.risk.service.RiskAuditService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * Created by jyou on 2018/10/22.
 *
 * @author jyou
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RaptorApplicationTest.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RiskAuditServiceTest {

    @Resource
    private RiskAuditService riskAuditService;

    String userCode = "BD1BB9682167BD335624E4E8153BECED";

    @Test
    public void test(){
        riskAuditService.audit(userCode);
    }

}
