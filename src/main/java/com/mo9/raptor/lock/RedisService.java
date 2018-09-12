package com.mo9.raptor.lock;


import java.util.concurrent.TimeUnit;

/**
 * Redis操作接口
 *
 * @author : xjding
 * @date :   2017-08-21 09:43
 */
public interface RedisService {

    /**
     * 非阻塞锁
     *
     * @param key        锁键
     * @param value      被谁锁定
     * @param expire     失效时间
     * @param expireUnit 失效时间单位
     * @return 锁是否成功
     */
    boolean lock(String key, String value, long expire, TimeUnit expireUnit);

    /**
     * 阻塞锁
     *
     * @param key         锁键
     * @param value       被谁锁定
     * @param timeout     尝试获取锁时长，建议传递500,结合实践单位，则可表示500毫秒
     * @param timeoutUnit 建议传递TimeUnit.MILLISECONDS
     * @param expire      锁被动失效时间
     * @param expireUnit  锁被动失效时间单位,建议传递TimeUnit.MILLISECONDS
     * @return 锁是否成功
     */
    boolean lock(String key, String value, long timeout, TimeUnit timeoutUnit, long expire, TimeUnit expireUnit);


    /**
     * 删除锁，强制执行，可能会释放其他线程占用的锁，
     * 慎用，慎用！！！
     *
     * @param key 锁键
     */
    void unlock(String key);

    /**
     * 删除锁
     *
     * @param lock 释放特定的锁
     */
    void release(Lock lock);

}
