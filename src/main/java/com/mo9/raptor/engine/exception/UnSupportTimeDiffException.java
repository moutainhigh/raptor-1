package com.mo9.raptor.engine.exception;

import com.mo9.raptor.exception.BaseException;

/**
 * Created by gqwu on 2018/7/9.
 */
public class UnSupportTimeDiffException extends BaseException {
    public UnSupportTimeDiffException(String msg) {
        super(msg);
    }

    public UnSupportTimeDiffException(Throwable t) {
        super(t);
    }

    public UnSupportTimeDiffException(String msg, Throwable t) {
        super(msg, t);
    }
}
