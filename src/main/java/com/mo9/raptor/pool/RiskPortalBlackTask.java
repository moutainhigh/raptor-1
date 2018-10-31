package com.mo9.raptor.pool;

import com.mo9.raptor.service.DingTalkService;
import com.mo9.raptor.utils.RiskUtilsV2;
import com.mo9.raptor.utils.httpclient.HttpClientApi;
import com.mo9.raptor.utils.httpclient.bean.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xtgu on 2018/10/30.
 * @author xtgu
 * 通知江湖救急 黑名单
 */
@Component
public class RiskPortalBlackTask implements ThreadPoolTask{

    private static final Logger logger = LoggerFactory.getLogger(RiskPortalBlackTask.class);
    @Value("${risk.portal.black.url}")
    private String riskPortalBlackUrl ;
    @Value("${loan.name.en}")
    private String loanNameEn ;

    @Autowired
    private HttpClientApi httpClientApi ;

    @Resource
    private DingTalkService dingTalkService;

    @Autowired
    private RiskUtilsV2 riskUtilsV2 ;

    private String mobile;
    private String orderId;

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public void run() {
        Map<String , String> param = new HashMap<String , String>();
        param.put("mobile" , mobile) ;
        param.put("orderId" , orderId) ;
        param.put("channel" , loanNameEn) ;
        int num = 0 ;
        this.toBlack(num , param);
    }

    /**
     * 执行黑名单
     * @param num
     * @return
     */
    private void toBlack(int num , Map<String , String> param) {
        //输入 0 默认加 1 最大循环三次
        num++ ;
        try {
            if(num < 4){
                String result = httpClientApi.doGet(riskPortalBlackUrl , param) ;
                if("success".equals(result)){
                    logger.error("手机号 " + mobile + "江湖救急黑名单执行成功 " + orderId);
                }else{
                    logger.error("手机号 " + mobile + "江湖救急黑名单执行失败 第"+ num + "次" +orderId);
                    this.toBlack(num , param);
                }
            }
        } catch (Exception e) {
           logger.error("手机号 " + mobile + "江湖救急黑名单执行异常 第"+ num + "次"+orderId);
           this.toBlack(num , param);
        }
    }
}
