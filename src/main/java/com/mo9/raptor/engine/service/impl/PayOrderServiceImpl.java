package com.mo9.raptor.engine.service.impl;

import com.mo9.raptor.bean.condition.FetchPayOrderCondition;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.service.IPayOrderService;
import com.mo9.raptor.engine.state.event.impl.AuditLaunchEvent;
import com.mo9.raptor.engine.state.event.impl.pay.DeductResponseEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.entity.PayOrderLogEntity;
import com.mo9.raptor.enums.PayTypeEnum;
import com.mo9.raptor.enums.ResCodeEnum;
import com.mo9.raptor.repository.PayOrderRepository;
import com.mo9.raptor.service.PayOrderLogService;
import com.mo9.raptor.utils.CommonValues;
import com.mo9.raptor.utils.GatewayUtils;
import com.mo9.raptor.utils.SeekerUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xzhang on 2018/7/10.
 */
@Service("payOrderServiceImpl")
public class PayOrderServiceImpl implements IPayOrderService {

    private static final Logger logger = LoggerFactory.getLogger(PayOrderServiceImpl.class);

    @Autowired
    private PayOrderRepository payOrderRepository;

    @Autowired
    private PayOrderLogService payOrderLogService;

    @Autowired
    private IEventLauncher payEventLauncher;

    @Autowired
    private GatewayUtils gatewayUtils;

    @Autowired
    private SeekerUtils seekerUtils;

    /**
     * 支付版本号
     */
    @Value("${raptor.pay.version}")
    private String raptorPayVersion;

    @Override
    public List<PayOrderEntity> listEntryDonePayLoan(String loanOrderId, List<String> types) {
        return payOrderRepository.listEntryDonePayLoan(loanOrderId, types);
    }

    @Override
    public PayOrderEntity getByOrderId(String payOrderId) {
        return payOrderRepository.getByOrderId(payOrderId);
    }

    @Override
    public List<PayOrderEntity> listByOrderIds(List<String> payOrderIds) {
        return payOrderRepository.listByOrderIds(payOrderIds);
    }

    @Override
    public List<PayOrderEntity> listByLoanOrderIdAndType(String loanOrderId, PayTypeEnum payType) {
        return payOrderRepository.listByLoanOrderIdAndType(loanOrderId, payType.name());
    }

    @Override
    public List<PayOrderEntity> listByUser(String userCode) {
        return payOrderRepository.listByUser(userCode);
    }

    @Override
    public List<PayOrderEntity> listByUserAndStatus(String userCode, List<String> statuses) {
        return payOrderRepository.listByUserAndStatus(userCode, statuses);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(PayOrderEntity payOrder) {
        payOrderRepository.save(payOrder);
    }

    @Override
    public Page<PayOrderEntity> listPayOrderByCondition(final FetchPayOrderCondition condition) {
        //规格定义
        Specification<PayOrderEntity> specification = new Specification<PayOrderEntity>() {
            @Override
            public Predicate toPredicate(Root<PayOrderEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>(10);
                if(condition.getUserCode() != null){
                    list.add(cb.equal(root.get("ownerId").as(String.class) , condition.getUserCode()));
                }
                if (StringUtils.isNotBlank(condition.getLoanOrderNumber())) {
                    list.add(cb.equal(root.get("loanOrderId").as(String.class) , condition.getLoanOrderNumber()));
                }
                if(condition.getFromTime() != null){
                    list.add(cb.ge(root.get("createTime").as(Long.class) , condition.getFromTime()));
                }
                if(condition.getToTime() != null){
                    list.add(cb.le(root.get("createTime").as(Long.class) , condition.getToTime()));
                }
                if(condition.getPayTypes() != null && condition.getPayTypes().size() > 0){
                    List<PayTypeEnum> payTypes = condition.getPayTypes();
                    CriteriaBuilder.In<String> in = cb.in(root.get("type").as(String.class));
                    for (PayTypeEnum payType : payTypes) {
                        in.value(payType.name());
                    }
                    list.add(in);
                }
                if(condition.getRepaymentOrderNumber() != null && condition.getRepaymentOrderNumber().size() > 0){
                    List<String> repaymentOrderNumbers = condition.getRepaymentOrderNumber();
                    CriteriaBuilder.In<String> in = cb.in(root.get("orderId").as(String.class));
                    for (String repaymentOrderNumber : repaymentOrderNumbers) {
                        in.value(repaymentOrderNumber);
                    }
                    list.add(in);
                }
                if(condition.getStates() != null && condition.getStates().size() > 0){
                    List<StatusEnum> payStates = condition.getPayState();
                    CriteriaBuilder.In<String> in = cb.in(root.get("status").as(String.class));
                    for (StatusEnum statusEnum : payStates) {
                        in.value(statusEnum.name());
                    }
                    list.add(in);
                }
                Predicate[] predicates = new Predicate[list.size()];
                predicates = list.toArray(predicates);
                return cb.and(predicates);
            }
        };
        //分页信息
        Page<PayOrderEntity> page = null;
        if (condition.getPageNumber() != null && condition.getPageSize() != null) {
            //分页信息
            Pageable pageable = PageRequest.of(condition.getPageNumber() - 1, condition.getPageSize());
            page = payOrderRepository.findAll(specification , pageable);
        } else {
            List<PayOrderEntity> loanOrderEntityList = payOrderRepository.findAll(specification);
            page = new PageImpl<PayOrderEntity>(loanOrderEntityList);
        }

        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePayOrderAndLogAndNotice(PayOrderEntity payOrder, PayOrderLogEntity payOrderLog) {
        this.save(payOrder);
        payOrderLogService.save(payOrderLog);

        try {
            AuditLaunchEvent event = new AuditLaunchEvent(payOrder.getOwnerId(), payOrder.getOrderId());
            payEventLauncher.launch(event);
        } catch (Exception e) {
            logger.error("还款订单[{}]审核事件错误", payOrder.getOrderId(), e);
        }
    }

    @Override
    public void savePayOrderAndLog(PayOrderEntity payOrder, PayOrderLogEntity payOrderLog) {
        this.save(payOrder);
        payOrderLogService.save(payOrderLog);
    }

    @Override
    public void repayNotice(String payOrderId) {
        PayOrderLogEntity payOrderLog = payOrderLogService.getByPayOrderId(payOrderId);
        /***************** 2018-11-08 顾晓桐增加版本 -- 支付中心********************/
        ResCodeEnum isPayoff = ResCodeEnum.SUCCESS ;
        if(raptorPayVersion.equals(CommonValues.SEEKER_PAY_VERSION)){
            //支付中心版本
            isPayoff = seekerUtils.payoff(payOrderLog);
        }else{
            isPayoff = gatewayUtils.payoff(payOrderLog);
        }
        /********************************************************************/
        if (!ResCodeEnum.SUCCESS.equals(isPayoff)) {
            try {
                DeductResponseEvent event= new DeductResponseEvent(payOrderId, BigDecimal.ZERO, false, System.currentTimeMillis() + ":扣款失败");
                payEventLauncher.launch(event);
            } catch (Exception e) {
                logger.error("还款订单[{}]扣款失败事件错误", payOrderId, e);
            }
        }
    }

    @Override
    public List<PayOrderEntity> findByStatus(String status) {
        return payOrderRepository.findByStatus(status);
    }
}
