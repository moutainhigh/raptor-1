package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.ReqHeaderParams;
import com.mo9.raptor.bean.condition.CashAccountLogCondition;
import com.mo9.raptor.bean.condition.CouponCondition;
import com.mo9.raptor.bean.condition.FetchPayOrderCondition;
import com.mo9.raptor.bean.req.BankReq;
import com.mo9.raptor.bean.req.LoginByCodeReq;
import com.mo9.raptor.bean.req.ModifyCertifyReq;
import com.mo9.raptor.bean.res.CashAccountLogRes;
import com.mo9.raptor.bean.res.CouponRes;
import com.mo9.raptor.engine.calculator.ILoanCalculator;
import com.mo9.raptor.engine.entity.CouponEntity;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.CouponService;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.entity.*;
import com.mo9.raptor.bean.res.AccountBankCardRes;
import com.mo9.raptor.bean.res.AuditStatusRes;
import com.mo9.raptor.enums.*;
import com.mo9.raptor.redis.RedisParams;
import com.mo9.raptor.redis.RedisServiceApi;
import com.mo9.raptor.service.*;
import com.mo9.raptor.utils.CommonUtils;
import com.mo9.raptor.utils.CommonValues;
import com.mo9.raptor.utils.IpUtils;
import com.mo9.raptor.utils.RegexUtils;
import com.mo9.raptor.utils.log.Log;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author zma
 * @date 2018/9/13
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    private static Logger logger = Log.get();

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

    @Autowired
    private BankLogService banklogService ;

    @Autowired
    private CommonUtils commonUtils ;

    @Autowired
    private CashAccountService cashAccountService ;

    @Autowired
    private CouponService couponService ;

    @Autowired
    private ILoanOrderService loanOrderService ;
    @Autowired
    private ILoanCalculator loanCalculator;

    @Resource
    private UserCertifyInfoService userCertifyInfoService;

    @RequestMapping(value = "/login_by_code")
    public BaseResponse loginByCode(@RequestBody @Validated LoginByCodeReq loginByCodeReq, HttpServletRequest request) {
        BaseResponse<Map<String, Object>> response = new BaseResponse<>();
        Map<String, Object> resMap = new HashMap<>(16);
        Map<String, Object> entity = new HashMap<>(16);
        String clientId = request.getHeader(ReqHeaderParams.CLIENT_ID);
        String mobile = loginByCodeReq.getMobile();
        String source = loginByCodeReq.getSource();
        String subSource = loginByCodeReq.getSubSource();
        String captchaKey = loginByCodeReq.getCaptchaKey();
        boolean isNewUser = false;
        //校验手机号是否合法，不合法登录失败
        boolean check = RegexUtils.checkChinaMobileNumber(mobile);
        if (!check) {
            logger.warn("用户登录----->>>>手机号=[{}]不合法",mobile);
            return response.buildFailureResponse(ResCodeEnum.MOBILE_NOT_MEET_THE_REQUIRE);
        }
        try {
            //判断是否需要校验图形验证码
            ResCodeEnum checkGraphic = null;
            if(StringUtils.isNotBlank(source)){
                checkGraphic = checkGraphic(loginByCodeReq.getCaptcha(), RedisParams.GRAPHIC_CAPTCHA_KEY + captchaKey);
            }
            if(checkGraphic != null && ResCodeEnum.SUCCESS != checkGraphic){
                logger.warn("用户登录----->>>>图形验证码校验失败mobile={}", mobile);
                return response.buildFailureResponse(checkGraphic);
            }

            //校验验证码是否正确
            String code = loginByCodeReq.getCode();
            ResCodeEnum resCodeEnum = captchaService.checkLoginMobileCaptcha(mobile, code);
            if (ResCodeEnum.SUCCESS != resCodeEnum) {
                return response.buildFailureResponse(resCodeEnum);
            }

            //检查用户是否在白名单，并可用
            UserEntity userEntity = userService.findByMobileAndDeleted(mobile,false);
            if (userEntity == null) {
                //校验今天是否允许新用户注册
                boolean b = userService.isaAllowNewUser();
                if(!b){
                    logger.warn("用户登录----->>>>手机号=[{}]非白名单用户",mobile);
                    return response.buildFailureResponse(ResCodeEnum.NOT_WHITE_LIST_USER);
                }
                //新用户注册
                userEntity = UserEntity.buildNewUser(mobile, source, subSource);
                isNewUser = true;
            }
            //返回token
            String token = UUID.randomUUID().toString().replaceAll("-", StringUtils.EMPTY);
            redisServiceApi.set(RedisParams.getAccessToken(clientId,userEntity.getUserCode()),token,RedisParams.EXPIRE_30M,raptorRedis);
            entity.put("userId",userEntity.getId());
            entity.put("mobile",userEntity.getMobile());
            entity.put("accessToken",token);
            entity.put("accountCode",userEntity.getUserCode());
            resMap.put("entity",entity);
            userEntity.setLastLoginTime(System.currentTimeMillis());
            userEntity.setUserIp(IpUtils.getRemoteHost(request));
            userEntity.setUpdateTime(System.currentTimeMillis());
            userService.save(userEntity);
            if(isNewUser){
                userService.addAllowNewUserNum();
            }
            logger.info("用户登录成功----->>>>手机号={},source={},subSource={}",mobile, source, subSource);
        } catch (IOException e) {
            Log.error(logger,e,"用户登录----->>>>验证码发送发生异常,手机号={}",mobile);
            return response.buildFailureResponse(ResCodeEnum.CAPTCHA_SEND_FAILED);
        } catch (Exception e) {
            Log.error(logger,e,"用户登录----->>>>发生异常,手机号={}",mobile);
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
       try {
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
           UserCertifyInfoEntity userCertifyInfoEntity = userCertifyInfoService.findByUserCode(userEntity.getUserCode()) ;
           if(userCertifyInfoEntity == null){
               //身份证不存在
               response.setCode(ResCodeEnum.USER_CARD_ID_NOT_EXIST.getCode());
               response.setMessage(ResCodeEnum.USER_CARD_ID_NOT_EXIST.getMessage());
               return response;
           }
           Boolean flag = commonUtils.fiveMinutesNumberOk(userCode) ;
           if(!flag){
               //存储log
               banklogService.create(bankReq.getCard() , userEntity.getIdCard() , userEntity.getRealName() , bankReq.getCardMobile() ,
                       bankReq.getBankName() ,userCode ,
                       bankReq.getCardStartCount() , bankReq.getCardSuccessCount() , bankReq.getCardFailCount(), CommonValues.FAILED);
               //验证过于频繁
               response.setCode(ResCodeEnum.BANK_VERIFY_TOO_FREQUENTLY.getCode());
               response.setMessage(ResCodeEnum.BANK_VERIFY_TOO_FREQUENTLY.getMessage());
               return response;
           }
           ResCodeEnum resCodeEnum = bankService.verify(bankReq , userEntity , userCertifyInfoEntity);
           if(ResCodeEnum.SUCCESS != resCodeEnum){
               response.setCode(resCodeEnum.getCode());
               response.setMessage(resCodeEnum.getMessage());
               return response;
           }
       }catch (Exception e){
           Log.error(logger,e,"修改银行卡信息----->>>>发生异常,userCode={}",userCode);
           return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
       }
        return response;
    }

    /**
     * 修改账户身份认证信息
     * @param request
     * @return
     */
    @PostMapping(value = "/modify_certify_info")
    public BaseResponse<Boolean> modifyCertifyInfo(HttpServletRequest request, @RequestBody @Validated ModifyCertifyReq modifyCertifyReq){
        BaseResponse response = new BaseResponse();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        try{
            UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
            if(userEntity == null ){
                logger.warn("修改账户身份认证信息-->用户不存在");
                return response.buildFailureResponse(ResCodeEnum.USER_NOT_EXIST);
            }
            UserCertifyInfoEntity entity1 = userCertifyInfoService.findByIdCard(modifyCertifyReq.getIdCard());
            if(entity1 != null && !userCode.equals(entity1.getUserCode())){
                logger.warn("修改账户身份认证信息-->身份证已存在,idCard={}", modifyCertifyReq.getIdCard());
                return response.buildFailureResponse(ResCodeEnum.IDCARD_IS_EXIST);
            }
            UserCertifyInfoEntity userCertifyInfoEntity = userCertifyInfoService.findByUserCode(userCode);
            userCertifyInfoService.modifyCertifyInfo(userEntity, userCertifyInfoEntity, modifyCertifyReq);
            userEntity.setIdCard(modifyCertifyReq.getIdCard());
            userEntity.setRealName(modifyCertifyReq.getRealName());
            if(!userEntity.getCertifyInfo()){
                userService.updateCertifyInfo(userEntity,true);
            }else {
                userService.save(userEntity);
            }
            return response.buildSuccessResponse(true);
        }catch (Exception e){
            Log.error(logger,e,"修改账户身份认证信息-->系统内部异常userCode={}", userCode);
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
            Log.error(logger,e,"用户登出-->系统内部异常userCode={}", userCode);
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
            /**
             * 判断当前用户状态是否需要扭转 因新增用户状态，
             * app无法快速改变，所以添加的临时改变计划，非长久之计
             */
            userEntity = UserEntity.changeStatusToAuditing(userEntity.getStatus(), userEntity);
            auditStatusRes.setAuditStatus(userEntity.getStatus());
            auditStatusRes.setCertifyInfo(userEntity.getCertifyInfo());
            auditStatusRes.setCallHistory(userEntity.getCallHistory());
            auditStatusRes.setMobileContacts(userEntity.getMobileContacts());
            AccountBankCardRes accountBankCardRes = null;
            Map<String,String> certifyInfoDetail  = null;
            if (userEntity.getCertifyInfo()){
                certifyInfoDetail = new HashMap<>(16);
                certifyInfoDetail.put("realName",userEntity.getRealName());
                certifyInfoDetail.put("idCard",userEntity.getIdCard());
            }
            auditStatusRes.setCertifyInfoDetail(certifyInfoDetail);
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
            Log.error(logger,e,"查询用户审核状态----->>>>发生异常,userCode={}",userCode);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
        map.put("entity",auditStatusRes);
        return response.buildSuccessResponse(map);
    }

    /**
     * 告知服务通话记录已上传
     * @param request
     * @return
     */
    @RequestMapping(value = "/phone_record_uploaded")
    public BaseResponse phoneRecordUploaded(HttpServletRequest request){
        BaseResponse<String> response = new BaseResponse<>();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        try {
            UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
            if (userEntity !=null){
                userService.updateCallHistory(userEntity,true);
            }else {
                logger.warn("通讯录授权失败,用户点击完成接口----->>>>userCode={},未查询到用户",userCode);
                return response.buildFailureResponse(ResCodeEnum.NOT_WHITE_LIST_USER);
            }
        } catch (Exception e) {
            Log.error(logger,e,"通讯录授权成功,用户点击完成接口----->>>>发生异常 userCode={}",userCode);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
        return response.buildSuccessResponse("ok");
    }

    /**
     * 获取账户余额
     * @param request
     * @return
     */
    @RequestMapping(value = "/get_balance")
    public BaseResponse<JSONObject> getBalance(HttpServletRequest request){
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        JSONObject entity = new JSONObject() ;
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        CashAccountEntity cashAccountEntity = cashAccountService.findByUserCode(userCode);
        if(cashAccountEntity == null){
            entity.put("balance" , "0");
        }else{
            entity.put("balance" , cashAccountEntity.getBalance().toPlainString());
        }
        response.setData(entity);
        return response;
    }

    /**
     * 获取用户优惠券
     * @param request
     * @return
     */
    @RequestMapping(value = "/get_coupons")
    public BaseResponse<JSONObject> getCoupons(@RequestParam(value = "pageNumber")Integer pageNumber ,
                                               @RequestParam(value = "pageSize")Integer pageSize ,
                                               @RequestParam(required = false , value = "type")List<String> type ,
                                               @RequestParam(required = false , value = "orderId")String orderId ,
                                               @RequestParam(required = false , value = "action")String action ,
                                               @RequestParam(required = false , value = "period")Integer period ,
                                               HttpServletRequest request){
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        JSONObject entity = new JSONObject() ;
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        CouponCondition couponCondition = new CouponCondition() ;
        couponCondition.setPageNumber(pageNumber);
        couponCondition.setPageSize(pageSize);
        couponCondition.setUserCode(userCode);
        //封装状态
        setStatusCondition(type , couponCondition);

        if(action != null && !("ALL".equals(action))){
            couponCondition.setUseType(action);
            if(orderId != null){
                //查询 订单金额
                LoanOrderEntity loanOrderEntity = loanOrderService.getByOrderId(orderId);
                if(loanOrderEntity == null){
                    response.setCode(ResCodeEnum.LOAN_ORDER_NOT_EXISTED.getCode());
                    response.setMessage(ResCodeEnum.LOAN_ORDER_NOT_EXISTED.getMessage());
                    return response ;
                }
                String payType = PayTypeEnum.REPAY_IN_ADVANCE.name() ;
                if("RENEWAL".equals(action)){
                    payType = PayTypeEnum.REPAY_POSTPONE.name() ;
                    if(period == null ){
                        //默认7天 - 暂时业务只有7天
                        period = 7 ;
                    }
                }
                Item orderRealItem =loanCalculator.realItem(System.currentTimeMillis(), loanOrderEntity, payType, period);
                BigDecimal shouldPay = orderRealItem.sum();
                couponCondition.setLimitAmount(shouldPay);
            }
        }

        Page<CouponEntity> page = couponService.findByCondition(couponCondition);
        List<CouponRes> returnList = setCounponRes(page);
        entity.put("coupons" , returnList);
        entity.put("total" , page.getTotalElements());
        response.setData(entity);
        return response;
    }

    /**
     * 封装优惠卷返回数据
     * @param page
     * @return
     */
    private List<CouponRes> setCounponRes(Page<CouponEntity> page) {
        List<CouponEntity> content = page.getContent();
        if (content == null || content.size() == 0) {
            return new ArrayList<CouponRes>();
        }
        List<CouponRes> returnList =  new ArrayList<CouponRes>();
        for(CouponEntity couponEntity : content){
            CouponRes couponRes = new CouponRes() ;
            BeanUtils.copyProperties(couponEntity , couponRes);
            Long expiryDate = couponEntity.getExpireDate() ;
            couponRes.setEndTime(couponEntity.getExpireDate());
            couponRes.setStartTime(couponEntity.getEffectiveDate());
            couponRes.setCouponsAmount(couponEntity.getApplyAmount().toPlainString());
            //判断时间
            if(couponEntity.getStatus().equals(StatusEnum.OVERDUE.name()) || System.currentTimeMillis() >= expiryDate){
                //已经过期
                couponRes.setType("OVERDUE");
            }else if(couponEntity.getStatus().equals(StatusEnum.PENDING.name()) ){
                couponRes.setType("EFFECTIVE");
            }else{
                couponRes.setType("USED");
            }
            couponRes.setCouponsId(couponEntity.getCouponId());
            couponRes.setAction(couponEntity.getUseType());
            returnList.add(couponRes) ;
        }
        return returnList ;
    }

    /**
     * 封装查询状态参数
     * @param type
     * @param couponCondition
     */
    private void setStatusCondition(List<String> type, CouponCondition couponCondition) {
        if(type == null || type.size() == 0){
            return ;
        }
        if(type.contains("OVERDUE") && type.contains("USED") && type.contains("EFFECTIVE")){
            return ;
        }
        List<String> statusList = new ArrayList<String>();
        //OVERDUE (已过期) , USED (已使用)  , EFFECTIVE (有效) , 不传递查询所有
        if(type.contains("OVERDUE")){
            statusList.add(StatusEnum.OVERDUE.name());
        }
        if(type.contains("USED")){
            statusList.add(StatusEnum.EXECUTING.name());
            statusList.add(StatusEnum.BUNDLED.name());
            statusList.add(StatusEnum.ENTRY_DOING.name());
            statusList.add(StatusEnum.ENTRY_DONE.name());
        }
        if(type.contains("EFFECTIVE")){
            statusList.add(StatusEnum.PENDING.name());
            //查询有效的数据 - 另外增加条件 - 时间
            couponCondition.setExpiryDate(System.currentTimeMillis());
        }
        couponCondition.setStatusList(statusList);
    }


    /**
     * 获取用户账户流水
     * @param pageNumber
     * @param pageSize
     * @param fromTime
     * @param toTime
     * @param type
     * @return
     */
    @RequestMapping(value = "/get_wallet_log")
    public BaseResponse<JSONObject> getWalletLog(@RequestParam(value = "pageNumber")Integer pageNumber ,
                                                 @RequestParam(value = "pageSize")Integer pageSize ,
                                                 @RequestParam(required = false , value = "fromTime")Long fromTime ,
                                                 @RequestParam(required = false , value = "toTime")Long toTime ,
                                                 @RequestParam(required = false , value = "type")List<String> type ,
                                                 HttpServletRequest request){
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        CashAccountLogCondition cashAccountLogCondition = new CashAccountLogCondition() ;
        //时间转换
        if(fromTime != null){
            Date fromDate = setDateForTime(fromTime);
            cashAccountLogCondition.setFromDate(fromDate);
        }
        if(toTime != null){
            Date toDate = setDateForTime(toTime);
            cashAccountLogCondition.setToDate(toDate);
        }
        cashAccountLogCondition.setPageNumber(pageNumber);
        cashAccountLogCondition.setPageSize(pageSize);
        cashAccountLogCondition.setUserCode(userCode);
        try {
            setInAndOutType(type , cashAccountLogCondition);
        } catch (Exception e) {
            logger.error("传递的查询类型异常 : " + type , e);
            response.setCode(ResCodeEnum.CASH_ACCOUNT_BUSINESS_TYPE_EXCEPTION.getCode());
            response.setMessage(ResCodeEnum.CASH_ACCOUNT_BUSINESS_TYPE_EXCEPTION.getMessage());
            return response;
        }
        Page<CashAccountLogEntity> page = cashAccountService.findLogByCondition(cashAccountLogCondition);
        List<CashAccountLogRes> cashAccountLogResList = setLogRes(page);
        JSONObject returnJson = new JSONObject();
        returnJson.put("entities",cashAccountLogResList);
        returnJson.put("total",page.getTotalElements());
        response.setData(returnJson);
        return response;
    }

    /**
     * 封装返回参数
     * @param page
     * @return
     */
    private List<CashAccountLogRes> setLogRes(Page<CashAccountLogEntity> page) {
        List<CashAccountLogEntity> content = page.getContent();
        if (content == null || content.size() == 0) {
            return new ArrayList<CashAccountLogRes>();
        }
        List<CashAccountLogRes> returnList =  new ArrayList<CashAccountLogRes>();
        for (CashAccountLogEntity cashAccountLogEntity : content){
            CashAccountLogRes cashAccountLogRes = new CashAccountLogRes() ;
            BeanUtils.copyProperties(cashAccountLogEntity , cashAccountLogRes);
            cashAccountLogRes.setCreateTime(cashAccountLogEntity.getCreateTime().getTime());
            cashAccountLogRes.setBalanceType(cashAccountLogEntity.getBalanceType().name());
            if(BusinessTypeEnum.ONLINE_POSTPONE == cashAccountLogEntity.getBusinessType() || BusinessTypeEnum.ONLINE_BALANCE_POSTPONE == cashAccountLogEntity.getBusinessType()){
                cashAccountLogRes.setType(BusinessTypeEnum.ONLINE_POSTPONE.name());
            }else if(BusinessTypeEnum.ONLINE_REPAY == cashAccountLogEntity.getBusinessType() || BusinessTypeEnum.ONLINE_BALANCE_REPAY == cashAccountLogEntity.getBusinessType()){
                cashAccountLogRes.setType(BusinessTypeEnum.ONLINE_REPAY.name());
            }else if(BusinessTypeEnum.UNDERLINE_POSTPONE == cashAccountLogEntity.getBusinessType() || BusinessTypeEnum.UNDERLINE_BALANCE_POSTPONE == cashAccountLogEntity.getBusinessType()){
                cashAccountLogRes.setType(BusinessTypeEnum.UNDERLINE_POSTPONE.name());
            }else if(BusinessTypeEnum.UNDERLINE_REPAY == cashAccountLogEntity.getBusinessType() || BusinessTypeEnum.UNDERLINE_BALANCE_REPAY == cashAccountLogEntity.getBusinessType()){
                cashAccountLogRes.setType(BusinessTypeEnum.UNDERLINE_REPAY.name());
            }else{
                cashAccountLogRes.setType(cashAccountLogEntity.getBusinessType().name());
            }
            returnList.add(cashAccountLogRes);
        }
        return returnList ;
    }

    /**
     * 设置出账入账类型
     * @param types
     * @param cashAccountLogCondition
     */
    private void setInAndOutType(List<String> types, CashAccountLogCondition cashAccountLogCondition) {
        if(types != null && types.contains("RECHARGE_TYPES")){
            BusinessTypeEnum[] type = BusinessTypeEnum.values() ;
            List<BusinessTypeEnum> list = Arrays.asList(type);
            cashAccountLogCondition.setInType(list);
        }
        if(types != null && types.contains("ENTRY_TYPES")){
            BusinessTypeEnum[] type = BusinessTypeEnum.values() ;
            List<BusinessTypeEnum> list = Arrays.asList(type);
            cashAccountLogCondition.setOutType(list);
        }
        if(types != null && types.size() > 0){
            //遍历
            List<BusinessTypeEnum> listIn = new ArrayList<BusinessTypeEnum>();
            List<BusinessTypeEnum> listOut = new ArrayList<BusinessTypeEnum>();
            for(String type : types){
                if(type.startsWith("IN_")){
                    listIn.add(BusinessTypeEnum.valueOf(type.split("IN_")[1])) ;
                }
                if(type.startsWith("OUT_")){
                    if(type.contains("ONLINE_REPAY")){
                        listOut.add(BusinessTypeEnum.ONLINE_REPAY) ;
                        listOut.add(BusinessTypeEnum.ONLINE_BALANCE_REPAY) ;
                    }else if(type.contains("ONLINE_POSTPONE")){
                        listOut.add(BusinessTypeEnum.ONLINE_POSTPONE) ;
                        listOut.add(BusinessTypeEnum.ONLINE_BALANCE_POSTPONE) ;
                    }else if(type.contains("UNDERLINE_REPAY")){
                        listOut.add(BusinessTypeEnum.UNDERLINE_REPAY) ;
                        listOut.add(BusinessTypeEnum.UNDERLINE_BALANCE_REPAY) ;
                    }else if(type.contains("UNDERLINE_POSTPONE")){
                        listOut.add(BusinessTypeEnum.UNDERLINE_POSTPONE) ;
                        listOut.add(BusinessTypeEnum.UNDERLINE_BALANCE_POSTPONE) ;
                    }else{
                        listOut.add(BusinessTypeEnum.valueOf(type.split("OUT_")[1])) ;
                    }
                }
            }
            if(!types.contains("RECHARGE_TYPES")){
                cashAccountLogCondition.setInType(listIn);
            }
            if(!types.contains("ENTRY_TYPES")){
                cashAccountLogCondition.setOutType(listOut);
            }
        }
    }

    /**
     * 转换时间 - Long - String
     * @param fromTime
     * @return
     */
    private Date setDateForTime(Long fromTime) {
        Calendar calendar = Calendar.getInstance() ;
        calendar.setTimeInMillis(fromTime);
        return calendar.getTime() ;
    }


    /**
     * 校验图形验证码是否正确
     * @param captcha
     * @return
     */
    private ResCodeEnum checkGraphic(String captcha, String redisCaptchaKey) {

        String pinCode = (String) redisServiceApi.get(redisCaptchaKey, raptorRedis);
        if (StringUtils.isBlank(pinCode) || StringUtils.isBlank(captcha)) {
            return ResCodeEnum.CAPTCHA_IS_INVALID_GRAPHIC;
        }

        if (!captcha.equalsIgnoreCase(pinCode)) {
            return ResCodeEnum.CAPTCHA_CHECK_ERROR_GRAPHIC;
        }
        redisServiceApi.remove(redisCaptchaKey, raptorRedis);
       return ResCodeEnum.SUCCESS;

    }
}
