package com.mo9.raptor.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 渠道列表
 * Created by xzhang on 2018/9/16.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_raptor_channel")
public class ChannelEntity extends BaseEntity {

    /**
     * 渠道名
     */
    @Column(name = "channel")
    private String channel;

    /**
     * 渠道中文名称
     */
    @Column(name = "channel_name")
    private String channelName;

    /**
     * 渠道类型, 还款, 放款
     */
    @Column(name = "channel_type")
    private String channelType;

    /**
     * 打开方式
     */
    @Column(name = "use_type")
    private String useType;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getUseType() {
        return useType;
    }

    public void setUseType(String useType) {
        this.useType = useType;
    }
}
