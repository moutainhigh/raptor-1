package com.mo9.raptor.engine.service.impl;

import com.mo9.raptor.bean.condition.FetchLoanOrderCondition;
import com.mo9.raptor.engine.entity.LendOrderEntity;
import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.enums.StatusEnum;
import com.mo9.raptor.engine.repository.LoanOrderRepository;
import com.mo9.raptor.engine.service.ILendOrderService;
import com.mo9.raptor.engine.service.ILoanOrderService;
import com.mo9.raptor.engine.state.event.impl.AuditLaunchEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.engine.utils.EngineStaticValue;
import com.mo9.raptor.engine.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xzhang on 2018/7/8.
 */
@Service("loanOrderService")
public class LoanOrderServiceImpl implements ILoanOrderService {

    private static final Logger logger = LoggerFactory.getLogger(LoanOrderServiceImpl.class);

    @Autowired
    private LoanOrderRepository loanOrderRepository;

    @Autowired
    private ILendOrderService lendOrderService;

    @Autowired
    private IEventLauncher loanEventLauncher;

    @Override
    public LoanOrderEntity getByOrderId(String orderId) {
        return loanOrderRepository.getByOrderId(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoanOrderEntity save(LoanOrderEntity loanOrder) {
         return loanOrderRepository.save(loanOrder);
    }

    @Override
    public Page<LoanOrderEntity> listLoanOrderByCondition(final FetchLoanOrderCondition condition) {

        //规格定义
        Specification<LoanOrderEntity> specification = new Specification<LoanOrderEntity>() {
            @Override
            public Predicate toPredicate(Root<LoanOrderEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
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
                if(condition.getLoanOrderState() != null && condition.getLoanOrderState().size() > 0){
                    List<StatusEnum> loanOrderStates = condition.getLoanOrderState();
                    CriteriaBuilder.In<String> in = cb.in(root.get("status").as(String.class));
                    for (StatusEnum loanOrderState : loanOrderStates) {
                        in.value(loanOrderState.name());
                    }
                    list.add(in);
                }
                Predicate[] predicates = new Predicate[list.size()];
                predicates = list.toArray(predicates);
                return cb.and(predicates);
            }
        };

        Page<LoanOrderEntity> page = null;
        if (condition.getPageNumber() != null && condition.getPageSize() != null) {
            //分页信息
            Pageable pageable = PageRequest.of(condition.getPageNumber() - 1, condition.getPageSize());
            page = loanOrderRepository.findAll(specification , pageable);
        } else {
            List<LoanOrderEntity> loanOrderEntityList = loanOrderRepository.findAll(specification);
            page = new PageImpl<LoanOrderEntity>(loanOrderEntityList);
        }
        return page;
    }

    @Override
    public LoanOrderEntity getLastIncompleteOrder(String userCode) {
        return loanOrderRepository.getLastIncompleteOrder(userCode);
    }

    @Override
    public LoanOrderEntity getLastIncompleteOrder(String userCode, List<String> processing) {
        return loanOrderRepository.getLastIncompleteOrder(userCode, processing);
    }

    @Override
    public List<LoanOrderEntity> listByRepaymentDate(Long begin, Long end) {
        return loanOrderRepository.listByRepaymentDate(begin, end);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveLendOrder(LoanOrderEntity loanOrder, LendOrderEntity lendOrder) throws Exception {
        lendOrderService.save(lendOrder);
        this.save(loanOrder);
        AuditLaunchEvent event = new AuditLaunchEvent(loanOrder.getOwnerId(), loanOrder.getOrderId());
        loanEventLauncher.launch(event);
    }

    @Override
    public List<LoanOrderEntity> listByStatus(List<String> statusEnums) {
        return loanOrderRepository.listByStatus(statusEnums);
    }

    @Override
    public List<LoanOrderEntity> listByUserAndStatus(String userCode, List<String> statusEnums) {
        return loanOrderRepository.listByUserAndStatus(userCode, statusEnums);
    }

    @Override
    public List<LoanOrderEntity> listShouldPayOrder() {
        Long today = TimeUtils.extractDateTime(System.currentTimeMillis());
        Long tomorrow = today + EngineStaticValue.DAY_MILLIS;
        return loanOrderRepository.listShouldPayOrder(today, tomorrow);
    }
}
