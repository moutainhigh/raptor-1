package com.mo9.raptor.service;

import com.mo9.raptor.RaptorApplicationTest;
import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;
import com.mo9.risk.bean.AuditResponse;
import com.mo9.risk.service.RiskAuditService;
import com.mo9.risk.service.RiskRuleEngineService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @author zma
 * @date 2018/10/30
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RaptorApplicationTest.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FastLoanRuleTest {

    @Resource
    private RiskAuditService riskAuditService;
    
    @Resource
    private RiskRuleEngineService riskRuleEngineService;

    /**
     * ip规则 用户ip范围在：[广西南宁，福建莆田，山东潍坊，甘肃酒泉，广东汕尾]，即拒绝
     */
    String ipCheckRule = "ipCheckRule";
    /**
     * 失信列表规则 姓名和脱敏身份证在失信表完全匹配到，即拒绝
     */
    String shixinCheckRule = "shixinCheckRule";
    /**
     * 黑名单规则 本人江湖救急黑名单
     */
    String blackListRule = "blackListRule";
    /**
     * 通讯录风险词 大于14次匹配
     */
    String riskWordRule = "riskWordRule";
    /**
     * 身份证省份规则   新疆、西藏、内蒙古拒绝
     */
    String idCardRule = "idCardRule";
    /**
     * 年龄规则  45以上、18以下拒绝
     */
    String ageRule = "ageRule";
    /**
     * 通讯录规则  需要大于15个联系人，小于1000个联系人，且通话记录中跟通讯录联系人至少有过3次以上通话
     */
    String contactsRule = "contactsRule";
    /**
     * 通话记录次数规则  180天需要小于100次
     */
    String callLogRule = "callLogRule";
    /**
     * 运营商3要素
     */
    String threeElementCheck = "threeElementCheck";
    /**
     * 照片防hack规则
     */
    String antiHackRule = "antiHackRule";
    /**
     * 活体照公安照对比 小于0.7拒绝
     */
    String livePicCompareRule = "livePicCompareRule";
    /**
     * 身份证公安照对比 小于0.7拒绝
     */
    String idPicCompareRule ="idPicCompareRule";
    /**
     * 第三方黑名单检查
     */
    String blaceExecute = "blaceExecute";
    /**
     * 紧急联系人都没有出现在通话记录中
     */
    String mergencyCallTimesRule = "mergencyCallTimesRule";
    /**
     * 紧急联系人有无未完成订单
     */
    String mergencyHadNoDoneOrderRule = "mergencyHadNoDoneOrderRule";
    /**
     * 手机号被同一家贷款机构呼叫次数  12次拒绝
     */
    String calledTimesByOneLoanCompanyRule = "calledTimesByOneLoanCompanyRule";
    /**
     * 手机号被不同贷款机构呼叫次数  20次拒绝
     */
    String calledTimesByDifferentLoanCompanyRule = "calledTimesByDifferentLoanCompanyRule";
    /**
     * 紧急联系人有没有命中江湖救急黑名单
     */
    String mergencyInJHJJBlackListRule = "mergencyInJHJJBlackListRule";
    /**
     * 入网时间规则 小于150天拒绝
     */
    String openDateRule = "openDateRule";

    String userCode = "AA20A480E526D644D13D9AC5593D2681";

    /**
     * 测试 单个规则
     * @throws Exception
     */
    @Test
    public void fastRuleTest() throws Exception {
        String methodName = shixinCheckRule;
        executeMethod(riskAuditService,methodName, userCode);
    }

    /**
     * 测试多个规则
     * @throws Exception
     */
    @Test
    public void fastRuleAllTest() throws Exception {
        executeMethod(riskAuditService,ipCheckRule, userCode);
        executeMethod(riskAuditService,shixinCheckRule, userCode);
        executeMethod(riskAuditService,blackListRule, userCode);
        executeMethod(riskAuditService,riskWordRule, userCode);
        executeMethod(riskRuleEngineService,mergencyCallTimesRule, userCode);
        executeMethod(riskRuleEngineService,mergencyHadNoDoneOrderRule, userCode);
        executeMethod(riskRuleEngineService,calledTimesByOneLoanCompanyRule, userCode);
        executeMethod(riskRuleEngineService,calledTimesByDifferentLoanCompanyRule, userCode);
        executeMethod(riskRuleEngineService,mergencyInJHJJBlackListRule, userCode);
        executeMethod(riskRuleEngineService,openDateRule, userCode);
        executeMethod(riskAuditService,idCardRule, userCode);
        executeMethod(riskAuditService,ageRule, userCode);
        executeMethod(riskAuditService,contactsRule, userCode);
        executeMethod(riskAuditService,callLogRule, userCode);
        executeMethod(riskAuditService,threeElementCheck, userCode);
        executeMethod(riskAuditService,antiHackRule, userCode);
        executeMethod(riskAuditService,livePicCompareRule, userCode);
        executeMethod(riskAuditService,idPicCompareRule, userCode);
        executeMethod(riskAuditService,blaceExecute, userCode);
    }

    @Test
    public void riskRuleEngineTest() throws Exception {
        executeMethod(riskRuleEngineService,mergencyCallTimesRule, userCode);
        executeMethod(riskRuleEngineService,mergencyHadNoDoneOrderRule, userCode);
        executeMethod(riskRuleEngineService,calledTimesByOneLoanCompanyRule, userCode);
        executeMethod(riskRuleEngineService,calledTimesByDifferentLoanCompanyRule, userCode);
        executeMethod(riskRuleEngineService,mergencyInJHJJBlackListRule, userCode);
        executeMethod(riskRuleEngineService,openDateRule, userCode);
    }

    /**
     * 测试全部审核流程
     */
    @Test
    public void auditTest(){
        AuditResponse audit = riskAuditService.audit(userCode);
        System.out.println(audit);
    }

    /**
     * RiskAuditServiceImpl 内部规则测试
     * @param object 当前方法所属对象
     * @param methodName 方法名称
     * @param var1 方法所需参数
     * @return
     * @throws Exception
     */
    private AuditResponseEvent executeMethod(Object object,String methodName, Object... var1) throws Exception {
        Method method = object.getClass().getDeclaredMethod(methodName, String.class);
        method.setAccessible(true);
        AuditResponseEvent invoke = (AuditResponseEvent) method.invoke(riskAuditService, var1);
        Assert.assertNotNull(invoke);
        System.err.println("规则：" + methodName + "是否审核通过:" + invoke.isPass() + "，描述：" + invoke.getExplanation());
        return invoke;
    }
}
