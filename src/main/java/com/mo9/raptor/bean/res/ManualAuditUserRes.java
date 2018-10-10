package com.mo9.raptor.bean.res;

/**
 * @author zma
 * @date 2018/10/10
 */
public class ManualAuditUserRes {
    private String mobile;

    private String realName;

    private String idCard;

    private String createTime;

    private String source;

    private String subSource;

    private String description;

    private String status;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSubSource() {
        return subSource;
    }

    public void setSubSource(String subSource) {
        this.subSource = subSource;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
