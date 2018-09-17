package com.mo9.raptor.engine.state.event.impl.user;

import com.mo9.raptor.engine.state.event.AbstractStateEvent;
import com.mo9.raptor.engine.state.event.IStateEvent;

/**
 * Created by gqwu on 2018/4/8.
 */
public class BlackEvent extends AbstractStateEvent implements IStateEvent {

    private final String explanation;

    public BlackEvent(String userCode, String explanation) {
        super(userCode);
        this.explanation = explanation;
    }

    public String getExplanation() {
        return explanation;
    }
}
