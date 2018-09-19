package com.mo9.raptor.controller;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.InputStream;


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
    public String get(Model model) {
        //打程jar后正常读取文件
        InputStream stream = getClass().getClassLoader().getResourceAsStream("static/file/服务协议.md");
        File targetFile = new File("file/服务协议.md");
        try {
            FileUtils.copyInputStreamToFile(stream, targetFile);
            String s = FileUtils.readFileToString(targetFile, "UTF-8");
            model.addAttribute("content",s);
        } catch (Exception e) {
            logger.error("获取服务协议发生异常，",e);
        }
        return "service/agreement";
    }
}
