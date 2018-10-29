package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.ThridBlackEntity;
import com.mo9.raptor.repository.ThridBlackRepository;
import com.mo9.raptor.risk.black.channel.KeMi;
import com.mo9.raptor.service.ThridBlackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by jyou on 2018/10/25.
 *
 * @author jyou
 */
@Service(value = "thridBlackService")
public class ThridBlackServiceImpl implements ThridBlackService {
    private static Logger logger = LoggerFactory.getLogger(KeMi.class);
    @Resource
    private ThridBlackRepository thridBlackRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(ThridBlackEntity thridBlack) {
        thridBlackRepository.save(thridBlack);
    }
}
