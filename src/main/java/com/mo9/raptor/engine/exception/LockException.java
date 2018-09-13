package com.mo9.raptor.engine.exception;

import com.mo9.raptor.exception.BaseException;
import com.mo9.raptor.lock.Lock;

/**
 * 订单获取锁超时
 * Created by sun on 2017/7/24.
 */
public class LockException extends BaseException {


  /**
   * 超时事件
   */
  private int timeout;

  /**
   * 锁对象
   */
  private Lock lock;

  public LockException(String message) {
    super(message);
  }

  public LockException(String message, Throwable t) {
    super(message, t);
  }

  public LockException setOrderNo(String orderId) {
    this.putParam("锁超时订单号", orderId);
    return this;
  }


  public LockException setTimeout(int timeout) {
    this.timeout = timeout;
    return (LockException) this.putParam("设定的超时时间", Integer.toString(timeout));
  }

  public LockException setLock(Lock lock) {
    this.lock = lock;
    return (LockException) this.putParam("锁key", lock.getName())
        .putParam("锁value", lock.getValue());
  }
}
