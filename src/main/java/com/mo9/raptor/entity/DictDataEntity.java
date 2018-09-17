package com.mo9.raptor.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 */
@Entity
@Table(name = "t_raptor_dict_data")
public class DictDataEntity extends BaseEntity{

    /**
     * 父表编号
     */
    @Column(name = "dict_type_no")
    private String dictTypeNo;

    /**
     * 编号
     */
    @Column(name = "dict_data_no")
    private String dictDataNo;

    /**
     * 名称
     */
    @Column(name = "name")
    private String name;

    public String getDictTypeNo() {
        return dictTypeNo;
    }

    public void setDictTypeNo(String dictTypeNo) {
        this.dictTypeNo = dictTypeNo;
    }

    public String getDictDataNo() {
        return dictDataNo;
    }

    public void setDictDataNo(String dictDataNo) {
        this.dictDataNo = dictDataNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
