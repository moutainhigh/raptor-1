package com.mo9.raptor.controller;

import com.mo9.raptor.bean.MessageVariable;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.RenewableDaysEnum;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.ModelUtils;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


/**
 * 读取服务协议
 *
 * @author zma
 * @date 2018/9/19
 */
@Controller
@RequestMapping("/agreement")
public class AgreementController {

    private static Logger logger = Log.get();

    private static final String DATE_FORMAT = "yyyy年MM月dd日";

    private static final String COMPANY_STAMP = "";
    /**
     *借款服务费占总金额百分比 不需要百分号，暂时固定
     */
    private static final String  SERVICE_CHARGE_PERCENT = "25";
    /**
     * 乙方支付展期服务费
     */
    private static final String  EXTENDED_SERVICE_CHARGE = "%s元/%s天";

    @Autowired
    private UserService userService;

    @Autowired
    private ILoanOrderService loanOrderService;

    /**
     * 服务协议
     *
     * @return
     */
    @GetMapping(value = "/service_agreement")
    public String getServiceAgreement(Model model, HttpServletRequest request) {
        String userCode = request.getParameter("userCode");
        String orderId = request.getParameter("loanOrderId");
        InputStream stream = getClass().getClassLoader().getResourceAsStream("static/md/service_agreement.md");
        try {
            UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
            LoanOrderEntity OrderEntity = loanOrderService.getByOrderId(orderId);
            String realName = "";
            String idCard = "";
            String lentUserName = "";
            String lentAddress = "";
            String lendTime = " 年 月 日";
            String loanOrderId = "";
            String loanServiceCharge = "";
            String postponeUnitCharge = "";
            String days = "";
            if (userEntity != null&&OrderEntity!=null) {
                realName = userEntity.getRealName();
                idCard = userEntity.getIdCard();
                lendTime= new SimpleDateFormat(DATE_FORMAT).format(OrderEntity.getLendTime());
                loanOrderId= OrderEntity.getOrderId();
                loanServiceCharge = String.valueOf(OrderEntity.getChargeValue().intValue());
                postponeUnitCharge = String.valueOf(OrderEntity.getPostponeUnitCharge().intValue());
                days =String.valueOf( RenewableDaysEnum.SEVENT.getDays());
            }
            Map variables = new HashMap<>(16);
            variables.put("sign", MessageVariable.RAPTOR_SIGN_NAME);
            variables.put("company", MessageVariable.COMPANY);
            variables.put("loanUserName", realName);
            variables.put("loanIdCard", idCard);
            variables.put("lentUserName", lentUserName);
            variables.put("lentAddress", lentAddress);
            variables.put("lendTime",lendTime);
            variables.put("loanOrderId",loanOrderId);
            variables.put("companyStamp",COMPANY_STAMP);
            variables.put("serviceChargePercent",SERVICE_CHARGE_PERCENT);
            variables.put("loanServiceCharge",loanServiceCharge);
            variables.put("extendedServiceCharge",String.format(EXTENDED_SERVICE_CHARGE,postponeUnitCharge,days));
            String process = ModelUtils.process(readStreamToString(stream), variables);
            model.addAttribute("title","借款服务协议");
            model.addAttribute("content", process);
        } catch (Exception e) {
            Log.error(logger,e,"获取服务协议发生异常，userCode={},orderId={}",userCode,orderId);
        }
        return "service/agreement";
    }

