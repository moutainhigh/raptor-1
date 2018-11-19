package com.mo9.raptor.controller;

import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.ReqHeaderParams;
import com.mo9.raptor.bean.req.UserContactsReq;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.service.UserContactsService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.log.Log;
import com.mo9.risk.app.entity.User;
import com.mo9.risk.service.RiskContractInfoService;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 */
@RestController
@RequestMapping("/user")
public class UserContactsController {

    private static Logger logger = Log.get();
    @Resource
    private UserService userService;

    @Resource
    private UserContactsService userContactsService;

    @Resource
    private RiskContractInfoService riskContractInfoService;

    private ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
            5,
            50,
            10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(200),
            new ThreadPoolExecutor.AbortPolicy());

    /**
     * 提交手机通讯录
     * @param request
     * @param req
     * @return
     */
    @RequestMapping("/submit_mobile_contacts")
    public BaseResponse<Boolean> submitMobileContacts(HttpServletRequest request, @RequestBody @Validated UserContactsReq req){
        BaseResponse<Boolean> response = new BaseResponse<Boolean>();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        String clientId = request.getHeader(ReqHeaderParams.CLIENT_ID);
        String clientVersion = request.getHeader(ReqHeaderParams.CLIENT_VERSION);
        try{
            UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
            if(userEntity == null ){
                logger.warn("提交通讯录-->用户不存在");
                return response.buildFailureResponse(ResCodeEnum.USER_NOT_EXIST);
            }
            userContactsService.submitMobileContacts(req.getData(), userCode, clientId, clientVersion);
            long count = userContactsService.findMobileContactsCount(userCode);
            if(count == 1){
                //更新用户表通讯录状态
                userService.updateMobileContacts(userEntity, true);
            }
            poolExecutor.execute(() -> {
                logger.info("手机通讯录更新完毕，开始存放mycat开始，userCode={},data是否为空={}", userEntity.getUserCode(), req.getData() == null ? true : false);
                User user = new User();
                BeanUtils.copyProperties(userEntity, user);
                riskContractInfoService.createAll(req.getData(), user);
            });
            return response.buildSuccessResponse(true);
        }catch (Exception e){
            Log.error(logger, e, "提交通讯录-->系统内部异常");
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
    }

}
