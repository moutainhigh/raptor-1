package com.mo9.raptor.engine.state.action.impl.user;

import com.mo9.raptor.engine.entity.IStateEntity;
import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.entity.UserContactsEntity;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.entity.UserLogEntity;
import com.mo9.raptor.repository.UserLogReprsitory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jyou on 2018/10/30.
 *
 * @author jyou
 * 用户日志记录action
 */
public class UserLogAction implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(UserLogAction.class);
    private UserEntity entity;

    private String preStatus;

    private UserLogReprsitory userLogReprsitory;


    public UserLogAction(UserLogReprsitory userLogReprsitory, UserEntity entity, String preStatus) {
        this.userLogReprsitory = userLogReprsitory;
        this.entity = entity;
        this.preStatus = preStatus;
    }

    @Override
    public String getActionType() {
        return this.getClass().getName();
    }

    @Override
    public String getOrderId() {
        return this.entity.getUserCode();
    }

    @Override
    public void run() {
        try{
            UserLogEntity userLogEntity = new  UserLogEntity();
            userLogEntity.setUserCode(entity.getUserCode());
            userLogEntity.setCreateTime(System.currentTimeMillis());
            userLogEntity.setDescribe(entity.getDescription());
            userLogEntity.setPreStatus(preStatus);
            userLogEntity.setPostStatus(entity.getStatus());
            userLogReprsitory.save(userLogEntity);
            logger.info("报存用户状态修改记录日志成功，userCode={}", this.entity.getUserCode());
        }catch (Exception e){
            logger.error("报存用户状态修改记录日志出现异常，userCode={}", this.entity.getUserCode(), e);
        }


    }
}
