package com.mo9.raptor.service;

import com.mo9.raptor.RaptorApplicationTest;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.risk.app.entity.User;
import com.mo9.risk.repo.RiskContractInfoRepository;
import com.mo9.risk.service.RiskContractInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
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

    private String data = "[\n" +
            "  {\n" +
            "    \"contact_mobile\" : \"(415) 5553695\",\n" +
            "    \"contact_name\" : \"BellKate\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"contact_mobile\" : \"(408) 5553514\",\n" +
            "    \"contact_name\" : \"HigginsDaniel\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"contact_mobile\" : \"13899999999\",\n" +
            "    \"contact_name\" : \"AppleseedJohn\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"contact_mobile\" : \"13899999999\",\n" +
            "    \"contact_name\" : \"HaroAnna\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"contact_mobile\" : \"(707) 5551854\",\n" +
            "    \"contact_name\" : \"ZakroffHank\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"contact_mobile\" : \"5556106679\",\n" +
            "    \"contact_name\" : \"TaylorDavid\"\n" +
            "  }\n" +
            "]";

    @Resource
    private RiskContractInfoService riskContractInfoService;

    @Resource
    private RiskContractInfoRepository riskContractInfoRepository;

    @Test
    public void testCreateAll(){
        UserEntity userEntity = new UserEntity();
        userEntity.setMobile("13213173517");
        userEntity.setUserCode("DD73904B9D39FD45CD7AC4E54F9576A8");
        User user = new User();
        BeanUtils.copyProperties(userEntity, user);
        riskContractInfoService.createAll(data, user.getUserCode(), user.getMobile());
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
