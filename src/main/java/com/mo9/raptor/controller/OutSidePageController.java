package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.req.PageReq;
import com.mo9.raptor.entity.SpreadChannelEntity;
import com.mo9.raptor.redis.RedisParams;
import com.mo9.raptor.redis.RedisServiceApi;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.IpUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

    @Resource
    private UserService userService;

    @Resource
    private RedisServiceApi redisServiceApi;

    @Resource(name = "raptorRedis")
    private RedisTemplate raptorRedis;

    @RequestMapping("/pageLoadJson")
    @ResponseBody
    public JSONObject pageLoadJson(HttpServletRequest request,@RequestParam(required = false,defaultValue = "1") Integer pageSize,@RequestParam(required = false,defaultValue = "1") Integer pageNumber) {
        JSONObject result = new JSONObject();
        String remoteHost = IpUtils.getRemoteHost(request);
        SpreadChannelEntity spreadChannelUser = (SpreadChannelEntity) redisServiceApi.get(RedisParams.ACTION_TOKEN_LONG + remoteHost, raptorRedis);
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
}
