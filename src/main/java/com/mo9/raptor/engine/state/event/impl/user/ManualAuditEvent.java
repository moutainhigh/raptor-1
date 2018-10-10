package com.mo9.raptor.engine.state.event.impl.user;

import com.mo9.raptor.engine.state.event.AbstractStateEvent;
import com.mo9.raptor.engine.state.event.IStateEvent;

/**
 * Created by jyou on 2018/10/9.
 *
 * @author jyou
 *
 * 人工审核事件
 */
public class ManualAuditEvent extends AbstractStateEvent implements IStateEvent {

    private boolean isPass;

    private String explanation;

    public ManualAuditEvent(String userCode, boolean isPass, String explanation) {
        super(userCode);
        this.explanation = explanation;
        this.isPass = isPass;
    }

    public String getExplanation() {
        return explanation;
    }

    public boolean isPass() {
        return isPass;
    }

}
