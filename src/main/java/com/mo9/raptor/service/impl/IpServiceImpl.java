package com.mo9.raptor.service.impl;

import com.mo9.raptor.entity.IpEntity;
import com.mo9.raptor.repository.IpRepository;
import com.mo9.raptor.service.IpService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by jyou on 2018/10/22.
 *
 * @author jyou
 */
@Service
public class IpServiceImpl implements IpService{

    @Resource
    private IpRepository ipRepository;

    @Override
    public List<IpEntity> findByIpNum(Long ipNum) {
        return ipRepository.findByIpNum(ipNum);
    }
}
