package com.mo9.raptor.service.impl;

import com.mo9.raptor.bean.condition.FetchPayOrderCondition;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.enums.PayTypeEnum;
import com.mo9.raptor.repository.PayOrderRepository;
import com.mo9.raptor.service.IPayOrderService;
import com.mo9.raptor.utils.IDWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by xzhang on 2018/7/10.
 */
@Service("payOrderServiceImpl")
public class PayOrderServiceImpl implements IPayOrderService {

    private static final Logger logger = LoggerFactory.getLogger(PayOrderServiceImpl.class);

    @Autowired
    PayOrderRepository payOrderRepository;

    @Autowired
    IDWorker idWorker;

    @Override
    public PayOrderEntity getByOrderId(String payOrderId) {
        return payOrderRepository.getByOrderId(payOrderId);
    }

    @Override
    public List<PayOrderEntity> listByOrderIds(List<String> payOrderIds) {
        return payOrderRepository.listByOrderIds(payOrderIds);
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
        Pageable pageable = PageRequest.of(condition.getPageNumber() - 1, condition.getPageSize());
        //查询
        return payOrderRepository.findAll(specification , pageable);
    }
}
