package com.mo9.raptor.controller;

import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.req.PageReq;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.entity.SpreadChannelEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.service.SpreadChannelService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.Md5Util;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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

    @Resource
    private UserService userService;

    @Resource
    private SpreadChannelService spreadChannelService;

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
     * 根据日期条件分页获取新用户注册数
     */
    @GetMapping("/get_register_number")
    public String getRegisterUserNumber(Model model,HttpServletRequest request, PageReq pageReq){
        String source = request.getParameter("source");
        Page<Map<String, Object>> registerUser = userService.getRegisterUserNumber(source,  pageReq);
        List<Map<String, Object>> content = registerUser.getContent();
        model.addAttribute("resultList",content);
        return "channel/show";
    }

    /**
     * 登录页面
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/to_login")
    public String toLogin(Model model,HttpServletRequest request){
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        if (StringUtils.isEmpty(userName)||StringUtils.isEmpty(password)){
            model.addAttribute("message","帐号或密码错误");
            return "channel/login";
        }
        SpreadChannelEntity spreadChannelEntity = spreadChannelService.findByLoginNameAndPassword(userName,password);
        if (spreadChannelEntity == null){
            model.addAttribute("message","帐号或密码错误");
            return "channel/login";
        }
        Page<Map<String, Object>> registerUser = userService.getRegisterUserNumber(spreadChannelEntity.getSource(),  new PageReq());
        List<Map<String, Object>> content = registerUser.getContent();
        model.addAttribute("resultList",content);
        model.addAttribute("code",0);
        return "channel/show";
    }
    @GetMapping("/login")
    public String loginIndex(Model model,HttpServletRequest request){
        return "channel/login";
    }

}
