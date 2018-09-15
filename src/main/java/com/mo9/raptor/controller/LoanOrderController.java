package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.ReqHeaderParams;
import com.mo9.raptor.bean.req.OrderAddReq;
import com.mo9.raptor.bean.res.LoanOrderRes;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.state.event.impl.AuditLaunchEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.utils.IDWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Value("${raptor.sockpuppet}")
    private String sockpuppet;

    /**
     * 下单
     * @param req
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<JSONObject> repay(@Valid @RequestBody OrderAddReq req, HttpServletRequest request) {
        BaseResponse<JSONObject> response = new BaseResponse<JSONObject>();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);
        // TODO: 检查用户

        LoanOrderEntity loanOrderEntity = loanOrderService.getLastIncompleteOrder(userCode);
        if (loanOrderEntity != null) {
            return response.buildFailureResponse(ResCodeEnum.ONLY_ONE_ORDER);
        }

        BigDecimal principal = req.getCapital();
        int loanTerm = req.getPeriod();

        /** TODO：业务相关值，如借贷服务费等，目前为新建订单时默认设定 */
        LoanOrderEntity loanOrder = new LoanOrderEntity();

        String orderId = sockpuppet + "-" + idWorker.nextId();
        loanOrder.setOrderId(orderId);
        loanOrder.setOwnerId(userCode);
        loanOrder.setType("RAPTOR");
        loanOrder.setLoanNumber(principal);
        loanOrder.setLoanTerm(loanTerm);
        loanOrder.setStatus(StatusEnum.PENDING.name());
        long now = System.currentTimeMillis();
        loanOrder.setCreateTime(now);
        loanOrder.setUpdateTime(now);
        /** 创建借款订单 */
        loanOrderService.save(loanOrder);
        try {
            AuditLaunchEvent event = new AuditLaunchEvent(userCode, loanOrder.getOrderId());
            loanEventLauncher.launch(event);
        } catch (Exception e) {
            logger.error("借款订单[{}]审核出错", orderId, e);
            return response.buildFailureResponse(ResCodeEnum.EXCEPTION_CODE);
        }
        return response;
    }

    /**
     * 获取上一笔未还清订单
     * @param request
     * @return
     */
    @GetMapping("/get_last_incomplete")
    public BaseResponse<LoanOrderRes> getLastIncomplete(HttpServletRequest request) {
        BaseResponse<LoanOrderRes> response = new BaseResponse<LoanOrderRes>();
        String userCode = request.getHeader(ReqHeaderParams.ACCOUNT_CODE);

        return response;
    }

}
