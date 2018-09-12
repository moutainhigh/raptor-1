package com.mo9.raptor.engine.statics;

/**
 * Created by gqwu on 2018/3/29.
 */
public interface EngineStaticValue {

    String INIT_WEIGHT_ID = "000000000000000000";

    String BATCH_NO_PREFIX = "batch-no-";

    Integer PREPAY_ITEM = 0;

    /**
     * 日毫秒数
     */
    long DAY_MILLIS = 86400000;

    /**
     * 秒毫秒数
     */
    long SECOND_MILLS = 1000;

    /**
     * 还款订单确认过期时间
     */
    int CONFIRM_PAY_EXPIRE_SECONDS = 30;

    /**
     * 年化天数
     */
    int ANNUAL_DAYS = 360;

    /**
     * 中间计算步骤保留位数
     */
    int INTERMEDIATE_STEP_SCALE = 18;

    /**
     * 结果保留位数
     */
    int RESULT_SCALE = 8;
}
