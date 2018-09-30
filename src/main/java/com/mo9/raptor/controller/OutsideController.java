package com.mo9.raptor.controller;

import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.Md5Util;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by jyou on 2018/9/29.
 *
 * @author jyou
 * 对外暴露接口
 */
@Controller
@RequestMapping(value = "/outside")
public class OutsideController {

    private static final String salt = "rtsDDcogZcPCu!NYkfgfjQq6O;~2Brtr";

    private static Logger logger = Log.get();

//    @Value("${raptor.url}")
    private String raptorUrl;

    @Resource
    private UserService userService;

    @GetMapping(value = "/to_black_user")
    @ResponseBody
    public BaseResponse<Boolean> toBlackUser(@RequestParam("userCode") String userCode, @RequestParam("desc")String desc, @RequestParam("sign")String sign){
        BaseResponse<Boolean> response = new BaseResponse<Boolean>();
        try{
            String str = userCode + desc + salt;
            String md5 = Md5Util.getMD5(str);
            if(!md5.equals(sign)){
                return response.buildFailureResponse(ResCodeEnum.SIGN_CHECK_ERROR);
            }
            UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
            if(userEntity == null){
                return response.buildFailureResponse(ResCodeEnum.USER_NOT_EXIST);
            }
            if(!StatusEnum.PASSED.name().equals(userEntity.getStatus())){
                return response.buildFailureResponse(ResCodeEnum.NOT_SUPPORT_TO_BLACK);
            }
            userService.toBlackUser(userEntity, desc);
            return response.buildSuccessResponse(true);
        }catch (Exception e){
            Log.error(logger,e,"拉黑用户----->>>>发生异常,userCode={}", userCode);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
    }

    /**
     * 暂时无用 TODO
     * @param model
     * @param source
     * @param subSource
     * @return
     */
    @GetMapping(value = "/to_source_login")
    public String toSourceLogin(Model model, @RequestParam("source") String source, @RequestParam("subSource") String subSource) {
        model.addAttribute("source",source);
        model.addAttribute("subSource", subSource);
        model.addAttribute("host",raptorUrl);
        //返回地址 todo ukar
        return "/test";
    }
}