    /**
     * 支付协议
     *
     * @return
     */
    @GetMapping(value = "/pay_agreement")
    public String getPayAgreement(Model model) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("static/md/pay_agreement.md");
        try {
            Map variables = new HashMap<>(16);
            variables.put("company", MessageVariable.COMPANY);
            variables.put("sign", MessageVariable.RAPTOR_SIGN_NAME);
            variables.put("companyStamp",COMPANY_STAMP);
            String process = ModelUtils.process(readStreamToString(stream), variables);
            model.addAttribute("title","支付协议");
            model.addAttribute("content",process);
        } catch (Exception e) {
            Log.error(logger,e,"获取支付协议发生异常");
        }
        return "service/agreement";
    }

    /**
     * 借款协议
     *
     * @return
     */
    @GetMapping(value = "/loan_agreement")
    public String getLoanAgreement(Model model, HttpServletRequest request) {
        String userCode = request.getParameter("userCode");
        String orderId = request.getParameter("loanOrderId");
        InputStream stream = getClass().getClassLoader().getResourceAsStream("static/md/loan_agreement.md");
        try {
            UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
            LoanOrderEntity orderEntity = loanOrderService.getByOrderId(orderId);
            String realName = "";
            String idCard = "";
            String mobile = "";
            String loanTel = "";
            String lentUserName = "";
            String lentAddress = "";
            String loanAddress = "";
            String lendTime = " 年 月 日";
            String loanNumber = "";
            String loanTerm = "";
            String interestValue = "";
            String loanOrderId = "";
            String repaymentDate = " 年 月 日";

            if (userEntity != null && orderEntity != null) {
                realName = userEntity.getRealName();
                idCard = userEntity.getIdCard();
                mobile = userEntity.getMobile();
                lendTime = new SimpleDateFormat(DATE_FORMAT).format(orderEntity.getLendTime());
                loanTerm = String.valueOf(orderEntity.getLoanTerm());
                loanNumber = String.valueOf(orderEntity.getLoanNumber());
                repaymentDate = new SimpleDateFormat(DATE_FORMAT).format(orderEntity.getRepaymentDate());
                interestValue = String.valueOf(orderEntity.getInterestValue());
                loanOrderId = orderEntity.getOrderId();
            }
            Map variables = new HashMap<>(16);
            variables.put("sign", MessageVariable.RAPTOR_SIGN_NAME);
            variables.put("loanUserName", realName);
            variables.put("loanIdCard", idCard);
            variables.put("lentUserName", lentUserName);
            variables.put("lentAddress", lentAddress);
            variables.put("company", MessageVariable.COMPANY);
            variables.put("lentTel", mobile);
            variables.put("loanTel", loanTel);
            variables.put("loanAddress", loanAddress);
            variables.put("lendTime", lendTime);
            variables.put("loanTerm", loanTerm);
            variables.put("loanNumber", loanNumber);
            variables.put("repaymentDate", repaymentDate);
            variables.put("interestValue", interestValue);
            variables.put("loanOrderId", loanOrderId);

            String process = ModelUtils.process(readStreamToString(stream), variables);
            model.addAttribute("title","借款协议");
            model.addAttribute("content", process);
        } catch (Exception e) {
            Log.error(logger,e,"获取借款协议发生异常，userCode={},orderId={}",userCode,orderId);
        }
        return "service/agreement";
    }

    /**
     * 平台服务协议
     *
     * @return
     */
    @GetMapping(value = "/platform_service_agreement")
    public String getPlatformServiceAgreement(Model model) {

        InputStream stream = getClass().getClassLoader().getResourceAsStream("static/md/platform_service_agreement.md");
        try {
            Map variables = new HashMap<>(16);
            variables.put("company", MessageVariable.COMPANY);
            variables.put("sign", MessageVariable.RAPTOR_SIGN_NAME);
            variables.put("simpleCompany", MessageVariable.SIMPLE_COMPANY);
            variables.put("companyStamp",COMPANY_STAMP);
            String process = ModelUtils.process(readStreamToString(stream), variables);
            model.addAttribute("content",process);
            model.addAttribute("title","用户服务协议");
        } catch (Exception e) {
            Log.error(logger,e,"获取支付协议发生异常");
        }
        return "service/agreement";
    }

    /**
     * 从流中读取字符串
     *
     * @param stream
     * @return
     * @throws IOException
     */
    private String readStreamToString(InputStream stream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = in.readLine()) != null) {
            buffer.append(line + "\n");
        }
        return buffer.toString();
    }
}
