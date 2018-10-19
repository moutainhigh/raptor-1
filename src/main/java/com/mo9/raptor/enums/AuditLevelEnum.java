package com.mo9.raptor.enums;

/**
 * 审核人员等级
 * @author zma
 * @date 2018/10/17
 */
public enum AuditLevelEnum {
    MANAGE("主管"),
    NORMAL("操作员");
    private String desc;

    AuditLevelEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
