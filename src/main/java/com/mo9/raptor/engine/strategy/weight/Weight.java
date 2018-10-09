package com.mo9.raptor.engine.strategy.weight;

import com.mo9.raptor.engine.utils.EngineStaticValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/** 权重计算类 */
public class Weight {

    private static final Logger logger = LoggerFactory.getLogger(Weight.class);

    /** 权重所归属的策略ID */
    private String id;

    /** 权重类型 - 百分比/固定量 */
    private WeightMode weightMode;

    /** 权重值 */
    private BigDecimal weightValue;

    public Weight deepClone () {
        Weight weight = new Weight();
        weight.id = id;
        weight.weightMode = weightMode;
        weight.weightValue = weightValue;
        return weight;
    }

    /** 正向计算权重 - 权重部分包含在参数金额内 */
    public BigDecimal forward(BigDecimal number) {

        BigDecimal weight = BigDecimal.ZERO;

        switch (weightMode) {
            case PERCENT: {
                weight = number.multiply(weightValue).setScale(EngineStaticValue.RESULT_SCALE, BigDecimal.ROUND_UP);
            } break;
            case QUANTITY: {
                if (number.compareTo(weightValue) < 0) {
                    weight = number;
                    /** 扣除已计算的权重 */
                    weightValue = weightValue.subtract(number);
                } else {
                    weight = weightValue;
                }
            } break;
            default: {
                logger.error("暂不支持该权重类型的运算！权重类型：[{}]", weightMode);
            }
        }

        return weight;
    }

    /** 反向计算权重 - 权重部分不包含在参数金额内 */
    public BigDecimal inverse (BigDecimal number) {

        BigDecimal weight = BigDecimal.ZERO;

        switch (weightMode) {
            case PERCENT: {
                weight = number.divide(BigDecimal.ONE.subtract(weightValue), EngineStaticValue.RESULT_SCALE, BigDecimal.ROUND_UP).subtract(number);
            } break;
            case QUANTITY: {
                weight = weightValue;
                /** 扣除已计算的权重 */
                weightValue = BigDecimal.ZERO;
            } break;
            default: {
                logger.error("暂不支持该类型的运算！类型[{}]", weightMode);
            }
        }

        return weight;
    }

    public WeightMode getWeightMode() {
        return weightMode;
    }

    public void setWeightMode(WeightMode weightMode) {
        this.weightMode = weightMode;
    }

    public BigDecimal getWeightValue() {
        return weightValue;
    }

    public void setWeightValue(BigDecimal weightValue) {
        this.weightValue = weightValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
