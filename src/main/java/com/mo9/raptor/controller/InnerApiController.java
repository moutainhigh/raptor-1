package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.res.MisOrderRes;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.enums.PayTypeEnum;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内部调用接口
 * 作废
 * Created by xzhang on 2018/9/19.
 */
@RestController()
@RequestMapping("/inner")
public class InnerApiController {

    private static Logger logger = Log.get();
    @Autowired
    private ILoanOrderService loanOrderService;

    @Autowired
    private IPayOrderService payOrderService;

    /**
     * 获取所有放款信息
     * @return
     */
    @GetMapping("/get_lent_info")
    public BaseResponse<JSONObject> add(
            @RequestParam("begin") Long begin,
            @RequestParam(value = "end", required = false) Long end) {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
//        if (end == null) {
//            end = System.currentTimeMillis();
//        }
//        List<LoanOrderEntity> loanOrderEntities = loanOrderService.listByRepaymentDate(begin, end);
//        if (loanOrderEntities == null || loanOrderEntities.size() == 0) {
//            response.setMessage("无符合要求的订单");
//            return response;
//        }
//        for (LoanOrderEntity loanOrderEntity : loanOrderEntities) {
//            MisOrderRes res = new MisOrderRes();
//            BeanUtils.copyProperties(loanOrderEntity, res);
//            if (StatusEnum.PAYOFF.name().equals(loanOrderEntity.getStatus())) {
//                res.setPayoffTime(loanOrderEntity.getUpdateTime());
//            }
//            List<PayOrderEntity> payOrderEntities = payOrderService.listByLoanOrderIdAndType(loanOrderEntity.getOrderId(), PayTypeEnum.REPAY_POSTPONE);
//            if (payOrderEntities != null) {
//                res.setPostponeCount(payOrderEntities.size());
//            }
//            // TODO: 发送mq
//        }

        return response;
    }

}
