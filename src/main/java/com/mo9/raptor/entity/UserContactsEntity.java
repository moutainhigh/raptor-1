package com.mo9.raptor.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 *
 * 通讯录表
 */
@Entity
@Table(name = "t_raptor_bank")
public class UserContactsEntity extends BaseEntity{

    @Column(name = "user_code")
    private String userCode;

    /**
     * 通讯录
     */
    @Column(name = "contacts_list")
    private String contactsList;

    /**
     * 客户端id
     */
    @Column(name = "client_id")
    private String clientId;

    /**
     * 版本号
     */
    @Column(name = "client_version")
    private String clientVersion;

    /**
     * 马甲名称
     */
    @Column(name = "sockpuppet")
    private String sockpuppet;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getContactsList() {
        return contactsList;
    }

    public void setContactsList(String contactsList) {
        this.contactsList = contactsList;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getSockpuppet() {
        return sockpuppet;
    }

    public void setSockpuppet(String sockpuppet) {
        this.sockpuppet = sockpuppet;
    }
}
