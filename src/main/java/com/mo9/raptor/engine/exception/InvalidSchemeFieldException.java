package com.mo9.raptor.engine.exception;

import com.mo9.raptor.exception.BaseException;

/**
 * Created by gqwu on 2018/4/4.
 */
public class InvalidSchemeFieldException extends BaseException {
    public InvalidSchemeFieldException(String msg) {
        super(msg);
    }

    public InvalidSchemeFieldException(Throwable t) {
        super(t);
    }

    public InvalidSchemeFieldException(String msg, Throwable t) {
        super(msg, t);
    }
}
