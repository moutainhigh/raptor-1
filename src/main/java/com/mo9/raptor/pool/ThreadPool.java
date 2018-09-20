package com.mo9.raptor.pool;

import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author jyou
 */
@Component
public class ThreadPool {
    /**
     * 线程池维护线程的最少数量
     */
    private static final int COREPOOLSIZE = 2;
    /**
     * 线程池维护线程的最大数量
     */
    private static final int MAXINUMPOOLSIZE = 20;
    /**
     * 线程池维护线程所允许的空闲时间
     */
    private static final long KEEPALIVETIME = 4;
    /**
     * 线程池维护线程所允许的空闲时间的单位
     */
    private static final TimeUnit UNIT = TimeUnit.SECONDS;
    /**
     * 线程池所使用的缓冲队列
     */
    private static final BlockingQueue<Runnable> WORKQUEUE = new ArrayBlockingQueue<Runnable>(20);
    /**
     * 线程池对拒绝任务的处理策略：AbortPolicy为抛出异常；CallerRunsPolicy为重试添加当前的任务，他会自动重复调用execute()方法；DiscardOldestPolicy为抛弃旧的任务，DiscardPolicy为抛弃当前的任务
     */
    private static final ThreadPoolExecutor.AbortPolicy HANDLER = new ThreadPoolExecutor.AbortPolicy();

    private ThreadPoolExecutor threadPool;

    public ThreadPool() {
        this.threadPool = new ThreadPoolExecutor(COREPOOLSIZE, MAXINUMPOOLSIZE, KEEPALIVETIME, UNIT, WORKQUEUE, HANDLER);
    }

    public void execute(ThreadPoolTask task){
            this.threadPool.execute(task);
    }

}
