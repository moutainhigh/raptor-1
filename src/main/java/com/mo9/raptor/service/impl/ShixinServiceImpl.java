package com.mo9.raptor.service.impl;

import com.mo9.raptor.repository.ShixinRepository;
import com.mo9.raptor.service.ShixinService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by jyou on 2018/10/22.
 *
 * @author jyou
 */
@Service
public class ShixinServiceImpl implements ShixinService{

    @Resource
    private ShixinRepository shixinRepository;

    @Override
    public long findByCardNumAndIname(String cardNum, String iname) {
        return shixinRepository.findByCardNumAndIname(cardNum, iname);
    }
}
