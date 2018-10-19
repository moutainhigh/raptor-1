package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.mqclient.MqMessage;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.ReqHeaderParams;
import com.mo9.raptor.engine.entity.CouponEntity;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.BillService;
import com.mo9.raptor.engine.service.CouponService;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.engine.utils.EngineStaticValue;
import com.mo9.raptor.engine.utils.TimeUtils;
import com.mo9.raptor.entity.PayOrderLogEntity;
import com.mo9.raptor.entity.RabbitProducerMqEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.enums.CaptchaBusinessEnum;
import com.mo9.raptor.enums.CurrencyEnum;
import com.mo9.raptor.enums.PayTypeEnum;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.mq.listen.LoanMo9mqListener;
import com.mo9.raptor.mq.producer.RabbitProducer;
import com.mo9.raptor.redis.RedisParams;
import com.mo9.raptor.redis.RedisServiceApi;
import com.mo9.raptor.service.PayOrderLogService;
import com.mo9.raptor.service.RabbitProducerMqService;
import com.mo9.raptor.service.UserService;
import com.mo9.raptor.utils.IDWorker;
import com.mo9.raptor.utils.Md5Encrypt;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author zma
 * @date 2018/9/17
 */
@RestController
@RequestMapping(value = "/test")
public class TestController {

    private static Logger logger = Log.get();
    @Resource
    private RedisServiceApi redisServiceApi;

    @Resource
    private UserService userService;

    @Autowired
    private ILoanOrderService loanOrderService;

    @Autowired
    private RabbitProducerMqService rabbitProducerMqService;

    @Autowired
    private PayOrderLogService payOrderLogService;

    @Autowired
    private LoanMo9mqListener loanMo9mqListener;

    @Autowired
    private IPayOrderService payOrderService;

    @Resource(name = "raptorRedis")
    private RedisTemplate raptorRedis;

    @Value("${test.open}")
    private String testOpen = "false";

    @Value("${raptor.sockpuppet}")
    private String sockpuppet;

    @Value("${loan.name.cn}")
    private String loanNameCn ;

    /**
     * 获取短信验证码
     * @param request
     * @param receive
     * @return
     */
    @RequestMapping("/fetch_mobile_verification_code")
    public BaseResponse fetchSmsVerificationCode(HttpServletRequest request, @RequestParam("mobile")String receive){
        BaseResponse response = new BaseResponse();
        if(!Boolean.valueOf(testOpen)){
            return response.buildFailureResponse(ResCodeEnum.TEST_OPEN_CLOSE) ;
        }
        String redisKey = getRedisKey(RedisParams.MOBILE_CAPTCHA_KEY, receive, CaptchaBusinessEnum.LOGIN);
        String redisCaptcha = (String) redisServiceApi.get(redisKey, raptorRedis);
        JSONObject codeData = new JSONObject();
        codeData.put("code",redisCaptcha);
        return new BaseResponse("手机验证码",codeData);
    }

