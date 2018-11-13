package com.mo9.raptor.engine.service.impl;

import com.mo9.raptor.bean.condition.CouponCondition;
import com.mo9.raptor.engine.entity.CouponEntity;
import com.mo9.raptor.engine.repository.CouponRespository;
import com.mo9.raptor.engine.service.CouponService;
import com.mo9.raptor.entity.CashAccountLogEntity;
import com.mo9.raptor.enums.BalanceTypeEnum;
import com.mo9.raptor.enums.BusinessTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 优惠券service
 * Created by xzhang on 2018/9/28.
 */
@Service("couponServiceImpl")
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponRespository couponRespository;

    @Override
    public CouponEntity getByCouponId(String couponId) {
        return couponRespository.getByCouponId(couponId);
    }

    @Override
    public BigDecimal getTotalDeductedAmount(String orderId) {
        Map<String, BigDecimal> totalDeductedAmount = couponRespository.getTotalDeductedAmount(orderId);
        return totalDeductedAmount.get("totalEntryAmount");
    }

    @Override
    public List<CouponEntity> getByPayOrderId(String payOrderId) {
        return couponRespository.getByPayOrderId(payOrderId);
    }

    @Override
    public List<CouponEntity> findByUserCodeNotDelete(String userCode) {
        return couponRespository.findByUserCodeNotDelete(userCode);
    }

    @Override
    public Page<CouponEntity> findByCondition(final CouponCondition condition) {
        //规格定义
        Specification<CouponEntity> specification = new Specification<CouponEntity>() {
            @Override
            public Predicate toPredicate(Root<CouponEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>(10);
                list.add(cb.equal(root.get("userCode").as(String.class),condition.getUserCode()));
                list.add(cb.equal(root.get("deleted").as(Boolean.class),false));
                if(condition.getExpiryDate() != null){
                    list.add(cb.ge(root.get("expiryDate").as(Long.class), condition.getExpiryDate()));
                }

                if(condition.getStatusList() != null && condition.getStatusList().size() > 0){
                    List<String> statusList = condition.getStatusList() ;
                    CriteriaBuilder.In<String> in = cb.in(root.get("status").as(String.class));
                    for (String status : statusList) {
                        in.value(status);
                    }
                    list.add(in);
                }

                Predicate[] predicates = new Predicate[list.size()];
                predicates = list.toArray(predicates);
                return cb.and(predicates);
            }
        };
        //分页信息
        Pageable pageable = PageRequest.of(condition.getPageNumber()-1, condition.getPageSize(), Sort.Direction.DESC, "apply_amount");
        //查询
        return couponRespository.findAll(specification , pageable);
    }

    @Override
    public CouponEntity getEffectiveBundledCoupon(String loanOrderId) {
        return couponRespository.getBundledCoupon(loanOrderId, System.currentTimeMillis());
    }

    @Override
    public CouponEntity save(CouponEntity couponEntity) {
        return couponRespository.save(couponEntity);
    }
}
