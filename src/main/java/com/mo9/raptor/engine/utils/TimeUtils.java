package com.mo9.raptor.engine.utils;

import com.mo9.raptor.engine.exception.UnSupportTimeDiffException;

import java.util.Calendar;

public class TimeUtils {

    /** 抽取日期毫秒数，时、分、秒、毫秒，置0 */
    public static Long extractDateTime(Long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    /** 天数差：4月15日 - 4月16日 差1天，根据业务需要决定是否计头计尾 */
    public static int dateDiff (long start, long end) {
        return new Long ((extractDateTime(end) - extractDateTime(start)) / EngineStaticValue.DAY_MILLIS).intValue();
    }

    /**
     * 以月间距为例：4月15日 ~ 5月14日为1个月间距
     * 以天间距为例：
     */
    public static int termDiff(long start, long end, int calendarUnit) throws UnSupportTimeDiffException {

        switch (calendarUnit) {
            case Calendar.YEAR:{
                Calendar s = Calendar.getInstance();
                s.setTimeInMillis(start);
                Calendar e = Calendar.getInstance();
                e.setTimeInMillis(end);
                e.add(Calendar.DATE, 1);
                int sy = s.get(Calendar.YEAR);
                int sm = s.get(Calendar.MONTH);
                int sd = s.get(Calendar.DATE);
                int ey = e.get(Calendar.YEAR);
                int em = e.get(Calendar.MONTH);
                int ed = e.get(Calendar.DATE);

                int diff = ey - sy;

                if (sm > em) {
                    return diff - 1;
                } else if (sm < em) {
                    return diff;
                } else if (sd > ed) {
                    return diff - 1;
                } else {
                    return diff;
                }
            }
            case Calendar.MONTH:{
                Calendar s = Calendar.getInstance();
                s.setTimeInMillis(start);
                Calendar e = Calendar.getInstance();
                e.setTimeInMillis(end);
                e.add(Calendar.DATE, 1);
                int sy = s.get(Calendar.YEAR);
                int sm = s.get(Calendar.MONTH);
                int sd = s.get(Calendar.DATE);
                int ey = e.get(Calendar.YEAR);
                int em = e.get(Calendar.MONTH);
                int ed = e.get(Calendar.DATE);

                int diff = (ey - sy) * 12 + em - sm;

                if (sd > ed) {
                    return diff - 1;
                } else {
                    return diff;
                }
            }
            case Calendar.DATE:{
                Calendar s = Calendar.getInstance();
                s.setTimeInMillis(start);
                Calendar e = Calendar.getInstance();
                e.setTimeInMillis(end);
                e.add(Calendar.DATE, 1);

                return dateDiff(s.getTimeInMillis(), e.getTimeInMillis());
            } default: {
                throw new UnSupportTimeDiffException("不支持该日历类型时差的比较！日历类型：" + calendarUnit);
            }
        }

    }
}
