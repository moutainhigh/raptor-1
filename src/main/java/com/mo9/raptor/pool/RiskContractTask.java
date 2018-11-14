package com.mo9.raptor.pool;

import com.mo9.raptor.entity.UserEntity;
import com.mo9.risk.app.entity.User;
import com.mo9.risk.service.RiskContractInfoService;
import org.springframework.beans.BeanUtils;
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
        User user = new User();
        BeanUtils.copyProperties(this.userEntity, user);
        riskContractInfoService.createAll(data, user);
    }
}
