package com.mo9.raptor.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mo9.raptor.RaptorApplicationTest;
import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.repository.UserRepository;
import com.mo9.raptor.risk.repo.RiskCallLogRepository;
import com.mo9.raptor.risk.service.RiskAuditService;
import com.mo9.raptor.risk.service.RiskWordService;
import com.mo9.raptor.riskdb.repo.RiskThirdBlackListRepository;
import com.mo9.raptor.utils.MobileUtil;
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

    //@Resource
    private RiskAuditService riskAuditService;

    @Resource
    private RuleLogService ruleLogService;

    @Resource
    private RiskThirdBlackListRepository riskThirdBlackListRepository;

    //@Resource
    private RiskCallLogRepository riskCallLogRepository;

    @Resource
    private UserRepository userRepository;


    @Resource
    private RiskWordService riskWordService;

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

    @Test
    public void t2() {
        AuditResponseEvent audit = riskAuditService.audit("04AB246646B193A4EB82DC509CE942AA");
        System.out.println(audit);
    }

    @Test
    public void t3() {
        int b = userRepository.inBlackList("04AB246646B193A4EB82DC509CE942AA");
        System.out.println(b);
        int c = riskThirdBlackListRepository.isInBlackList("13120502501");
        System.out.println(c);
        int s = riskWordService.filter("{\"contact\":[{\"contact_mobile\":\"\",\"contact_name\":\"窝窝\"},{\"contact_mobile\":\"15950939281\",\"contact_name\":\"恒昌\"},{\"contact_mobile\":\"17621334147\",\"contact_name\":\"贷我飞\"},{\"contact_mobile\":\"18201943514\",\"contact_name\":\"宜信\"},{\"contact_mobile\":\"15952440191\",\"contact_name\":\"钱满仓\"},{\"contact_mobile\":\"15952440192\",\"contact_name\":\"有呗贝科技\"},{\"contact_mobile\":\"15952440193\",\"contact_name\":\"好贷\"},{\"contact_mobile\":\"15952440194\",\"contact_name\":\"钱万万\"},{\"contact_mobile\":\"15952440195\",\"contact_name\":\"陆金所\"},{\"contact_mobile\":\"15952440196\",\"contact_name\":\"开心钱包\"},{\"contact_mobile\":\"15952440196\",\"contact_name\":\"小花钱包\"},{\"contact_mobile\":\"15952440197\",\"contact_name\":\"钱马金融\"},{\"contact_mobile\":\"15952440197\",\"contact_name\":\"神灯小袋贷\"},{\"contact_mobile\":\"15952440198\",\"contact_name\":\"现金卡\"},{\"contact_mobile\":\"15952440199\",\"contact_name\":\"借了花\"},{\"contact_mobile\":\"15952440111\",\"contact_name\":\"钱急送\"},{\"contact_mobile\":\"15952440112\",\"contact_name\":\"99分期\"},{\"contact_mobile\":\"15952440113\",\"contact_name\":\"还呗\"},{\"contact_mobile\":\"15952440114\",\"contact_name\":\"啪啪钱包\"},{\"contact_mobile\":\"15952440115\",\"contact_name\":\"小葱钱包\"},{\"contact_mobile\":\"15952440115\",\"contact_name\":\"缺钱么\"},{\"contact_mobile\":\"15952440116\",\"contact_name\":\"如意贷\"},{\"contact_mobile\":\"15952440118\",\"contact_name\":\"有人贷\"},{\"contact_mobile\":\"15952440119\",\"contact_name\":\"马可金融\"},{\"contact_mobile\":\"15952440121\",\"contact_name\":\"小花仙\"},{\"contact_mobile\":\"15952440122\",\"contact_name\":\"小葱拌豆腐\"},{\"contact_mobile\":\"15952440123\",\"contact_name\":\"仙女\"},{\"contact_mobile\":\"15952440124\",\"contact_name\":\"找我\"},{\"contact_mobile\":\"15952440125\",\"contact_name\":\"发货\"},{\"contact_mobile\":\"15952440126\",\"contact_name\":\"大厦\"},{\"contact_mobile\":\"15952440126\",\"contact_name\":\"胡歌\"},{\"contact_mobile\":\"15952440127\",\"contact_name\":\"帅哥\"},{\"contact_mobile\":\"15952440129\",\"contact_name\":\"借财童子\"},{\"contact_mobile\":\"15952440789\",\"contact_name\":\"大家都好\"},{\"contact_mobile\":\"15952440777\",\"contact_name\":\"哦不\"},{\"contact_mobile\":\"15952440968\",\"contact_name\":\"我去\"},{\"contact_mobile\":\"18362011111\",\"contact_name\":\"张杰\"},{\"contact_mobile\":\"18362014725\",\"contact_name\":\"我去了\"},{\"contact_mobile\":\"18362056893\",\"contact_name\":\"是谁\"},{\"contact_mobile\":\"18362012358\",\"contact_name\":\"同乐\"},{\"contact_mobile\":\"15952440985\",\"contact_name\":\"张艺兴\"},{\"contact_mobile\":\"15952440136\",\"contact_name\":\"我卡\"},{\"contact_mobile\":\"18362077777\",\"contact_name\":\"实话\"},{\"contact_mobile\":\"18362500000\",\"contact_name\":\"事业\"},{\"contact_mobile\":\"18362500000\",\"contact_name\":\"17317514440\"},{\"contact_mobile\":\"13787303194\",\"contact_name\":\"wp\"},{\"contact_mobile\":\"15073000648\",\"contact_name\":\"mm\"}],\"countryName\":\"CN\",\"buyerMobile\":\"17621334147\"}");
        System.out.println(s);
    }

    @Test
    public void  t4(){
        String json = "{\"contact\":[{\"contact_mobile\":\"\",\"contact_name\":\"窝窝\"},{\"contact_mobile\":\"15950939281\",\"contact_name\":\"恒昌\"},{\"contact_mobile\":\"17621334147\",\"contact_name\":\"贷我飞\"},{\"contact_mobile\":\"18201943514\",\"contact_name\":\"宜信\"},{\"contact_mobile\":\"15952440191\",\"contact_name\":\"钱满仓\"},{\"contact_mobile\":\"15952440192\",\"contact_name\":\"有呗贝科技\"},{\"contact_mobile\":\"15952440193\",\"contact_name\":\"好贷\"},{\"contact_mobile\":\"15952440194\",\"contact_name\":\"钱万万\"},{\"contact_mobile\":\"15952440195\",\"contact_name\":\"陆金所\"},{\"contact_mobile\":\"15952440196\",\"contact_name\":\"开心钱包\"},{\"contact_mobile\":\"15952440196\",\"contact_name\":\"小花钱包\"},{\"contact_mobile\":\"15952440197\",\"contact_name\":\"钱马金融\"},{\"contact_mobile\":\"15952440197\",\"contact_name\":\"神灯小袋贷\"},{\"contact_mobile\":\"15952440198\",\"contact_name\":\"现金卡\"},{\"contact_mobile\":\"15952440199\",\"contact_name\":\"借了花\"},{\"contact_mobile\":\"15952440111\",\"contact_name\":\"钱急送\"},{\"contact_mobile\":\"15952440112\",\"contact_name\":\"99分期\"},{\"contact_mobile\":\"15952440113\",\"contact_name\":\"还呗\"},{\"contact_mobile\":\"15952440114\",\"contact_name\":\"啪啪钱包\"},{\"contact_mobile\":\"15952440115\",\"contact_name\":\"小葱钱包\"},{\"contact_mobile\":\"15952440115\",\"contact_name\":\"缺钱么\"},{\"contact_mobile\":\"15952440116\",\"contact_name\":\"如意贷\"},{\"contact_mobile\":\"15952440118\",\"contact_name\":\"有人贷\"},{\"contact_mobile\":\"15952440119\",\"contact_name\":\"马可金融\"},{\"contact_mobile\":\"15952440121\",\"contact_name\":\"小花仙\"},{\"contact_mobile\":\"15952440122\",\"contact_name\":\"小葱拌豆腐\"},{\"contact_mobile\":\"15952440123\",\"contact_name\":\"仙女\"},{\"contact_mobile\":\"15952440124\",\"contact_name\":\"找我\"},{\"contact_mobile\":\"15952440125\",\"contact_name\":\"发货\"},{\"contact_mobile\":\"15952440126\",\"contact_name\":\"大厦\"},{\"contact_mobile\":\"15952440126\",\"contact_name\":\"胡歌\"},{\"contact_mobile\":\"15952440127\",\"contact_name\":\"帅哥\"},{\"contact_mobile\":\"15952440129\",\"contact_name\":\"借财童子\"},{\"contact_mobile\":\"15952440789\",\"contact_name\":\"大家都好\"},{\"contact_mobile\":\"15952440777\",\"contact_name\":\"哦不\"},{\"contact_mobile\":\"15952440968\",\"contact_name\":\"我去\"},{\"contact_mobile\":\"18362011111\",\"contact_name\":\"张杰\"},{\"contact_mobile\":\"18362014725\",\"contact_name\":\"我去了\"},{\"contact_mobile\":\"18362056893\",\"contact_name\":\"是谁\"},{\"contact_mobile\":\"18362012358\",\"contact_name\":\"同乐\"},{\"contact_mobile\":\"15952440985\",\"contact_name\":\"张艺兴\"},{\"contact_mobile\":\"15952440136\",\"contact_name\":\"我卡\"},{\"contact_mobile\":\"18362077777\",\"contact_name\":\"实话\"},{\"contact_mobile\":\"18362500000\",\"contact_name\":\"事业\"},{\"contact_mobile\":\"18362500000\",\"contact_name\":\"17317514440\"},{\"contact_mobile\":\"13787303194\",\"contact_name\":\"wp\"},{\"contact_mobile\":\"15073000648\",\"contact_name\":\"mm\"}],\"countryName\":\"CN\",\"buyerMobile\":\"17621334147\"}";
        JSONArray jsonArray;
        //有2种JSON格式...
        if (json.startsWith("{")) {
            jsonArray = JSON.parseObject(json).getJSONArray("contact");
        } else {
            jsonArray = JSON.parseArray(json);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < jsonArray.size(); i++) {
            String name = jsonArray.getJSONObject(i).getString("contact_name");
            stringBuilder.append(name + "|");
        }
        System.out.println(stringBuilder.toString());
        int hitCount = riskWordService.filter(stringBuilder.toString());
        System.out.println(hitCount);
    }

}
