package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.req.CashRenewalReq;
import com.mo9.raptor.bean.req.CashRepayReq;
import com.mo9.raptor.bean.res.ChannelDetailRes;
import com.mo9.raptor.enums.RepayChannelTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 还款
 * Created by xzhang on 2018/9/13.
 */
@RestController("/cash")
public class PayOrderController {

    private static final Logger logger = LoggerFactory.getLogger(PayOrderController.class);

    /**
     * TODO:还清
     * @param req
     * @return
     */
    @PostMapping("/repay")
    public BaseResponse<JSONObject> repay(@Valid @RequestBody CashRepayReq req) {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();

        return response;
    }

    /**
     * TODO: 续期
     * @param req
     * @return
     */
    @PostMapping("/renewal")
    public BaseResponse<JSONObject> renewal(@Valid @RequestBody CashRenewalReq req) {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();

        return response;
    }

    /**
     * 获取渠道列表
     * @return
     */
    @GetMapping("/get_repay_channels")
    public BaseResponse<JSONObject> getRepayChannels () {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        List<ChannelDetailRes> channels = new ArrayList<ChannelDetailRes>();
        for (RepayChannelTypeEnum channelType : RepayChannelTypeEnum.values()) {
            ChannelDetailRes res = new ChannelDetailRes();
            res.setChannelName(channelType.getChannelName());
            res.setChannelType(channelType.getChannelType());
            res.setUseType(channelType.getChannelUseType().getDesc());
            channels.add(res);
        }
        JSONObject data = new JSONObject();
        data.put("entities", channels);
        return response.buildSuccessResponse(data);
    }

}
