package com.mo9.raptor.engine.simulator;

import com.mo9.raptor.daily.LoanOrderDaily;
import com.mo9.raptor.engine.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * Created by gqwu on 2018/4/3.
 */
public class Clock implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Clock.class);

    private Calendar clock;

    private boolean clockLoaded;

    private boolean clockPaused;

    private int speed;

    private LoanOrderDaily loanOrderDaily;

    private String userCode;

    public Clock(String userCode, LoanOrderDaily loanOrderDaily) {
        this.userCode = userCode;
        this.clockLoaded = false;
        this.clockPaused = true;
        this.speed = 1;
        this.clock = Calendar.getInstance();
        this.loanOrderDaily = loanOrderDaily;
    }

    public boolean clockIsLoaded() {
        return clockLoaded;
    }

    public void loadClock() {
        this.clockLoaded = true;
    }

    public void unloadClock() {
        this.clockLoaded = false;
        this.clockPaused = true;
    }

    public boolean clockIsPaused() {
        return clockPaused;
    }

    public void pauseClock() {
        this.clockPaused = true;
    }

    public synchronized void activateClock() {
        this.clockPaused = false;
        this.notify();
    }

    @Override
    public synchronized void run() {
        long start;
        long end;
        long sleep;
        while (true) {
            try {
                if (this.clockPaused == true) {
                    this.wait();
                }
                start = System.currentTimeMillis();
                long now = this.clock.getTimeInMillis();
                long tick = speed * 1000;
                this.setTimeMillis(now + tick);
                end = System.currentTimeMillis();
                /** 单次循环 + 睡眠时间 = 1秒，使得符合设定时钟速率 */
                sleep = 1000 - end + start;
                if (sleep < 0) {
                    logger.debug("借贷模拟器，自定义时钟关联的任务执行时间过长，用时[{}]毫秒，时钟将不能准确执行速率设定！", end - start);
                } else {
                    Thread.sleep(sleep);
                }
            } catch (InterruptedException e) {
                logger.debug("用户时钟-中断，", e);
            } catch (Exception e) {
                logger.debug("用户时钟-异常",e);
            }
        }
    }

    /**
     * 设置用户时钟，将初始化用户数据
     * @return
     */
    public void setTimeMillis(long setTime) {

        boolean pause = this.clockPaused;
        long clockTime = this.clock.getTimeInMillis();

        if (setTime > clockTime) {
            if (!pause) {
                this.clockPaused = true;
            }
            long days = TimeUtils.dateDiff(clockTime, setTime);
            for (int i = 0; i < days; i ++) {
                /**
                 * 每日任务执行
                 */
                this.clock.add(Calendar.DATE, 1);
                /** TODO: */
            }
            if (!pause) {
                this.activateClock();
            }
        } else if (setTime < clockTime) {
            //TODO:
        }
        this.clock.setTimeInMillis(setTime);
    }

    public long timeMillis() {
        return clock.getTimeInMillis();
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return this.speed;
    }

}
