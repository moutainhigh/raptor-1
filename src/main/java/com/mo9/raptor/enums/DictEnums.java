package com.mo9.raptor.enums;

import com.mo9.raptor.utils.DateUtils;

import java.util.Date;

/**
 * Created by jyou on 2018/9/28.
 *
 * @author jyou
 *
 * 数据字典服务key
 */
public enum  DictEnums {
    REGISTER_NUM("NEW_REGISTER_USER", DateUtils.formartDate(new Date()), "每天限制注册新用户数量"),
            ;
    private String dictTypeNo;

    private String dictDataNo;

    private String desc;

    DictEnums(String dictTypeNo, String dictDataNo, String desc) {
        this.dictTypeNo = dictTypeNo;
        this.dictDataNo = dictDataNo;
        this.desc = desc;
    }

    public String getDictTypeNo() {
        return dictTypeNo;
    }

    public String getDictDataNo() {
        return dictDataNo;
    }

    public String getDesc() {
        return desc;
    }
}
