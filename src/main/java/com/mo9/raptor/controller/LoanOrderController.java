package com.mo9.raptor.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.bean.ReqHeaderParams;
import com.mo9.raptor.bean.req.OrderAddReq;
import com.mo9.raptor.bean.res.LoanOrderRes;
import com.mo9.raptor.engine.calculator.ILoanCalculator;
import com.mo9.raptor.engine.calculator.LoanCalculatorFactory;
import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.state.event.impl.AuditLaunchEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.engine.utils.EngineStaticValue;
import com.mo9.raptor.engine.utils.TimeUtils;
import com.mo9.raptor.enums.ProductEnum;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.utils.IDWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
/**
 * 还款
 * Created by xzhang on 2018/9/13.
 */
@RestController()
@RequestMapping("/order")
public class LoanOrderController {

    private static final Logger logger = LoggerFactory.getLogger(LoanOrderController.class);

    @Autowired
    private IDWorker idWorker;

    @Autowired
    private ILoanOrderService loanOrderService;

    @Autowired
    private ILendOrderService lendOrderService;

    @Autowired
    private IEventLauncher loanEventLauncher;

    @Autowired
    private LoanCalculatorFactory loanCalculatorFactory;

    @Value("${raptor.sockpuppet}")
    private String sockpuppet;

    @Value("${postpone.unit.charge}")
    private String postponeCharge;

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

        // TODO: 检查输入
        BigDecimal principal = req.getCapital();
        int loanTerm = req.getPeriod();
        if (!(ProductEnum.checkLoanDays(loanTerm) && ProductEnum.checkPrincipal(principal))) {
            return response.buildFailureResponse(ResCodeEnum.ERROR_LOAN_PARAMS);
        }

        LoanOrderEntity loanOrder = new LoanOrderEntity();
        String orderId = sockpuppet + "-" + idWorker.nextId();
        loanOrder.setOrderId(orderId);
        loanOrder.setOwnerId(userCode);
        loanOrder.setType("RAPTOR");
        loanOrder.setLoanNumber(principal);
        loanOrder.setPostponeUnitCharge(new BigDecimal(postponeCharge));
        loanOrder.setLoanTerm(loanTerm);
        loanOrder.setStatus(StatusEnum.PENDING.name());
        long now = System.currentTimeMillis();
        Long today = TimeUtils.extractDateTime(now);
        loanOrder.setRepaymentDate(today + loanTerm * EngineStaticValue.DAY_MILLIS);
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
        LoanOrderEntity loanOrderEntity = loanOrderService.getLastIncompleteOrder(userCode);
        if (loanOrderEntity == null) {
            return response;
        }
        ILoanCalculator calculator = loanCalculatorFactory.load(loanOrderEntity);
        Item realItem = calculator.realItem(System.currentTimeMillis(), loanOrderEntity);

        LoanOrderRes res = new LoanOrderRes();
        res.setOrderId(loanOrderEntity.getOrderId());
        res.setRepayAmount(realItem.sum().toPlainString());
        res.setRepayTime(loanOrderEntity.getRepaymentDate());
        res.setState(loanOrderEntity.getStatus());
        res.setAbateAmount("0");
        LendOrderEntity lendOrderEntity = lendOrderService.getByOrderId(loanOrderEntity.getOrderId());
        res.setReceiveBankCard(lendOrderEntity.getBankCard());
        res.setRenew(calculator.getRenew(loanOrderEntity));
        return response.buildSuccessResponse(res);
    }

}
