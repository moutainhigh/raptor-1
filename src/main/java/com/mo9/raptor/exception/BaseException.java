package com.mo9.raptor.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 基础参数化异常基类
 * @author jyou
 */
public class BaseException extends Exception {

  /**
   * 异常打印参数
   */
  private Map<String, String> param = new HashMap<String, String>();

  public BaseException(String msg) {
    super(msg);
  }

  public BaseException(Throwable t) {
    super(t);
  }

  public BaseException(String msg, Throwable t) {
    super(msg, t);
  }

  public BaseException putParam(String name, String value) {
    this.param.put(name, value);
    return this;
  }

  @Override
  public String getMessage() {
    String message = super.getMessage();
    StringBuilder sb = new StringBuilder(message);
    Set<Entry<String, String>> entries = this.param.entrySet();
    for (Entry<String, String> entry : entries) {
      sb.append(" ");
      sb.append(entry.getKey());
      sb.append(":");
      sb.append("[");
      sb.append(entry.getValue());
      sb.append("]");
    }
    return sb.toString();

  }
}
