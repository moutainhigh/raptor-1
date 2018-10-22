package com.mo9.raptor.controller;

import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.req.PageReq;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.entity.BankEntity;
import com.mo9.raptor.entity.CardBinInfoEntity;
import com.mo9.raptor.entity.SpreadChannelEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.redis.RedisParams;
import com.mo9.raptor.redis.RedisServiceApi;
import com.mo9.raptor.service.BankService;
import com.mo9.raptor.service.CardBinInfoService;
import com.mo9.raptor.service.SpreadChannelService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.IpUtils;
import com.mo9.raptor.utils.Md5Util;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    private static Logger logger = Log.get();
    //    @Value("${raptor.url}")
    private String raptorUrl;

    @Resource
    private UserService userService;


    @Resource
    private SpreadChannelService spreadChannelService;

    @Resource
    private RedisServiceApi redisServiceApi;

    @Resource(name = "raptorRedis")
    private RedisTemplate raptorRedis;

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
            if (StringUtils.isEmpty(cMap.get("sub_source"))){
                continue;
            }
            for (Map<String, Object> loanMap : channelLoanCount){
                if (cMap.get("source").equals(loanMap.get("source"))&&cMap.get("date").equals(loanMap.get("date"))){
                    if (cMap.get("sub_source").equals(loanMap.get("sub_source"))) {
                        map.putAll(loanMap);
                        continue;
                    }
                }
            }
            for (Map<String, Object> auditUserMap : auditUser){
                if (cMap.get("date").equals(auditUserMap.get("date"))&& cMap.get("source").equals(auditUserMap.get("source"))){
                    if (cMap.get("sub_source").equals(auditUserMap.get("sub_source"))) {
                        map.putAll(auditUserMap);
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

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "1111";
    }

}
