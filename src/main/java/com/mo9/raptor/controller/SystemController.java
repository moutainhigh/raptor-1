package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.req.PageReq;
import com.mo9.raptor.engine.utils.TimeUtils;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.service.CommonService;
import com.mo9.raptor.service.DingTalkService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.CommonValues;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by xtgu on 2018/9/16.
 * @author xtgu
 */
@RestController
@RequestMapping(value = "/system")
public class SystemController {

    private static Logger logger = Log.get();
    @Value("${system.switch}")
    private String systemSwitch ;

    /**
     * 临时添加默认值为空，防止线上启动报错
     */
    @Value("${contact.information:}")
    private String contactInformation ;

    @Autowired
    private CommonService commonService ;

    @Autowired
    private DingTalkService dingTalkService ;

    /**
     * 查询系统是否开启
     * @return
     */
    @GetMapping("/switch")
    public BaseResponse<JSONObject> systemSwitch(HttpServletRequest request){
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        JSONObject returnJson = new JSONObject() ;
        if(systemSwitch != null && CommonValues.TRUE.equals(systemSwitch)){
            returnJson.put("switch" , true) ;
        }else{
            returnJson.put("switch" , false) ;
        }
        response.setData(returnJson);
        return response ;
    }
    /**
     * 获取客服联系方式【新】
     * @return
     */
    @GetMapping("/contact_information")
    public BaseResponse<JSONObject> getContactInformation(){
        BaseResponse<JSONObject> response = new BaseResponse<>();
        JSONObject returnJson = new JSONObject() ;
        returnJson.put("contactInformation",contactInformation);
        return response.buildSuccessResponse(returnJson) ;
    }

    /**
     * 获取系统信息
     * @return
     */
    @GetMapping("/common_task")
    public BaseResponse<JSONObject> commonTask(){
        BaseResponse<JSONObject> response = new BaseResponse<>();
        Long time = TimeUtils.extractDateTime(System.currentTimeMillis())/1000 ;
        JSONObject returnJson = new JSONObject() ;
        returnJson.put("contactInformation",contactInformation);
        Map<String , Integer> commonUserInfo = commonService.findUserInfo("ssss") ;
        Map<String , Integer> loanInfo = commonService.findLoanInfo("ssss") ;
        Map<String , Integer> repayInfo = commonService.findRepayInfo(time);

        dingTalkService.sendText(" 用户总数 :  " + commonUserInfo.get("userNumber") + "\n 今日登陆用户数 : " + commonUserInfo.get("userLoginNumber")
                + "\n身份证认证总数 : " + commonUserInfo.get("userCardNumber") + "\n通话记录认证总数 : " + commonUserInfo.get("userPhoneNumber")
                + "\n通讯录认证总数 : " + commonUserInfo.get("userCallHistoryNumber") + "\n银行卡认证总数 : " + commonUserInfo.get("userBankNumber")
                + "\n今日放款限额 : " + loanInfo.get("maxAmount") + "\n今日放款总数 : " + loanInfo.get("loanNumber")
                + "\n今日放款总金额 : " + loanInfo.get("loanAmount") + "\n今日还款总数 : " + repayInfo.get("repayNumber")
                + "\n今日还款金额 : " + repayInfo.get("repayAmount") + "\n今日延期总数 : " + repayInfo.get("postponeNumber")
                + "\n今日延期金额 : " + repayInfo.get("postponeAmount") + "\n逾期单量 : " + repayInfo.get("overdueNumber"));

        return response.buildSuccessResponse(returnJson) ;
    }

}
