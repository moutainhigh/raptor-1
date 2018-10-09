package com.mo9.raptor.engine.state.event;

/**
 * Created by gqwu on 2018/4/4.
 */
public abstract class AbstractStateEvent extends AbstractEvent implements IStateEvent {

    private final String entityUniqueId;

    public AbstractStateEvent(String entityUniqueId) {
        super();
        this.entityUniqueId = entityUniqueId;
    }

    @Override
    public String getEntityUniqueId() {
        return this.entityUniqueId;
    }
}
