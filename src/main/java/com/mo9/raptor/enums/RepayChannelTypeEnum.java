package com.mo9.raptor.enums;

/**还款渠道
 * 1.易联 H5支付 2.易联 SDK支付 3.连连  SDK支付 4.XXX
 * Created by xzhang on 2018/9/13.
 */
public enum RepayChannelTypeEnum {

    /**
     * 易联H5
     */
    YILIANPAY_H5(1, ChannelUseType.LINK, "易联H5支付"),
    ;

    /**
     * 渠道名
     */
    private String channelName;

    /**
     * 渠道编号
     */
    private Integer channelType;

    /**
     * 渠道使用方式
     */
    private ChannelUseType channelUseType;

    public String getChannelName() {
        return channelName;
    }

    public Integer getChannelType() {
        return channelType;
    }

    public ChannelUseType getChannelUseType() {
        return channelUseType;
    }

    RepayChannelTypeEnum(Integer channelType, ChannelUseType channelUseType, String channelName) {
        this.channelName = channelName;
        this.channelType = channelType;
        this.channelUseType = channelUseType;
    }

    public static RepayChannelTypeEnum getByChannelType(Integer channelType) {
        for (RepayChannelTypeEnum repayChannelTypeEnum : RepayChannelTypeEnum.values()) {
            if (repayChannelTypeEnum.channelType.equals(channelType)) {
                return repayChannelTypeEnum;
            }
        }
        return null;
    }
}
