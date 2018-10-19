package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.res.ManualAuditUserRes;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.state.event.impl.user.ManualAuditEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.entity.AuditOperationRecordEntity;
import com.mo9.raptor.entity.AuditUserEntity;
import com.mo9.raptor.entity.UserContactsEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.AuditLevelEnum;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.redis.RedisParams;
import com.mo9.raptor.redis.RedisServiceApi;
import com.mo9.raptor.service.AuditOperationRecordService;
import com.mo9.raptor.service.AuditUserService;
import com.mo9.raptor.service.UserContactsService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.CookieUtils;
import com.mo9.raptor.utils.IpUtils;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人工审核
 *
 * @author zma
 * @date 2018/10/17
 */
@Controller
@RequestMapping(value = "/credit")
public class CreditAuditController {

    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    @Resource
    private AuditUserService auditUserService;

    @Resource
    private UserService userService;

    @Autowired
    private IEventLauncher userEventLauncher;

    @Resource
    private AuditOperationRecordService auditOperationRecordService;

    @Resource
    private RedisServiceApi redisServiceApi;

    @Resource(name = "raptorRedis")
    private RedisTemplate raptorRedis;

    @Resource
    private UserContactsService userContactsService;

    private static Logger logger = Log.get();

    /**
     * 人工审核登录页面
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/audit/to_login")
    public String auditLogin(Model model, HttpServletRequest request, HttpServletResponse response) {
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        String remoteHost = IpUtils.getRemoteHost(request);
        //只有公司 ip允许登录
         if (!IpUtils.ips.contains(remoteHost)) {
            logger.warn("非公司ip禁止登录，ip为[{}]", remoteHost);
            model.addAttribute("message", "登录失败");
            return "credit/login";
        }
        String loginName = userName;
        String cookieName = CookieUtils.getValueFromCookies(request, "loginName");
        if (StringUtils.isEmpty(loginName)&&!StringUtils.isEmpty(cookieName)) {
            loginName = cookieName;
        }else if (StringUtils.isEmpty(cookieName)||!cookieName.equals(loginName)){
            CookieUtils.addCookie(response,"loginName",loginName,24 * 60 * 60);
        }
        //查看是否在登录状态
        AuditUserEntity auditUserEntity = (AuditUserEntity) redisServiceApi.get(RedisParams.ACTION_TOKEN_LONG_AUDIT + remoteHost + loginName, raptorRedis);
        //非登录状态去登录
        if (auditUserEntity == null) {
            if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
                model.addAttribute("message", "登录已过期");
                return "credit/login";
            }
            auditUserEntity = auditUserService.findByLoginNameAndPassword(userName, password);
            if (auditUserEntity == null) {
                model.addAttribute("message", "帐号或密码错误");
                return "credit/login";
            }
            //设置登录成功
            CookieUtils.addCookie(response, "loginName", loginName, 24 * 60 * 60);
            loginName = auditUserEntity.getLoginName();
            logger.info("人工审核登录接口-------->>>>>用户[{}]登录成功,ip为[{}]", loginName, remoteHost);
        }

        redisServiceApi.set(RedisParams.ACTION_TOKEN_LONG_AUDIT + remoteHost + auditUserEntity.getLoginName(), auditUserEntity, RedisParams.EXPIRE_30M, raptorRedis);
        if (AuditLevelEnum.MANAGE.name().equals(auditUserEntity.getLevel())) {
            return "credit/manage";
        }
        //查询所有需要当前操作员审核的用户
        List<UserEntity> userEntities = userService.findManualAuditUserBuyOperateId(auditUserEntity.getId().toString());
        //封装返回参数
        List<ManualAuditUserRes> resultList = copyUserEntityProperties(userEntities);
        model.addAttribute("resultList", resultList);
        return "credit/show";
    }

    /**
     * 主管分配待审核用户
     * @param request
     * @return
     */
    @RequestMapping("/audit/distribute")
    @ResponseBody
    public JSONObject distributeUser(HttpServletRequest request,@RequestParam String operateId,@RequestParam Integer limit) {
        JSONObject result = new JSONObject();
        try {
            //查看是否在登录状态
            AuditUserEntity auditUserEntity = authLoginStatus(request);
            if (auditUserEntity == null || !AuditLevelEnum.MANAGE.name().equals(auditUserEntity.getLevel() )){
            result.put("code",-1);
            return result;
        }
          auditOperationRecordService.distributeUser(limit,operateId,auditUserEntity.getId().toString());
        } catch (Exception e) {
            result.put("code",-9);
            logger.error("人工审核后台--------->>>>>主管分配任务发生异常",e);
        }
        result.put("code",0);
        return result;
    }

