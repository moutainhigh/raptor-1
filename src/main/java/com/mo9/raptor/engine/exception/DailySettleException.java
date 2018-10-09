package com.mo9.raptor.engine.exception;

import com.mo9.raptor.exception.BaseException;

/**
 * Created by gqwu on 2018/7/9.
 */
public class DailySettleException extends BaseException {
    public DailySettleException(String msg) {
        super(msg);
    }

    public DailySettleException(Throwable t) {
        super(t);
    }

    public DailySettleException(String msg, Throwable t) {
        super(msg, t);
    }
}
