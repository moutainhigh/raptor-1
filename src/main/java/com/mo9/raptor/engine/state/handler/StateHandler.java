package com.mo9.raptor.engine.state.handler;

import com.mo9.raptor.engine.enums.StatusEnum;

import java.lang.annotation.*;

/**
 * Created by sun on 2017/8/7.
 * 注解状态
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StateHandler {
  StatusEnum name();
}
