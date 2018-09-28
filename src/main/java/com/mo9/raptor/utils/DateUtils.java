package com.mo9.raptor.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jyou on 2018/9/28.
 *
 * @author jyou
 */
public class DateUtils {
    private static final String pattern1 = "yyyy-MM-dd";

    /**
     * 格式化日期
     * @param date
     * @return
     */
    public static synchronized String formartDate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern1);
        return sdf.format(date);
    }
}
