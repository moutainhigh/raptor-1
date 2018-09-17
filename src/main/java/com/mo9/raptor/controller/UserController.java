package com.mo9.raptor.controller;

import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.ReqHeaderParams;
import com.mo9.raptor.bean.req.BankReq;
import com.mo9.raptor.bean.req.LoginByCodeReq;
import com.mo9.raptor.bean.req.ModifyCertifyReq;
import com.mo9.raptor.entity.UserCertifyInfoEntity;
import com.mo9.raptor.bean.res.AccountBankCardRes;
import com.mo9.raptor.bean.res.AuditStatusRes;
import com.mo9.raptor.entity.BankEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.BankAuthStatusEnum;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.redis.RedisParams;
import com.mo9.raptor.redis.RedisServiceApi;
import com.mo9.raptor.service.BankService;
import com.mo9.raptor.service.CaptchaService;
import com.mo9.raptor.service.UserCertifyInfoService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.RegexUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @Resource
    private UserCertifyInfoService userCertifyInfoService;

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
            UserEntity userEntity = userService.findByMobileAndDeleted(mobile,false);
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
            userEntity.setLastLoginTime(System.currentTimeMillis());
            userService.save(userEntity);
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
        ResCodeEnum resCodeEnum = bankService.verify(bankReq.getCard() , bankReq.getCardMobile() , bankReq.getBankName() , userEntity);
        if(ResCodeEnum.SUCCESS != resCodeEnum){
            response.setCode(resCodeEnum.getCode());
            response.setMessage(resCodeEnum.getMessage());
            return response;
        }
        return response;
    }

    /**
     * 修改账户身份认证信息
     * @param request
     * @return
     */
    @PostMapping(value = "/modify_certify_info")
    public BaseResponse<Boolean> modifyCertifyInfo(HttpServletRequest request, @RequestBody ModifyCertifyReq modifyCertifyReq){
        BaseResponse response = new BaseResponse();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        try{
            UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
            if(userEntity == null ){
                logger.warn("修改账户身份认证信息-->用户不存在");
                return response.buildFailureResponse(ResCodeEnum.USER_NOT_EXIST);
            }
            UserCertifyInfoEntity userCertifyInfoEntity = userCertifyInfoService.findByUserCode(userCode);
            userCertifyInfoService.modifyCertifyInfo(userEntity, userCertifyInfoEntity, modifyCertifyReq);
            if(!userEntity.getCertifyInfo()){
                userService.updateCertifyInfo(userEntity, true);
            }
            return response.buildSuccessResponse(true);
        }catch (Exception e){
            logger.error("修改账户身份认证信息-->系统内部异常", e);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
    }


    /**
     * 登出
     * @param request
     * @return
     */
    @PostMapping(value = "/logout")
    public BaseResponse<Boolean> logout(HttpServletRequest request){
        BaseResponse<Boolean> response = new BaseResponse<Boolean>();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        String clientId = request.getHeader(ReqHeaderParams.CLIENT_ID);
        try{
            UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
            if(userEntity == null ){
                logger.warn("用户登出-->用户不存在");
                return response.buildFailureResponse(ResCodeEnum.USER_NOT_EXIST);
            }
            redisServiceApi.remove(RedisParams.getAccessToken(clientId,userCode), raptorRedis);
            return response.buildSuccessResponse(true);
        }catch (Exception e){
            logger.error("用户登出-->系统内部异常", e);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }

    }

    /**
     * 查询审核状态
     * @param request
     * @return
     */
    @GetMapping(value = "/get_audit_status")
    public BaseResponse getAuditStatus(HttpServletRequest request){
        BaseResponse<Map<String, AuditStatusRes>> response = new BaseResponse();
        AuditStatusRes auditStatusRes = new AuditStatusRes();
        Map<String,AuditStatusRes> map = new HashMap<>(16);
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        try {
            UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode,false);
            if(userEntity == null ){
                //用户不存在
                return response.buildFailureResponse(ResCodeEnum.NOT_WHITE_LIST_USER);
            }
            auditStatusRes.setAuditStatus(userEntity.getStatus());
            auditStatusRes.setCertifyInfo(userEntity.getCertifyInfo());
            auditStatusRes.setCallHistory(userEntity.getCallHistory());
            auditStatusRes.setMobileContacts(userEntity.getMobileContacts());
            AccountBankCardRes accountBankCardRes = null;
            String bankAuthStatus = userEntity.getBankAuthStatus();
            //银行验证是否成功，不成功直接返回
            if (!BankAuthStatusEnum.SUCCESS.name().equals(bankAuthStatus)){
                auditStatusRes.setAccountBankCardVerified(false);
                map.put("entity",auditStatusRes);
                response.setData(map);
                return response.buildSuccessResponse(map);
            }
            //查询用户银行卡信息
            BankEntity bankEntity = bankService.findByUserCodeLastOne(userEntity.getUserCode());
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
            auditStatusRes.setAccountBankCard(accountBankCardRes);
        } catch (Exception e) {
            logger.error("查询用户审核状态----->>>>发生异常{}",e);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
        map.put("entity",auditStatusRes);
        return response.buildSuccessResponse(map);
    }

}
