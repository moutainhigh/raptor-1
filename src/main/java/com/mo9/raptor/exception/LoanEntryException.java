package com.mo9.raptor.exception;

/**
 * Created by gqwu on 2018/4/4.
 */
public class LoanEntryException extends BaseException {
    public LoanEntryException(String msg) {
        super(msg);
    }

    public LoanEntryException(Throwable t) {
        super(t);
    }

    public LoanEntryException(String msg, Throwable t) {
        super(msg, t);
    }
}
