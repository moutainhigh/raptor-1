package com.mo9.raptor.service;

import com.mo9.raptor.RaptorApplicationTest;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.risk.repo.RiskContractInfoRepository;
import com.mo9.raptor.risk.service.RiskContractInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jyou on 2018/10/18.
 *
 * @author jyou
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RaptorApplicationTest.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RiskContractInfoServiceTest {

    private String data = "{\"contact\":[{\"contact_mobile\":\"911\",\"contact_name\":\"面试  大学\"}],\"countryName\":\"CN\",\"buyerMobile\":\"18883968542\"}";

    @Resource
    private RiskContractInfoService riskContractInfoService;

    @Resource
    private RiskContractInfoRepository riskContractInfoRepository;

    @Test
    public void testCreateAll(){
        UserEntity userEntity = new UserEntity();
        userEntity.setMobile("13213173517");
        userEntity.setUserCode("123456");
        riskContractInfoService.createAll(data, userEntity);
    }

    @Test
    public void testUpdateMatchingMobile(){
        String userCode = "123456";
        Set<String> set = new HashSet<>();
        set.add("15703034772");
        set.add("110");
        set.add("112");
        List<String> list = new ArrayList<>(set );
        riskContractInfoRepository.updateMatchingMobile(userCode, list);
    }
}
