package com.mo9.raptor.pool;

import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.risk.service.RiskContractInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by jyou on 2018/10/18.
 *
 * @author jyou
 */
@Component
public class RiskContractTask implements ThreadPoolTask {

    @Resource
    private RiskContractInfoService riskContractInfoService;

    private String data;

    private UserEntity userEntity;

   public void build(String data, UserEntity userEntity){
       this.data = data;
       this.userEntity = userEntity;
   }

    @Override
    public void run() {
        riskContractInfoService.createAll(data, userEntity);
    }
}
