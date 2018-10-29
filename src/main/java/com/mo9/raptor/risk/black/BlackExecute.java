package com.mo9.raptor.risk.black;

import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.risk.black.channel.KeMi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by jyou on 2018/10/25.
 *
 * @author jyou
 */
public class BlackExecute {

    private static Logger logger = LoggerFactory.getLogger(BlackExecute.class);

    private Set<BlackChannel> channelSet;

    private UserEntity userEntity;

    public BlackExecute(UserEntity userEntity) {
        this.userEntity = userEntity;
        this.channelSet = new HashSet<>();
        this.channelSet.add(new KeMi());
    }

    public AuditResponseEvent execute(){

        if(this.channelSet == null || this.channelSet.size() == 0){
            return new AuditResponseEvent(this.userEntity.getUserCode(), true, "");
        }

        for(BlackChannel channel : this.channelSet){
            if(!channel.channelIsOpen()){
                logger.info("第三方黑名单渠道检查，当前渠道未开启，检查渠道名称={}，userCode={}", channel.getClass().getName(), userEntity.getUserCode());
                continue;
            }
            AuditResponseEvent responseEvent = channel.doBlackCheck(this.userEntity);
            if(!responseEvent.isPass()){
                logger.info("第三方黑名单渠道检查，用户未通过，检查渠道名称={}，userCode={}", channel.getClass().getName(), userEntity.getUserCode());
                return responseEvent;
            }
            logger.info("第三方黑名单渠道检查，当前渠道检查通过，检查渠道名称={}，userCode={}", channel.getClass().getName(), userEntity.getUserCode());
        }

        return new AuditResponseEvent(this.userEntity.getUserCode(), true, "");
    }

}
