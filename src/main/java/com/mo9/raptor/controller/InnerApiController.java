package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.req.OrderAddReq;
import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.engine.service.ILoanOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 内部调用接口
 * Created by xzhang on 2018/9/19.
 */
@RestController()
@RequestMapping("/inner")
public class InnerApiController {


    @Autowired
    private ILoanOrderService loanOrderService;

    @Autowired
    private ILendOrderService lendOrderService;

    /**
     * 获取所有放款信息
     * @return
     */
    @GetMapping("/get_lent_info")
    public BaseResponse<JSONObject> add(
            @RequestParam("begin") Long begin,
            @RequestParam(value = "end", required = false) Long end) {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        if (end == null) {
            end = System.currentTimeMillis();
        }
        List<LoanOrderEntity> loanOrderEntities = loanOrderService.listByRepaymentDate(begin, end);
        if (loanOrderEntities == null || loanOrderEntities.size() == 0) {
            response.setMessage("无符合要求的订单");
            return response;
        }
        for (LoanOrderEntity loanOrderEntity : loanOrderEntities) {
            String orderId = loanOrderEntity.getOrderId();
            LendOrderEntity lendOrder = lendOrderService.getByOrderId(orderId);

        }




        return response;
    }

}
