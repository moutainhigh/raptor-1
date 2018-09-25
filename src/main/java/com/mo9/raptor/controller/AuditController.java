package com.mo9.raptor.controller;

import com.mo9.raptor.bean.BaseResponse;
import com.mo9.raptor.engine.state.action.impl.user.UserAuditAction;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.repository.UserRepository;
import com.mo9.raptor.risk.service.RiskAuditService;
import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zma
 * @date 2018/9/13
 */
@RestController
@RequestMapping(value = "/audit")
public class AuditController {

    private static Logger logger = Log.get();

    @Autowired
    private IEventLauncher userEventLauncher;

    @Autowired
    private RiskAuditService riskAuditService;

    @Resource
    private UserRepository userRepository;

    @RequestMapping(value = "/resendauditing")
    public BaseResponse resend() {
        logger.info("开始主动审核状态为AUDITING的用户");
        List<UserEntity> auditing = userRepository.findByStatus("AUDITING");
        for (UserEntity userEntity : auditing) {
            UserAuditAction userAuditAction = new UserAuditAction(userEntity.getUserCode(), userEventLauncher, riskAuditService);
            userAuditAction.run();
        }
        return new BaseResponse("ok");
    }
}
