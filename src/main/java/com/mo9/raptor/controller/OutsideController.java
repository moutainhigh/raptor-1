package com.mo9.raptor.controller;

import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.req.PageReq;
import com.mo9.raptor.bean.res.ManualAuditUserRes;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.entity.BankEntity;
import com.mo9.raptor.entity.CardBinInfoEntity;
import com.mo9.raptor.engine.state.event.impl.user.ManualAuditEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.entity.SpreadChannelEntity;
import com.mo9.raptor.entity.UserContactsEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.redis.RedisParams;
import com.mo9.raptor.redis.RedisServiceApi;
import com.mo9.raptor.service.BankService;
import com.mo9.raptor.service.CardBinInfoService;
import com.mo9.raptor.service.SpreadChannelService;
import com.mo9.raptor.service.UserContactsService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.IpUtils;
import com.mo9.raptor.utils.Md5Util;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by jyou on 2018/9/29.
 *
 * @author jyou
 *         对外暴露接口
 */
@Controller
@RequestMapping(value = "/outside")
public class OutsideController {

    private static final String salt = "rtsDDcogZcPCu!NYkfgfjQq6O;~2Brtr";
    public static List<String> MANUAL_AUDIT_USER = Arrays.asList("LBHM:V0CR8N", "DFDF:FVEPA4", "WEFE:C9WHMP", "HRRG:KVTT6D", "YERT:R3VZQ5", "EYEH:TGUOAP", "JDFG:AU5M1W", "SDFG:XA2YES", "DFGD:5MAJBH", "UTRR:UIJH44", "PIUO:0B43LW", "IPUI:TSWIKC", "YUTY:57AD3Z", "RETT:2FFMI9", "QWEF:9ZUG2C");
    private static Logger logger = Log.get();
    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    //    @Value("${raptor.url}")
    private String raptorUrl;

    @Resource
    private UserService userService;

    @Resource
    private UserContactsService userContactsService;

    @Resource
    private SpreadChannelService spreadChannelService;

    @Resource
    private RedisServiceApi redisServiceApi;

    @Resource(name = "raptorRedis")
    private RedisTemplate raptorRedis;

    @Autowired
    private IEventLauncher userEventLauncher;

    @Resource
    private BankService bankService;

    @Resource
    private CardBinInfoService cardBinInfoService;

