package com.mo9.raptor.service;

import com.mo9.raptor.RaptorApplicationTest;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.repository.UserRepository;
import com.mo9.raptor.risk.repo.RiskCallLogRepository;
import com.mo9.raptor.risk.service.RiskAuditService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RaptorApplicationTest.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuditServiceTest {

    @Resource
    private RiskAuditService riskAuditService;

    @Resource
    private RuleLogService ruleLogService;

    @Resource
    private RiskCallLogRepository riskCallLogRepository;

    @Resource
    private UserRepository userRepository;
    @Test
    public void test() {
        //System.out.println(((RiskAuditServiceImpl)riskAuditService).callLogRule());
        //System.out.println(((RiskAuditServiceImpl)riskAuditService).callLogRule());
        //ruleLogService.create("test", "IdPicCompareRule", null, false, "");
        Integer callLogCountAfterTimestamp = riskCallLogRepository.getCallLogCountAfterTimestamp("18616297271", 123L);
        ////int count = callLogCountAfterTimestamp
        System.out.println(callLogCountAfterTimestamp);

        List<UserEntity> auditing = userRepository.findByStatus("PASSED");
        System.out.println(auditing.size());
    }

}
