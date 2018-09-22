package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.utils.CommonValues;
import com.mo9.raptor.utils.log.Log;
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

    private static Logger logger = Log.get();
    @Value("${system.switch}")
    private String systemSwitch ;

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

}
