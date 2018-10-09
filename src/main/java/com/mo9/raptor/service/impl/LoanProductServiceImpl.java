package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.LoanProductEntity;
import com.mo9.raptor.repository.LoanProductRepository;
import com.mo9.raptor.service.LoanProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by xtgu on 2018/9/16.
 * @author xtgu
 */
@Service
public class LoanProductServiceImpl implements LoanProductService {

    @Autowired
    private LoanProductRepository productRepository ;

    @Override
    public List<LoanProductEntity> findNotDelete() {
        return productRepository.findNotDelete();
    }

    @Override
    public List<LoanProductEntity> findByAmount(BigDecimal amount) {
        return productRepository.findByAmount(amount);
    }

    @Override
    public LoanProductEntity findByAmountAndPeriod(BigDecimal amount, Integer period) {
        return productRepository.findByAmountAndPeriod(amount, period);
    }
}
