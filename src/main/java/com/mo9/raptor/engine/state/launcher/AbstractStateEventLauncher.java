package com.mo9.raptor.engine.state.launcher;

import com.mo9.raptor.engine.state.action.ActionExecutorImpl;
import com.mo9.raptor.engine.state.action.IActionExecutor;
import com.mo9.raptor.engine.entity.IStateEntity;
import com.mo9.raptor.engine.entity.PayOrderEntity;
import com.mo9.raptor.engine.state.action.impl.user.UserLogAction;
import com.mo9.raptor.engine.state.event.IStateEvent;
import com.mo9.raptor.engine.exception.InvalidEventException;
import com.mo9.raptor.engine.exception.LockException;
import com.mo9.raptor.engine.exception.NotExistException;
import com.mo9.raptor.engine.state.handler.IStateHandler;
import com.mo9.raptor.engine.state.handler.StateHandlerFactory;
import com.mo9.raptor.entity.UserEntity;
import com.mo9.raptor.lock.Lock;
import com.mo9.raptor.lock.RedisService;
import com.mo9.raptor.repository.UserLogReprsitory;
import com.mo9.raptor.utils.IDWorker;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by gqwu on 2018/4/19.
 * 状态机事件处理器抽象类，用于处理特定的，作用于包含状态字段的实体类的事件，
 * 要求该实体类的状态处理，实现@code IStateHandler接口
 */
@Component
public abstract class AbstractStateEventLauncher<E extends IStateEntity, V extends IStateEvent> implements IEventLauncher<V> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractStateEventLauncher.class);


    @Autowired
    private RedisService redisService;

    @Autowired
    private IDWorker idWorker;

    @Autowired
    private StateHandlerFactory stateHandlerFactory;

    @Autowired
    private UserLogReprsitory userLogReprsitory;

    public abstract E selectEntity (String entityUniqueId);

    public abstract void saveEntity (E entity);

    @Override
    public void launch(V event) throws Exception {

        if (StringUtils.isBlank(event.getEntityUniqueId())) {
            throw new InvalidEventException("事件传递的状态实体ID为空，事件：" + event.toString());
        }
        IActionExecutor actionExecutor = new ActionExecutorImpl();

        Lock lock = new Lock(event.getEntityUniqueId(), idWorker.nextId()+"");
        try {
            /**
             * 非阻塞锁，不等待竞争者释放锁，因为状态机事件，一般是互斥，
             * 当两个非互斥事件发生时，业务层需要主动捕捉LockException，重新发送事件
             */
            if (redisService.lock(lock.getName(), lock.getValue(), 5000, TimeUnit.MILLISECONDS)) {
                E entity = selectEntity(event.getEntityUniqueId());
                String preStatus = entity.getStatus();
                if (entity == null) {
                    throw new NotExistException("事件的目标状态实体不存在，事件：" + event.toString() + "，实体：" + event.getEntityUniqueId());
                }
                // TODO: 临时代码
                if (entity instanceof PayOrderEntity) {
                    PayOrderEntity payOrderEntity = (PayOrderEntity) entity;
                    logger.info("还款订单[{}]处理前状态为[{}], 当前时间[{}]", payOrderEntity.getOrderId(), entity.getStatus(), System.currentTimeMillis());
                }
                IStateHandler stateHandler = stateHandlerFactory.instance(entity.getStatus(), entity.getClass());

                if (stateHandler == null) {
                    throw new NotExistException("事件目标状态实体状态没有合适的处理器，事件：" + event.toString() +"订单号: " + event.getEntityUniqueId() + "，订单状态：" + entity.getStatus());
                }

                IStateEntity stateEntity = stateHandler.handle(entity, event, actionExecutor);
                this.saveEntity((E) stateEntity);
                // TODO: 临时代码
                if (stateEntity instanceof PayOrderEntity) {
                    PayOrderEntity payOrderEntity = (PayOrderEntity) stateEntity;
                    logger.info("还款订单[{}]处理后状态为[{}], 当前时间[{}]", payOrderEntity.getOrderId(), entity.getStatus(), System.currentTimeMillis());
                }else if(stateEntity instanceof UserEntity){
                    UserEntity postEntity = (UserEntity) stateEntity;
                    UserLogAction userLogAction = new UserLogAction(userLogReprsitory, postEntity, preStatus);
                    actionExecutor.append(userLogAction);
                }
            } else {
                throw new LockException("事件为目标状态实体请求锁时，竞争失败，事件：" + event.toString() + "，实体：" + event.getEntityUniqueId());
            }
        } finally {
            redisService.release(lock);
        }

        /**
         * 完成事件处理后，由线程池异步执行附加操作
         */
        actionExecutor.execute();

    }
}
