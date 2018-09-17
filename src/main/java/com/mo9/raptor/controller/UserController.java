package com.mo9.raptor.controller;

import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.ReqHeaderParams;
import com.mo9.raptor.bean.req.BankReq;
import com.mo9.raptor.bean.req.LoginByCodeReq;
import com.mo9.raptor.bean.res.AccountBankCardRes;
import com.mo9.raptor.bean.res.AuditStatusRes;
import com.mo9.raptor.entity.BankEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.redis.RedisParams;
import com.mo9.raptor.redis.RedisServiceApi;
import com.mo9.raptor.service.BankService;
import com.mo9.raptor.service.CaptchaService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.RegexUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author zma
 * @date 2018/9/13
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Resource
    private RedisServiceApi redisServiceApi;

    @Resource(name = "raptorRedis")
    private RedisTemplate raptorRedis;

    @Resource
    private CaptchaService captchaService;

    @Autowired
    private BankService bankService ;

    @RequestMapping(value = "/login_by_code")
    public BaseResponse loginByCode(@RequestBody @Validated LoginByCodeReq loginByCodeReq, HttpServletRequest request) {
        BaseResponse<Map<String, Object>> response = new BaseResponse<>();
        Map<String, Object> resMap = new HashMap<>(16);
        Map<String, String> entity = new HashMap<>(16);
        String clientId = request.getHeader(ReqHeaderParams.CLIENT_ID);
        String mobile = loginByCodeReq.getMobile();
        //校验手机号是否合法，不合法登录失败
        boolean check = RegexUtils.checkChinaMobileNumber(mobile);
        if (!check) {
            return response.buildFailureResponse(ResCodeEnum.MOBILE_NOT_MEET_THE_REQUIRE);
        }
        try {
            //检查用户是否在白名单，并可用
            //TODO 白名单 查询方法
            UserEntity userEntity = userService.findByMobile(mobile);
            if (userEntity == null) {
                return response.buildFailureResponse(ResCodeEnum.NOT_WHITE_LIST_USER);
            }
            //校验验证码是否正确
            String code = loginByCodeReq.getCode();
            ResCodeEnum resCodeEnum = captchaService.checkLoginMobileCaptcha(mobile, code);
            if (ResCodeEnum.SUCCESS != resCodeEnum) {
                return response.buildFailureResponse(resCodeEnum);
            }
            //返回token
            String token = UUID.randomUUID().toString().replaceAll("-", StringUtils.EMPTY);
            redisServiceApi.set(RedisParams.getAccessToken(clientId,userEntity.getUserCode()),token,RedisParams.EXPIRE_30M,raptorRedis);
            entity.put("mobile",userEntity.getMobile());
            entity.put("accessToken",token);
            entity.put("accessCode",userEntity.getUserCode());
            resMap.put("entity",entity);
        } catch (IOException e) {
            logger.error("用户登录----->>>>验证码发送发生异常{}",e);
            return response.buildFailureResponse(ResCodeEnum.CAPTCHA_SEND_FAILED);
        } catch (Exception e) {
            logger.error("用户登录----->>>>发生异常{}",e);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
        return response.buildSuccessResponse(resMap);
    }


    /**
     * 修改银行卡信息 -- 包含验证四要素 , 第一次新增
     * @param bankReq
     * @param request
     * @return
     */
    @PostMapping(value = "/modify_bank_card_info")
    public BaseResponse modifyBankCardInfo(@RequestBody @Validated BankReq bankReq, HttpServletRequest request) {
        BaseResponse response = new BaseResponse();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode,false);
        if(userEntity == null ){
            //用户不存在
            response.setCode(ResCodeEnum.NOT_WHITE_LIST_USER.getCode());
            response.setMessage(ResCodeEnum.NOT_WHITE_LIST_USER.getMessage());
            return response;
        }
        if(userEntity.getIdCard() == null){
            //身份证不存在
            response.setCode(ResCodeEnum.USER_CARD_ID_NOT_EXIST.getCode());
            response.setMessage(ResCodeEnum.USER_CARD_ID_NOT_EXIST.getMessage());
            return response;
        }
        ResCodeEnum resCodeEnum = bankService.verify(bankReq.getCard() , userEntity.getIdCard() , userEntity.getRealName() , bankReq.getCardMobile() , userEntity.getUserCode() , bankReq.getBankName());
        if(ResCodeEnum.SUCCESS != resCodeEnum){
            response.setCode(resCodeEnum.getCode());
            response.setMessage(resCodeEnum.getMessage());
            return response;
        }
        return response;
    }


    @PostMapping(value = "/get_audit_status")
    public BaseResponse getAuditStatus(HttpServletRequest request){
        BaseResponse response = new BaseResponse();
        AuditStatusRes auditStatusRes = new AuditStatusRes();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode,false);
        if(userEntity == null ){
            //用户不存在
            return response.buildFailureResponse(ResCodeEnum.NOT_WHITE_LIST_USER);
        }
        auditStatusRes.setAuditStatus(userEntity.getAuditStatus());
        auditStatusRes.setCertifyInfo(userEntity.getCertifyInfo());
        auditStatusRes.setCallHistory(userEntity.getCallHistory());
        AccountBankCardRes accountBankCardRes = null;
        BankEntity bankEntity = bankService.findByUserCodeLastOne(userEntity.getUserCode());
        auditStatusRes.setAccountBankCard(accountBankCardRes);
        if (bankEntity == null){
            auditStatusRes.setAccountBankCardVerified(false);
        }else {
            auditStatusRes.setAccountBankCardVerified(true);
            accountBankCardRes = new AccountBankCardRes();
            accountBankCardRes.setBankName(bankEntity.getBankName());
            accountBankCardRes.setCard(bankEntity.getBankNo());
            accountBankCardRes.setCardName(bankEntity.getUserName());
            accountBankCardRes.setCardMobile(bankEntity.getMobile());
        }
        response.setData(auditStatusRes);
        return response.buildSuccessResponse(response);
    }

}