    /**
     * 获取待人工审核用户总数
     * @param request
     * @return
     */
    @RequestMapping("audit/get_manual_totality")
    @ResponseBody
    public JSONObject getManualTotality(HttpServletRequest request){
        JSONObject result = new JSONObject();
        //查看是否在登录状态
        AuditUserEntity auditUserEntity = authLoginStatus(request);
        if (auditUserEntity == null || !AuditLevelEnum.MANAGE.name().equals(auditUserEntity.getLevel() )){
            result.put("code",-1);
            return result;
        }
        Long total = userService.countByStatus(StatusEnum.MANUAL);
        Long auditOperationRecordTotal =  auditOperationRecordService.countByStatus(StatusEnum.MANUAL);
        result.put("total",total);
        result.put("un_distribute",total - auditOperationRecordTotal);
        return result;
    }
    /**
     * 主管获取已分配案件 操作员处理进度
     * @param request
     * @return
     */
    @RequestMapping("audit/get_audit_record")
    @ResponseBody
    public JSONObject getAuditRecord(HttpServletRequest request){
        JSONObject result = new JSONObject();
        //查看是否在登录状态
        AuditUserEntity auditUserEntity = authLoginStatus(request);
        if (auditUserEntity == null || !AuditLevelEnum.MANAGE.name().equals(auditUserEntity.getLevel() )){
            result.put("code",-1);
            return result;
        }
        List<AuditUserEntity> allAuditUse = auditUserService.findNormalUser();

        // 获取操作员所有操作记录（未审核，已审核案件个数）
        List<Map<String,Object>> auditRecord = auditOperationRecordService.getAuditRecord(auditUserEntity.getId());
        List<Map<String,Object>> auditRecordMapList  = getAuditRecordMapList(allAuditUse,auditRecord);
        result.put("code",0);
        result.put("list",auditRecordMapList);
        return result;
    }

