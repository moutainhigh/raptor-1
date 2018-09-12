package com.mo9.raptor.engine.exception;

import com.mo9.raptor.exception.BaseException;

/**
 * Created by gqwu on 2018/4/4.
 */
public class InvalidEventException extends BaseException {
    public InvalidEventException(String msg) {
        super(msg);
    }

    public InvalidEventException(Throwable t) {
        super(t);
    }

    public InvalidEventException(String msg, Throwable t) {
        super(msg, t);
    }
}
