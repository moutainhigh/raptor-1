package com.mo9.raptor.controller;

import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static Logger logger = LoggerFactory.getLogger(AgreementController.class);

    @Autowired
    private UserService userService;

    /**
     * 服务协议
     *
     * @return
     */
    @GetMapping(value = "/service_agreement")
    public String getServiceAgreement(Model model, HttpServletRequest request) {
        String userCode = request.getParameter("userCode");
        InputStream stream = getClass().getClassLoader().getResourceAsStream("static/md/service_agreement.md");
        try {
            UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
            String realName = "";
            String idCard = "";
            if (userEntity != null) {
                realName = userEntity.getRealName();
                idCard = userEntity.getIdCard();
            }
            Map variables = new HashMap<>(16);
            variables.put("realName", realName);
            variables.put("idCard", idCard);
            String process = ModelUtils.process(readStreamToString(stream), variables);
            model.addAttribute("content", process);
        } catch (Exception e) {
            logger.error("获取服务协议发生异常，", e);
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
            model.addAttribute("content", readStreamToString(stream));
        } catch (Exception e) {
            logger.error("获取支付协议发生异常，", e);
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
        InputStream stream = getClass().getClassLoader().getResourceAsStream("static/md/loan_agreement.md");
        try {
            UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
            String realName = "";
            String idCard = "";
            if (userEntity != null) {
                realName = userEntity.getRealName();
                idCard = userEntity.getIdCard();
            }
            Map variables = new HashMap<>(16);
            variables.put("realName", realName);
            variables.put("idCard", idCard);
            String process = ModelUtils.process(readStreamToString(stream), variables);
            model.addAttribute("content", process);
        } catch (Exception e) {
            logger.error("获取支付协议发生异常，", e);
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
            model.addAttribute("content", readStreamToString(stream));
        } catch (Exception e) {
            logger.error("获取支付协议发生异常，", e);
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