    private List<Map<String,Object>> getAuditRecordMapList(List<AuditUserEntity> allAuditUse, List<Map<String, Object>> auditRecord) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (AuditUserEntity auditUserEntity: allAuditUse){
             String operateId = auditUserEntity.getId().toString();
            Map<String, Object>  map =new HashMap<>();
            for (Map<String, Object> auditRecordMap:auditRecord){
                if (operateId.equals(auditRecordMap.get("operate_id"))){
                    map.putAll(auditRecordMap);
                    continue;
                }
            }
            if (StringUtils.isEmpty(map.get("all_audit_num"))||StringUtils.isEmpty(map.get("manual_audit_num"))){
                map.put("all_audit_num",0);
                map.put("manual_audit_num",0);
            }
            map.put("operate_id",operateId);
            map.put("login_name",auditUserEntity.getLoginName());
            map.put("name",auditUserEntity.getName());
            resultList.add(map);
        }
        return resultList;

    }


    @RequestMapping("audit/operate")
    @ResponseBody
    public String auditOperate(HttpServletRequest request, @RequestParam String type, @RequestParam String userCode,@RequestParam String remark) {
        //查看是否在登录状态
        AuditUserEntity auditUser = authLoginStatus(request);
        //非登录状态去登录
        if (auditUser == null) {
            return "-1";
        }
        logger.info("人工审核操作接口-------->>>>>操作人[{}]，userCode=[{}],type=[{}]", auditUser.getName(),  userCode, type);
        List<String> mobile = new ArrayList<>();
        UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
        //非人工审核状态禁止
        if (userEntity == null || !StatusEnum.MANUAL.name().equals(userEntity.getStatus())) {
            return "-1";
        }
        mobile.add(userEntity.getMobile());
        manualAuditUser(mobile, StatusEnum.valueOf(type), "mo9@2018", "人工审核，操作人:" + auditUser.getName());
        AuditOperationRecordEntity auditOperationRecordEntity = auditOperationRecordService.findByOperateIdAndUserCode(auditUser.getId(),userCode);
        if (auditOperationRecordEntity==null){
            return "-1";
        }
        auditOperationRecordEntity.setUpdateTime(System.currentTimeMillis());
        auditOperationRecordEntity.setRemark(remark);
        auditOperationRecordEntity.setStatus(type);
        auditOperationRecordEntity.setAuditTime(System.currentTimeMillis());
        auditOperationRecordService.save(auditOperationRecordEntity);
        return "0";
    }

    private List<ManualAuditUserRes> copyUserEntityProperties(List<UserEntity> userEntities) {
        List<ManualAuditUserRes> resultList = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            ManualAuditUserRes manualAuditUserRes = new ManualAuditUserRes();
            BeanUtils.copyProperties(userEntity, manualAuditUserRes);
            manualAuditUserRes.setStatus(StatusEnum.valueOf(userEntity.getStatus()).getExplanation());
            manualAuditUserRes.setAuthTime(new SimpleDateFormat(DATE_FORMAT).format(userEntity.getAuthTime()));
            resultList.add(manualAuditUserRes);
        }
        return resultList;
    }

    private BaseResponse<Boolean> manualAuditUser(List<String> mobiles, StatusEnum status, String password, String explanation) {
        BaseResponse<Boolean> response = new BaseResponse<Boolean>();
        if (!password.equals("mo9@2018")) {
            return response.buildFailureResponse(ResCodeEnum.INVALID_SIGN);
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    manualUser(mobiles, status, explanation);
                } catch (Exception e) {
                    logger.error("批量人工修改用户状态出现异常", e);
                }
            }
        });
        t.start();
        return response.buildSuccessResponse(true);

    }
    private void manualUser(List<String> mobiles, StatusEnum statusEnum, String explanation) throws Exception {
        List<UserEntity> list = userService.findByMobiles(mobiles);
        if (list == null || list.size() == 0) {
            logger.info("批量人工修改用户状态根据手机号列表查询用户不存在");
            return;
        }
        boolean isPass = false;
        if (statusEnum == StatusEnum.PASSED) {
            isPass = true;
        } else if (statusEnum == StatusEnum.REJECTED) {
            isPass = false;
        } else {
            logger.info("批量人工修改用户状态，statusEnum不符合要求statusEnum={}", statusEnum);
            return;
        }
        for (UserEntity userEntity : list) {
            String status = userEntity.getStatus();
            if (!StatusEnum.MANUAL.name().equals(status)) {
                continue;
            }
            ManualAuditEvent event = new ManualAuditEvent(userEntity.getUserCode(), isPass, explanation);
            userEventLauncher.launch(event);
        }

    }
    @GetMapping("audit/login")
    public String loginIndex(Model model, HttpServletRequest request) {
        String loginName = CookieUtils.getValueFromCookies(request, "loginName");
        redisServiceApi.remove(RedisParams.ACTION_TOKEN_LONG + IpUtils.getRemoteHost(request)+loginName, raptorRedis);
        return "credit/login";
    }
    @GetMapping("audit/contacts")
    public String auditContacts(Model model, @RequestParam String userCode, HttpServletRequest request) {
        //查看是否在登录状态
        AuditUserEntity auditUser = authLoginStatus(request);
        //非登录状态去登录
        if (auditUser == null) {
            model.addAttribute("message", "登录已过期");
            return "audit/login";
        }
        UserContactsEntity userContactsEntity = userContactsService.getByUserCode(userCode);
        if (userContactsEntity != null) {
            model.addAttribute("contacts", userContactsEntity.getContactsList());
        }
        return "audit/contacts";
    }

    private AuditUserEntity authLoginStatus(HttpServletRequest request){
        String remoteHost = IpUtils.getRemoteHost(request);
        String loginName = CookieUtils.getValueFromCookies(request, "loginName");
        //查看是否在登录状态
        return (AuditUserEntity) redisServiceApi.get(RedisParams.ACTION_TOKEN_LONG_AUDIT + remoteHost + loginName, raptorRedis);
    }

}
