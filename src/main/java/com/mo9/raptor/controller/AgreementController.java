package com.mo9.raptor.controller;

import com.mo9.raptor.utils.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


/**
 * 读取服务协议
 * @author zma
 * @date 2018/9/19
 */
@Controller
@RequestMapping("/service")
public class AgreementController {

    private static Logger logger = LoggerFactory.getLogger(AgreementController.class);
    /**
     *读取服务协议
     * @return
     */
    @RequestMapping(value = "/agreement")
    public String getServiceAgreement(Model model,HttpServletRequest request) {

        InputStream stream = getClass().getClassLoader().getResourceAsStream("static/js/服务协议.md");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null){
                buffer.append(line+"\n");
            }
            Map variables = new HashMap<>(16);
            variables.put("realName","张三");
            variables.put("idCard","5222554545484654");
            String process = ModelUtils.process(buffer.toString(), variables);
            model.addAttribute("content",process);
        } catch (Exception e) {
            logger.error("获取服务协议发生异常，",e);
        }
        return "service/agreement";
    }
}
