package com.mo9.raptor.engine.exception;

import com.mo9.raptor.exception.BaseException;

/**
 * Created by gqwu on 2018/4/4.
 */
public class NotExistException extends BaseException {
    public NotExistException(String msg) {
        super(msg);
    }

    public NotExistException(Throwable t) {
        super(t);
    }

    public NotExistException(String msg, Throwable t) {
        super(msg, t);
    }
}
