package com.mo9.raptor.engine.state.handler;

import com.mo9.raptor.engine.entity.IStateEntity;
import com.mo9.raptor.engine.exception.NotExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gqwu on 2018/4/19.
 */
@Component
public class StateHandlerFactory {
    private static final Logger logger = LoggerFactory.getLogger(StateHandlerFactory.class);

    @Autowired
    private ApplicationContext springContext;

    private static Map<Class<IStateEntity>, Map<String, IStateHandler>> stateHandlers
            = new HashMap<Class<IStateEntity>, Map<String, IStateHandler>>();

    @PostConstruct
    public void init() {
        Collection states = this.springContext.getBeansWithAnnotation(StateHandler.class).values();
        for (Object object : states) {
            if (!(object instanceof IStateHandler)) {
                continue;
            }
            Type[] interfaceTypes = object.getClass().getGenericInterfaces();
            for (Type interfaceType: interfaceTypes) {
                Type[] parameterTypes = ((ParameterizedType) interfaceType).getActualTypeArguments();
                for (Type parameterType :parameterTypes) {
                    Class clazz = (Class) parameterType;
                    if (IStateEntity.class.isAssignableFrom(clazz)) {
                        Map<String, IStateHandler> entityStates = stateHandlers.get(clazz);
                        if (entityStates == null) {
                            entityStates = new HashMap<String, IStateHandler>();
                            stateHandlers.put(clazz, entityStates);
                        }
                        IStateHandler stateHandler = (IStateHandler) object;
                        StateHandler stateHandlerAnnotation = stateHandler.getClass().getAnnotation(StateHandler.class);
                        entityStates.put(stateHandlerAnnotation.name().name(), stateHandler);
                        logger.debug("状态实体：[{}]，状态类型：[{}]，状态处理类：[{}]",clazz.getSimpleName(), stateHandlerAnnotation.name() ,stateHandler.getClass());
                    }
                }
            }
        }
    }

    public IStateHandler instance (String status, Class<? extends IStateEntity> clazz) throws NotExistException {
        Map<String, IStateHandler> entityStates = stateHandlers.get(clazz);
        if (entityStates != null) {
            return entityStates.get(status);
        }
        logger.debug("状态类型[{}]没有匹配的处理类",status);
        throw new NotExistException("状态类型"+ status + "没有匹配的处理类");
    }
}