    /**
     * 修改用户状态
     */
    @RequestMapping("/update_user_status")
    public BaseResponse<UserEntity> updateUserStatus(HttpServletRequest request, @RequestParam("status") StatusEnum status){
        BaseResponse<UserEntity> response = new BaseResponse<UserEntity>();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        try{
            if(!Boolean.valueOf(testOpen)){
                return response.buildFailureResponse(ResCodeEnum.TEST_OPEN_CLOSE) ;
            }
            UserEntity userEntity = userService.findByUserCodeAndDeleted(userCode, false);
            userEntity.setStatus(status.name());
            userEntity.setRemark("测试接口修改用户状态为:" + status.name());
            userService.save(userEntity);
            logger.info("测试修改用户状态为status={}", status);
            return response.buildSuccessResponse(userEntity);
        }catch (Exception e){
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
    }

    @RequestMapping("/test")
    public String test(){
        return "1";
    }

    @RequestMapping("/getNameCn")
    public String getNameCn(){
        return loanNameCn;
    }

    private String getRedisKey(String redisKey, String receive, CaptchaBusinessEnum businessCode){
        return redisKey + businessCode + "_" + receive;
    }

    /**
     * 推送mq
     */
    @RequestMapping("/mq")
    public BaseResponse<JSONObject> mq(@RequestParam("sign") String sign, HttpServletRequest request){
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        if (!"28B21099FBDD85467CC01E7B80146FF0".equals(sign)) {
            response.setMessage("验签错误");
            return response;
        }

        List<PayOrderEntity> payOrderEntities = payOrderService.findByStatus(StatusEnum.ENTRY_DONE.name());
        for (PayOrderEntity payOrderEntity : payOrderEntities) {
            LoanOrderEntity loanOrderEntity = loanOrderService.getByOrderId(payOrderEntity.getLoanOrderId());

            PayOrderLogEntity payOrderLogEntity = payOrderLogService.getByPayOrderId(payOrderEntity.getOrderId());

            loanMo9mqListener.notifyMisRepay(payOrderLogEntity, loanOrderEntity.getPostponeCount(), loanOrderEntity);
        }

//        List<LoanOrderEntity> loanOrderEntities = loanOrderService.listByStatus(Arrays.asList(StatusEnum.LENT));
//        if (loanOrderEntities == null || loanOrderEntities.size() == 0) {
//            response.setMessage("无订单");
//            return response;
//        }
//
//        for (LoanOrderEntity loanOrderEntity : loanOrderEntities) {
//            RabbitProducerMqEntity producerMqEntity = rabbitProducerMqService.findByMessageKey(loanOrderEntity.getOrderId());
//            if (producerMqEntity != null) {
//                logger.info("订单[{}]已发送过mq, 跳过", loanOrderEntity.getOrderId());
//                continue;
//            }
//            loanMo9mqListener.notifyMisLend(loanOrderEntity.getOrderId());
//        }
        response.setMessage("ok");
        return response;
    }

    /**
     * 推送指定的mq
     */
    @RequestMapping("/mq2")
    public BaseResponse<JSONObject> mq2(@RequestParam("sign") String sign, HttpServletRequest request){
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        if (!"28B21099FBDD85467CC01E7B80146FF0".equals(sign)) {
            response.setMessage("验签错误");
            return response;
        }

        loanMo9mqListener.notifyMisLend("TTYQ-230353561840652288");
        loanMo9mqListener.notifyMisLend("TTYQ-230351599736852480");
        loanMo9mqListener.notifyMisLend("TTYQ-230352143721304064");

        response.setMessage("ok");
        return response;
    }

    /**
     * 推送还清的借款订单
     */
    @RequestMapping("/mq3")
    public BaseResponse<JSONObject> mq3(@RequestParam("sign") String sign, HttpServletRequest request){
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        if (!"28B21099FBDD85467CC01E7B80146FF0".equals(sign)) {
            response.setMessage("验签错误");
            return response;
        }

        List<LoanOrderEntity> loanOrderEntities = loanOrderService.listByStatus(StatusEnum.OLD_PAYOFF);
        if (loanOrderEntities == null || loanOrderEntities.size() == 0) {
            response.setMessage("无订单");
            return response;
        }

        for (LoanOrderEntity loanOrderEntity : loanOrderEntities) {
            RabbitProducerMqEntity producerMqEntity = rabbitProducerMqService.findByMessageKey(loanOrderEntity.getOrderId());
            if (producerMqEntity != null) {
                logger.info("订单[{}]已发送过mq, 跳过", loanOrderEntity.getOrderId());
                continue;
            }
            loanMo9mqListener.notifyMisLend(loanOrderEntity.getOrderId());
        }

        response.setMessage("ok");
        return response;
    }

    @RequestMapping("/push_lent_mq")
    public BaseResponse<JSONObject> pushLentMq(
            @RequestParam("sign") String sign,
            @RequestParam("orderId") String orderId,
            HttpServletRequest request){
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        if (!"28B21099FBDD85467CC01E7B80146FF0".equals(sign)) {
            response.setMessage("验签错误");
            return response;
        }
        LoanOrderEntity loanOrderEntity = loanOrderService.getByOrderId(orderId);
        if (loanOrderEntity == null || !StatusEnum.LENT.name().equals(loanOrderEntity.getStatus())) {
            response.setMessage("无订单");
            return response;
        }

        loanMo9mqListener.notifyMisLend(orderId);

        response.setMessage("ok");
        return response;
    }

}
