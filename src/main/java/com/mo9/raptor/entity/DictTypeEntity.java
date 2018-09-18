package com.mo9.raptor.entity;

import javax.persistence.*;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 */
@Entity
@Table(name = "t_raptor_dict_type")
public class DictTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 编号
     */
    @Column(name = "dict_type_no")
    private String dictTypeNo;

    /**
     * 名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    @Column(name = "create_time")
    private Long createTime;

    @Column(name = "update_time")
    private Long updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDictTypeNo() {
        return dictTypeNo;
    }

    public void setDictTypeNo(String dictTypeNo) {
        this.dictTypeNo = dictTypeNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}
