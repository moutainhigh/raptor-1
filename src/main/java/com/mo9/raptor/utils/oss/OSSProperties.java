package com.mo9.raptor.utils.oss;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by jyou on 2018/3/5.
 */
@Component
public class OSSProperties {

    @Value("${raptor.oss.http-prefix}")
    private String httpPrefix;

    @Value("${raptor.oss.write-endpoint}")
    private String writeEndpoint;

    @Value("${raptor.oss.read-endpoint}")
    private String readEndpoint;

    @Value("${raptor.oss.access-key-id}")
    private String accessKeyId;

    @Value("${raptor.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${raptor.oss.bucket-name}")
    private String bucketName;

    @Value("${raptor.oss.catalog}")
    private String catalog;
    
    @Value("${raptor.oss.catalog.callLogReport}")
    private String catalogCallLog;

    @Value("${raptor.oss.catalog.callLogRule}")
    private String catalogCallRule;

    @Value("${raptor.oss.expire-time}")
    private String expireTime;

    public String getHttpPrefix() {
        return httpPrefix;
    }

    public void setHttpPrefix(String httpPrefix) {
        this.httpPrefix = httpPrefix;
    }

    public String getWriteEndpoint() {
        return writeEndpoint;
    }

    public void setWriteEndpoint(String writeEndpoint) {
        this.writeEndpoint = writeEndpoint;
    }

    public String getReadEndpoint() {
        return readEndpoint;
    }

    public void setReadEndpoint(String readEndpoint) {
        this.readEndpoint = readEndpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getCatalogCallLog() {
        return catalogCallLog;
    }

    public void setCatalogCallLog(String catalogCallLog) {
        this.catalogCallLog = catalogCallLog;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getCatalogCallRule() {
        return catalogCallRule;
    }

    public void setCatalogCallRule(String catalogCallRule) {
        this.catalogCallRule = catalogCallRule;
    }
}