    /**
     * 拉黑用户
     *
     * @param userCode
     * @param desc
     * @param sign
     * @return
     */
    @GetMapping(value = "/to_black_user")
    @ResponseBody
    public BaseResponse<Boolean> toBlackUser(@RequestParam("userCode") String userCode, @RequestParam("desc") String desc, @RequestParam("sign") String sign) {
        BaseResponse<Boolean> response = new BaseResponse<Boolean>();
        try {
            String str = userCode + desc + salt;
            String md5 = Md5Util.getMD5(str);
            if (!md5.equals(sign)) {
                return response.buildFailureResponse(ResCodeEnum.SIGN_CHECK_ERROR);
            }
            UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
            if (userEntity == null) {
                return response.buildFailureResponse(ResCodeEnum.USER_NOT_EXIST);
            }
            if (!StatusEnum.PASSED.name().equals(userEntity.getStatus())) {
                return response.buildFailureResponse(ResCodeEnum.NOT_SUPPORT_TO_BLACK);
            }
            userService.toBlackUser(userEntity, desc);
            return response.buildSuccessResponse(true);
        } catch (Exception e) {
            Log.error(logger, e, "拉黑用户----->>>>发生异常,userCode={}", userCode);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
    }

    /**
     * 暂时无用 TODO
     *
     * @param model
     * @param source
     * @param subSource
     * @return
     */
    @GetMapping(value = "/to_source_login")
    public String toSourceLogin(Model model, @RequestParam("source") String source, @RequestParam("subSource") String subSource) {
        model.addAttribute("source", source);
        model.addAttribute("subSource", subSource);
        model.addAttribute("host", raptorUrl);
        //返回地址 todo ukar
        return "/test";
    }

    /**
     * 根据日期条件分页获取新用户注册数
     */
    @GetMapping("/get_register_number")
    public String getRegisterUserNumber(Model model, HttpServletRequest request, PageReq pageReq) {
        String source = request.getParameter("source");
        Page<Map<String, Object>> registerUser = userService.getRegisterUserNumber(source, pageReq);
        List<Map<String, Object>> content = registerUser.getContent();
        model.addAttribute("resultList", content);
        return "channel/show";
    }

    /**
     * 登录页面
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/to_login")
    public String toLogin(Model model, HttpServletRequest request) {
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        String remoteHost = IpUtils.getRemoteHost(request);
        //查看是否在登录状态
        SpreadChannelEntity spreadChannelUser = (SpreadChannelEntity) redisServiceApi.get(RedisParams.ACTION_TOKEN_LONG + remoteHost, raptorRedis);
        //非登录状态去登录
        if (spreadChannelUser == null) {
            if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
                model.addAttribute("message", "登录已过期");
                return "channel/login";
            }
            spreadChannelUser = spreadChannelService.findByLoginNameAndPassword(userName, password);
            if (spreadChannelUser == null) {
                model.addAttribute("message", "帐号或密码错误");
                return "channel/login";
            }
        }
        //设置登录成功
        redisServiceApi.set(RedisParams.ACTION_TOKEN_LONG + remoteHost, spreadChannelUser, RedisParams.EXPIRE_1D, raptorRedis);
        logger.info("渠道推广登录接口-------->>>>>渠道[{}]登录成功,ip为[{}]", spreadChannelUser.getSource(), remoteHost);
        Page<Map<String, Object>> registerUser = userService.getRegisterUserNumber(spreadChannelUser.getSource(), new PageReq());

        List<Map<String, Object>> content = registerUser.getContent();

        //根据渠道和子渠道查询填写资料用户个数
        List<Map<String, Object>> auditUser = userService.toAuditUserCount(spreadChannelUser.getSource());
        // 查询去借款的用户数
        List<Map<String, Object>> channelLoanCount = userService.getChannelLoanCount(spreadChannelUser.getSource());
        //组装数据
        content = getResultList(content, auditUser, channelLoanCount);
        model.addAttribute("resultList", content);
        model.addAttribute("code", 0);
        return "channel/show";
    }

    private List<Map<String, Object>> getResultList(List<Map<String, Object>> content, List<Map<String, Object>> auditUser, List<Map<String, Object>> channelLoanCount) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = 0; i < content.size(); i++) {
            Map<String, Object>  map =new HashMap<>();
            Map<String, Object> cMap = content.get(i);
            map.putAll(cMap);
            for (Map<String, Object> auditUserMap : auditUser){
                if (cMap.get("date").equals(auditUserMap.get("date"))&& cMap.get("source").equals(auditUserMap.get("source"))){
                    if (cMap.get("sub_source") == null||auditUserMap.get("sub_source")==null) {
                        map.putAll(auditUserMap);
                        continue;
                    }
                    if (cMap.get("sub_source").equals(auditUserMap.get("sub_source"))) {
                        map.putAll(auditUserMap);
                        continue;
                    }
                }
            }
            for (Map<String, Object> loanMap : channelLoanCount){
                if (cMap.get("source").equals(loanMap.get("source"))&&cMap.get("date").equals(loanMap.get("date"))){
                    if (cMap.get("sub_source") == null||loanMap.get("sub_source")==null) {
                        map.putAll(loanMap);
                        continue;
                    }
                    if (cMap.get("sub_source").equals(loanMap.get("sub_source"))) {
                        map.putAll(loanMap);
                        continue;
                    }
                }
            }
            resultList.add(map);
        }
        return resultList;
    }

    @GetMapping("/login")
    public String loginIndex(Model model, HttpServletRequest request) {
        redisServiceApi.remove(RedisParams.ACTION_TOKEN_LONG + IpUtils.getRemoteHost(request), raptorRedis);
        return "channel/login";
    }

    @GetMapping("audit/contacts")
    public String auditContacts(Model model, @RequestParam String userCode, HttpServletRequest request) {
        String remoteHost = IpUtils.getRemoteHost(request);
        //查看是否在登录状态
        Object auditUser = redisServiceApi.get(RedisParams.ACTION_TOKEN_LONG_AUDIT + remoteHost, raptorRedis);
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

    @GetMapping("/update_all_bank_name")
    @ResponseBody
    public BaseResponse<Boolean> updateAllBankName(String password) {
        BaseResponse<Boolean> response = new BaseResponse<Boolean>();
        if (!password.equals("mo9@2018")) {
            return response.buildFailureResponse(ResCodeEnum.INVALID_SIGN);
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                List<BankEntity> list = bankService.findAll();
                if (list == null || list.size() == 0) {
                    return;
                }

                for (BankEntity bankEntity : list) {
                    String bankNo = bankEntity.getBankNo();
                    if (org.apache.commons.lang.StringUtils.isBlank(bankNo) || bankNo.length() < 6) {
                        continue;
                    }
                    CardBinInfoEntity cardBinInfoEntity = cardBinInfoService.findByCardPrefix(bankNo.substring(0, 6));
                    if (cardBinInfoEntity == null) {
                        bankEntity.setBankName("未知");
                    } else {
                        String bankName = bankEntity.getBankName();
                        String cardBank = cardBinInfoEntity.getCardBank();
                        if (cardBank.equals(bankName)) {
                            continue;
                        }
                        bankEntity.setBankName(cardBank);
                    }
                    bankService.save(bankEntity);
                }
            }
        });
        t.start();
        return response.buildSuccessResponse(true);
    }


    /**
     * 人工审核登录页面
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("audit/to_login")
    public String audittoLogin(Model model, HttpServletRequest request, HttpServletResponse response,@RequestParam(required = false,defaultValue = "channel_1") String source) {
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        String remoteHost = IpUtils.getRemoteHost(request);
        //只有公司 ip允许登录
        if (!IpUtils.ips.contains(remoteHost)) {
            logger.warn("非公司ip禁止登录，ip为[{}]", remoteHost);
            model.addAttribute("message", "登录失败");
            return "audit/login";
        }
        //查看是否在登录状态
        Object auditUser = redisServiceApi.get(RedisParams.ACTION_TOKEN_LONG_AUDIT + remoteHost, raptorRedis);
        //非登录状态去登录
        if (auditUser == null) {
            if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
                model.addAttribute("message", "登录已过期");
                return "audit/login";
            }
            if (!MANUAL_AUDIT_USER.contains(userName + ":" + password)) {
                model.addAttribute("message", "帐号或密码错误");
                return "audit/login";
            }
            auditUser = userName;
            //设置登录成功
            logger.info("人工审核登录接口-------->>>>>用户[{}]登录成功,ip为[{}]", auditUser, remoteHost);
        }
        Cookie cookie = new Cookie("username", auditUser.toString());
        cookie.setMaxAge(30 * 60);
        response.addCookie(cookie);
        redisServiceApi.set(RedisParams.ACTION_TOKEN_LONG_AUDIT + remoteHost, auditUser, RedisParams.EXPIRE_30M, raptorRedis);

        //查询所有需要人工审核的用户
        List<UserEntity> userEntities = userService.findManualAuditUser(source);
        //封装返回参数
        List<ManualAuditUserRes> resultList = copyUserEntityProperties(userEntities);

        model.addAttribute("resultList", resultList);
        model.addAttribute("code", 0);
        model.addAttribute("source",source);
        return "audit/show";
    }

    /**
     * 操作审核结果
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("audit/operate")
    @ResponseBody
    public String auditOperate(Model model, HttpServletRequest request, @RequestParam String type, @RequestParam String userCode) {
        String remoteHost = IpUtils.getRemoteHost(request);
        //查看是否在登录状态
        Object auditUser = redisServiceApi.get(RedisParams.ACTION_TOKEN_LONG_AUDIT + remoteHost, raptorRedis);
        //非登录状态去登录
        if (auditUser == null) {
            return "-1";
        }
        String username = getUserNameFromCookies(request);
        logger.info("人工审核操作接口-------->>>>>操作人[{}]，ip为[{}]，userCode=[{}],type=[{}]", username, remoteHost, userCode, type);
        List<String> mobile = new ArrayList<>();
        UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
        //非人工审核状态禁止
        if (userEntity == null || !StatusEnum.MANUAL.name().equals(userEntity.getStatus())) {
            return "-1";
        }
        mobile.add(userEntity.getMobile());
        manualAuditUser(mobile, StatusEnum.valueOf(type), "mo9@2018", "人工审核，操作人:" + username);
        //查询所有需要人工审核的用户
        List<UserEntity> userEntities = userService.findManualAuditUser("channel_1");
        //封装返回参数
        List<ManualAuditUserRes> resultList = copyUserEntityProperties(userEntities);
        model.addAttribute("resultList", resultList);
        model.addAttribute("code", 0);
        return "0";
    }

    private String getUserNameFromCookies(HttpServletRequest request) {
        String username = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (cookie.getName().equals("username")) {
                    username = cookie.getValue();
                    break;
                }
            }
        }
        return username;
    }

    private List<ManualAuditUserRes> copyUserEntityProperties(List<UserEntity> userEntities) {
        List<ManualAuditUserRes> resultList = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            ManualAuditUserRes manualAuditUserRes = new ManualAuditUserRes();
            BeanUtils.copyProperties(userEntity, manualAuditUserRes);
            manualAuditUserRes.setStatus(StatusEnum.valueOf(userEntity.getStatus()).getExplanation());
            manualAuditUserRes.setCreateTime(new SimpleDateFormat(DATE_FORMAT).format(userEntity.getCreateTime()));
            resultList.add(manualAuditUserRes);
        }
        return resultList;
    }

    @GetMapping("audit/login")
    public String auditLoginIndex(Model model, HttpServletRequest request) {
        redisServiceApi.remove(RedisParams.ACTION_TOKEN_LONG_AUDIT + IpUtils.getRemoteHost(request), raptorRedis);
        return "audit/login";
    }


    //    @GetMapping("/manual_audit_user")
//    @ResponseBody
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

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "4444";
    }

}
