package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.req.CashRenewalReq;
import com.mo9.raptor.bean.req.CashRepayReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 还款
 * Created by xzhang on 2018/9/13.
 */
@RestController("/cash")
public class PayOrderController {

    private static final Logger logger = LoggerFactory.getLogger(PayOrderController.class);


    /**
     * 还清
     * @param req
     * @return
     */
    @PostMapping("repay")
    public BaseResponse<JSONObject> repay(@Valid CashRepayReq req) {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();

        return response;
    }

    /**
     * 续期
     * @param req
     * @return
     */
    @PostMapping("renewal")
    public BaseResponse<JSONObject> renewal(@Valid CashRenewalReq req) {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();

        return response;
    }

}
