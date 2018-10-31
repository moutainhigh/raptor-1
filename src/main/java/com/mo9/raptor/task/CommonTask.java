package com.mo9.raptor.task;

import com.mo9.raptor.bean.vo.CommonUserInfo;
import com.mo9.raptor.engine.utils.TimeUtils;
import com.mo9.raptor.service.CommonService;
import com.mo9.raptor.service.DingTalkService;
import com.mo9.raptor.utils.CommonValues;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by xtgu on 2018/9/25.
 * @author xtgu
 * 系统定时器 - 查询各种放款信息等
 */
@Component("commonTask")
public class CommonTask {
    private static Logger logger = Log.get();

    @Value("${task.open}")
    private String taskOpen ;

    @Autowired
    private CommonService commonService ;

    @Autowired
    private DingTalkService dingTalkService ;


    @Scheduled(cron = "0 0 0/1 * * ?")
    public void commonTask(){

        if(CommonValues.TRUE.equals(taskOpen)){
            Long time = TimeUtils.extractDateTime(System.currentTimeMillis())/1000 ;
            logger.info("系统定时器开启");
            /*Map<String , Integer> commonUserInfo = commonService.findUserInfo("ssss") ;*/
            Map<String , Integer> loanInfo = commonService.findLoanInfo("ssss") ;
            Map<String , Integer> repayInfo = commonService.findRepayInfo(time);

            /*dingTalkService.sendText(" 用户总数 :  " + commonUserInfo.get("userNumber") + "\n 今日登陆用户数 : " + commonUserInfo.get("userLoginNumber")
                    + "\n身份证认证总数 : " + commonUserInfo.get("userCardNumber") + "\n通话记录认证总数 : " + commonUserInfo.get("userPhoneNumber")
                    + "\n通讯录认证总数 : " + commonUserInfo.get("userCallHistoryNumber") + "\n银行卡认证总数 : " + commonUserInfo.get("userBankNumber")
                    + "\n今日放款限额 : " + loanInfo.get("maxAmount") + "\n今日放款总数 : " + loanInfo.get("loanNumber")
                    + "\n今日放款总金额 : " + loanInfo.get("loanAmount") + "\n今日还款总数 : " + repayInfo.get("repayNumber")
                    + "\n今日还款金额 : " + repayInfo.get("repayAmount") + "\n今日延期总数 : " + repayInfo.get("postponeNumber")
                    + "\n今日延期金额 : " + repayInfo.get("postponeAmount") + "\n逾期单量 : " + repayInfo.get("overdueNumber"));*/

            dingTalkService.sendText("今日放款限额 : " + loanInfo.get("maxAmount") + "\n今日放款总数 : " + loanInfo.get("loanNumber")
                    + "\n今日放款总金额 : " + loanInfo.get("loanAmount") + "\n今日还款总数 : " + repayInfo.get("repayNumber")
                    + "\n今日还款金额 : " + repayInfo.get("repayAmount") + "\n今日延期总数 : " + repayInfo.get("postponeNumber")
                    + "\n今日延期金额 : " + repayInfo.get("postponeAmount") + "\n逾期单量 : " + repayInfo.get("overdueNumber"));

            logger.info("系统定时器开启结束 ");
        }

    }


}
