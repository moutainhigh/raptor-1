package com.mo9.raptor.utils.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.mo9.raptor.utils.upload.FileStreamTransformer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.util.UUID;

/**
 * @Authro qygu.
 * @Email qiyao.gu@qq.com.
 * @Date 2017/6/15.
 */
@Component
public class OSSFileUpload {

    @Resource
    private OSSProperties ossProperties;
    /**
     * 文件上传
     *
     * @return 上传成功后的文件地址
     */


    public String upload(FileStreamTransformer fileStreamTransformer) {
        ObjectMetadata meta = new ObjectMetadata();
        String fileName = generateFileName(fileStreamTransformer.getFileName());

        meta.setContentLength(fileStreamTransformer.getSize());

        new OSSClient(ossProperties.getWriteEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret()).putObject(
                ossProperties.getBucketName(),
                fileName,
                fileStreamTransformer.getInputStream(),
                meta
        );

        return buildFileURL(fileName);
    }

    /**
     *
     * @param inputStream 字节流
     * @param format 图片格式 默认jpg
     * @return 上传成功后的文件地址
     */
    public String upload(ByteArrayInputStream inputStream,String format) {
        String fileName = generateFileNameByFormat(format == null ? "jpg" : format);
        new OSSClient(ossProperties.getWriteEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret()).putObject(
                ossProperties.getBucketName(),
                fileName,
                inputStream
        );
        return buildFileURL(fileName);
    }


    /**
     * 通过图片格式完成文件名生成
     * @param format
     * @return
     */
    private String generateFileNameByFormat(String format) {
        return ossProperties.getCatalog() + "/" + UUID.randomUUID().toString().replace("-", StringUtils.EMPTY) + "."
                + format;
    }
    private String generateFileName(String oldFileName) {
        return ossProperties.getCatalog() + "/" + UUID.randomUUID().toString().replace("-", StringUtils.EMPTY) + "."
                + FilenameUtils.getExtension(oldFileName);
    }

    private String buildFileURL(String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append(ossProperties.getHttpPrefix())
                .append(ossProperties.getReadEndpoint().substring(ossProperties.getHttpPrefix().length()))
                .append("/").append(fileName);

        return sb.toString();
    }

}