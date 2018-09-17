package com.mo9.raptor.exception;

/**
 * Created by gqwu on 2018/4/4.
 */
public class InvalidLoanException extends BaseException {
    public InvalidLoanException(String msg) {
        super(msg);
    }

    public InvalidLoanException(Throwable t) {
        super(t);
    }

    public InvalidLoanException(String msg, Throwable t) {
        super(msg, t);
    }
}
