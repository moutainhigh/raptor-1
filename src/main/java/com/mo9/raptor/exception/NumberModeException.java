package com.mo9.raptor.exception;

/**
 * Created by gqwu on 2018/4/4.
 */
public class NumberModeException extends BaseException {
    public NumberModeException(String msg) {
        super(msg);
    }

    public NumberModeException(Throwable t) {
        super(t);
    }

    public NumberModeException(String msg, Throwable t) {
        super(msg, t);
    }
}
