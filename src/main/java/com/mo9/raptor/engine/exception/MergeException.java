package com.mo9.raptor.engine.exception;

import com.mo9.raptor.exception.BaseException;

/**
 * Created by gqwu on 2018/7/9.
 */
public class MergeException extends BaseException {
    public MergeException(String msg) {
        super(msg);
    }

    public MergeException(Throwable t) {
        super(t);
    }

    public MergeException(String msg, Throwable t) {
        super(msg, t);
    }
}
