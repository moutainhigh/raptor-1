package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.req.PageReq;
import com.mo9.raptor.entity.SpreadChannelEntity;
import com.mo9.raptor.redis.RedisParams;
import com.mo9.raptor.redis.RedisServiceApi;
import com.mo9.raptor.service.SpreadChannelService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.CookieUtils;
import com.mo9.raptor.utils.IpUtils;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zma
 * @date 2018/10/15
 */
@Controller
@RequestMapping(value = "/outside")
public class OutSidePageController {

    private static Logger logger = Log.get();

    @Resource
    private SpreadChannelService spreadChannelService;

    @Resource
    private UserService userService;

    @Resource
    private RedisServiceApi redisServiceApi;

    @Resource(name = "raptorRedis")
    private RedisTemplate raptorRedis;

    /**
     * 登录页面
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/to_login")
    public String toLogin(Model model, HttpServletRequest request, HttpServletResponse response) {
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        String remoteHost = IpUtils.getRemoteHost(request);
        String loginToken = CookieUtils.getLoginValue(request);
        SpreadChannelEntity spreadChannelUser = null;
        if (StringUtils.isEmpty(userName)&&!StringUtils.isEmpty(loginToken)) {
            //验证登录token
            spreadChannelUser = (SpreadChannelEntity) redisServiceApi.get(RedisParams.ACTION_TOKEN_LONG + remoteHost + loginToken, raptorRedis);
        }
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
            //设置登录成功
            redisServiceApi.set(RedisParams.ACTION_TOKEN_LONG + remoteHost + CookieUtils.addLoginCookie(response), spreadChannelUser, RedisParams.EXPIRE_1D, raptorRedis);
        }
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

    @RequestMapping("/pageLoadJson")
    @ResponseBody
    public JSONObject pageLoadJson(HttpServletRequest request,@RequestParam(required = false,defaultValue = "1") Integer pageSize,@RequestParam(required = false,defaultValue = "1") Integer pageNumber) {
        JSONObject result = new JSONObject();
        String loginToken = CookieUtils.getLoginValue(request);
        SpreadChannelEntity spreadChannelUser = (SpreadChannelEntity) redisServiceApi.get(RedisParams.ACTION_TOKEN_LONG + IpUtils.getRemoteHost(request) + loginToken, raptorRedis);
        if (spreadChannelUser==null){
             result.put("code",-1);
            return  result;
        }
        Page<Map<String, Object>> registerUser = userService.getRegisterUserNumber(spreadChannelUser.getSource(), new PageReq(pageNumber,pageSize));
        List<Map<String, Object>> content = registerUser.getContent();
        //根据渠道和子渠道查询填写资料用户个数
        List<Map<String, Object>> auditUser = userService.toAuditUserCount(spreadChannelUser.getSource());
        // 查询去借款的用户数
        List<Map<String, Object>> channelLoanCount = userService.getChannelLoanCount(spreadChannelUser.getSource());
        //组装数据
        content = getResultList(content, auditUser, channelLoanCount);
        result.put("pageSize",pageSize);
        result.put("pageNumber",pageNumber);
        result.put("totalRow",registerUser.getTotalElements());
        result.put("totalPage",registerUser.getTotalPages());
        result.put("list",content);
        return result;
    }

    @RequestMapping("/show")
    public String show(){
        return "channel/page_show";
    }

    @GetMapping("/login")
    public String loginIndex(Model model, HttpServletRequest request) {
        redisServiceApi.remove(RedisParams.ACTION_TOKEN_LONG + IpUtils.getRemoteHost(request)+CookieUtils.getLoginValue(request), raptorRedis);
        return "channel/login";
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
}
