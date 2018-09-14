package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.ReqHeaderParams;
import com.mo9.raptor.bean.req.OrderAddReq;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.state.event.impl.AuditLaunchEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.utils.IDWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
/**
 * 还款
 * Created by xzhang on 2018/9/13.
 */
@RestController("/order")
public class LoanOrderController {

    private static final Logger logger = LoggerFactory.getLogger(LoanOrderController.class);

    @Autowired
    private IDWorker idWorker;

    @Autowired
    private ILoanOrderService loanOrderService;

    @Autowired
    private IEventLauncher loanEventLauncher;

    /**
     * 还清
     * @param req
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<JSONObject> repay(@Valid @RequestBody OrderAddReq req, HttpServletRequest request) {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        // TODO: 检查用户

        BigDecimal principal = req.getCapital();
        int loanTerm = req.getPeriod();

        /** TODO：业务相关值，如借贷服务费等，目前为新建订单时默认设定 */
        LoanOrderEntity loanOrder = new LoanOrderEntity();

        loanOrder.setOrderId(idWorker.nextId()+"");
        loanOrder.setOwnerId(userCode);
        loanOrder.setType("RAPTOR");
        loanOrder.setLoanNumber(principal);
        loanOrder.setLoanTerm(loanTerm);

        /** 创建借款订单 */
        loanOrderService.save(loanOrder);

        AuditLaunchEvent event = new AuditLaunchEvent(userCode, loanOrder.getOrderId());

        try {
            loanEventLauncher.launch(event);
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.code = 0;

        return response;
    }

}
