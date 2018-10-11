package com.mo9.raptor.engine.state.action.impl.user;

import com.alibaba.fastjson.JSON;
import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.utils.push.PushBean;
import com.mo9.raptor.utils.push.PushUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jyou on 2018/10/11.
 *
 * @author jyou
 */
public class UserPushAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(UserPushAction.class);

    private String userCode;

    private PushUtils pushUtils;

    private PushBean pushBean;

    public UserPushAction(String userCode, PushUtils pushUtils, PushBean pushBean) {
        this.userCode = userCode;
        this.pushUtils = pushUtils;
        this.pushBean = pushBean;
    }

    @Override
    public String getActionType() {
        return this.getClass().getName();
    }

    @Override
    public String getOrderId() {
        return userCode;
    }

    @Override
    public void run() {
        pushUtils.push(pushBean);
        logger.info("完成推送--->userCode={},pushBena={}", userCode, JSON.toJSONString(pushBean));
    }
}
