package com.mo9.raptor.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by gqwu on 2018/3/23.
 * 分期还款方式
 */
public enum InstallmentModeEnum {

    FIXED_INSTALLMENT("等额本息"),
    FIXED_PRINCIPAL("等额本金"),
    REPAY_PRINCIPAL_WITH_INSTALLMENT("到期还本付息"),
    ;

    private String explanation;

    InstallmentModeEnum(String explanation){
        this.explanation = explanation;
    }

    public String getExplanation() {
        return explanation;
    }



    public static InstallmentModeEnum getByName(String name) {
        if (StringUtils.isBlank(name)) {
            return FIXED_INSTALLMENT;
        }
        InstallmentModeEnum[] settleTypes = InstallmentModeEnum.values();
        for (InstallmentModeEnum settleType : settleTypes) {
            if (settleType.name().equals(name)) {
                return settleType;
            }
        }
        return FIXED_INSTALLMENT;
    }



}
