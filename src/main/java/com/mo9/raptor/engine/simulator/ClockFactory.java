package com.mo9.raptor.engine.simulator;

import com.mo9.raptor.daily.LoanOrderDaily;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by gqwu on 2018/4/6.
 */
@Component
public class ClockFactory {

    private static Map<String, Clock> clockMap = new HashMap<String, Clock>();

    public static Clock getClock (String userCode) {
        return clockMap.get(userCode);
    }

    public Clock createClock (String userCode) {
        Clock clock = new Clock(userCode);
        clockMap.put(userCode, clock);
        return clock;
    }

    public static long clockTime(String userCode) {

        Clock clock = clockMap.get(userCode);

        if (clock == null || !clock.clockIsLoaded()) {
            return System.currentTimeMillis();
        }
        return clockMap.get(userCode).timeMillis();
    }

    public void setClockTime (String userCode, long timeMillis) {
        Clock clock = clockMap.get(userCode);
        if (clock == null) {
            clock = new Clock(userCode);
            clockMap.put(userCode, clock);
        }
        clock.setTimeMillis(timeMillis);
    }

    public static int clockSpeed(String userCode) {

        Clock clock = clockMap.get(userCode);

        if (clock == null || !clock.clockIsLoaded()) {
            return 0;
        }
        return clockMap.get(userCode).getSpeed();
    }

    public void setClockSpeed (String userCode, int speed) {
        Clock clock = clockMap.get(userCode);
        if (clock == null) {
            clock = new Clock(userCode);
            clockMap.put(userCode, clock);
        }
        clock.setSpeed(speed);
    }
}