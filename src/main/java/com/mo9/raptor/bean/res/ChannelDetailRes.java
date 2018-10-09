package com.mo9.raptor.bean.res;

/**
 * 渠道详情
 * Created by xzhang on 2018/9/13.
 */
public class ChannelDetailRes {

    /**
     * 渠道类型
     */
    private Long channelType;

    /**
     * 渠道名
     */
    private String channelName;

    /**
     * 使用方式
     */
    private String useType;

    public Long getChannelType() {
        return channelType;
    }

    public void setChannelType(Long channelType) {
        this.channelType = channelType;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getUseType() {
        return useType;
    }

    public void setUseType(String useType) {
        this.useType = useType;
    }
}
