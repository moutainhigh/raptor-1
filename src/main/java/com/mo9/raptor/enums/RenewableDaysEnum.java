package com.mo9.raptor.enums;

/**
 * 可用延期天数
 * Created by xzhang on 2018/9/13.
 */
public enum RenewableDaysEnum {

    /**
     * 7天
     */
    SEVENT(7),

    /**
     * 14天
     */
    FOURTEEN(14)

    ;

    /**
     * 可延期天数
     */
    private Integer days;

    public Integer getDays() {
        return days;
    }

    RenewableDaysEnum(Integer days) {
        this.days = days;
    }

    public static Boolean checkRenewableDays(Integer days) {
        if (days == null) {
            return false;
        }
        for (RenewableDaysEnum renewableDays : RenewableDaysEnum.values()) {
            if (days.equals(renewableDays.days)) {
                return true;
            }
        }
        return false;
    }

    public static Integer getBasicRenewableDaysTimes(Integer days) {
        return days / getBasicRenewableDays();
    }

    public static Integer getBasicRenewableDays() {
        return SEVENT.days;
    }

}
