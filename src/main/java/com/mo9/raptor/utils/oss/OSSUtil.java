package com.mo9.raptor.utils.oss;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URL;
import java.util.Date;

/**
 * @author qiyao.gu@qq.com.
 */
@Component
public class OSSUtil {

    private static final String DEFAULT_STYLE = "image/resize,w_750,h_480";

    @Resource
    private OSSProperties ossProperties;

    public String createSign(String fileName) {
        OSSClient ossClient = new OSSClient(ossProperties.getReadEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret());
        Date expireTime = new Date(System.currentTimeMillis() + Long.valueOf(ossProperties.getExpireTime()));
        String url = ossClient.generatePresignedUrl(ossProperties.getBucketName(), ossProperties.getCatalog() + "/" + fileName, expireTime).toString();
        //由于read endpoint的配置原因需要这么处理
        return url.replaceAll(ossProperties.getBucketName() + "/", "");
    }

    public String createSignWithStyle(String fileName) {
        OSSClient ossClient = new OSSClient(ossProperties.getReadEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret());
        Date expireTime = new Date(System.currentTimeMillis() + Long.valueOf(ossProperties.getExpireTime()));
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(ossProperties.getBucketName(), ossProperties.getCatalog() + "/" + fileName, HttpMethod.GET);
        req.setExpiration(expireTime);
        req.setProcess(DEFAULT_STYLE);
        URL signedUrl = ossClient.generatePresignedUrl(req);
        return signedUrl.toString();
    }

    /*
     * 图片地址加签名
     */
    public String getSignedPath(String spec) {
        if (StringUtils.isEmpty(spec)) {
            return spec;
        }
        String filename = spec.substring(spec.lastIndexOf('/') + 1);
        int queryIndex = filename.lastIndexOf("?");
        String query = "";

        if (queryIndex != -1) {
            query = "&" + filename.substring(queryIndex + 1);
            filename = filename.substring(0, queryIndex);
        }

        return createSign(filename) + query;
    }
}