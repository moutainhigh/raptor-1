package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.utils.CommonValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by xtgu on 2018/9/16.
 * @author xtgu
 */
@RestController
@RequestMapping(value = "/system")
public class SystemController {

    private static final Logger logger = LoggerFactory.getLogger(SystemController.class);

    @Value("${system.switch}")
    private String systemSwitch ;

    /**
     * 临时添加默认值为空，防止线上启动报错
     */
    @Value("${contact.information:}")
    private String contactInformation ;

    /**
     * 查询系统是否开启
     * @return
     */
    @GetMapping("/switch")
    public BaseResponse<JSONObject> systemSwitch(HttpServletRequest request){
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        JSONObject returnJson = new JSONObject() ;
        if(systemSwitch != null && CommonValues.TRUE.equals(systemSwitch)){
            returnJson.put("switch" , true) ;
        }else{
            returnJson.put("switch" , false) ;
        }
        response.setData(returnJson);
        return response ;
    }
    /**
     * 获取客服联系方式【新】
     * @return
     */
    @GetMapping("/contact_information")
    public BaseResponse<JSONObject> getContactInformation(){
        BaseResponse<JSONObject> response = new BaseResponse<>();
        JSONObject returnJson = new JSONObject() ;
        returnJson.put("contactInformation",contactInformation);
        return response.buildSuccessResponse(returnJson) ;
    }

}
