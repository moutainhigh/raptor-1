package com.mo9.raptor.utils.upload;

import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Authro qygu.
 * @Email qiyao.gu@qq.com.
 * @Date 2017/6/15.
 */
public class SpringMultipartFileTransformer implements FileStreamTransformer {

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 文件参数名称
     */
    private String parameterName;

    /**
     * 文件长度
     */
    private long size;

    /**
     * 文件二进制数据
     */
    private byte[] data;

    /**
     * 文件流对象
     */
    private InputStream inputStream;

    private SpringMultipartFileTransformer() {
    }

    public static FileStreamTransformer transformer(MultipartFile multipartFile) throws IOException {
        if (ObjectUtils.isEmpty(multipartFile)) {
            throw new NullPointerException();
        }

        SpringMultipartFileTransformer fileStreamTransformer = new SpringMultipartFileTransformer();

        fileStreamTransformer.size = multipartFile.getSize();
        fileStreamTransformer.data = multipartFile.getBytes();
        fileStreamTransformer.parameterName = multipartFile.getName();
        fileStreamTransformer.contentType = multipartFile.getContentType();
        fileStreamTransformer.inputStream = multipartFile.getInputStream();
        fileStreamTransformer.fileName = multipartFile.getOriginalFilename();

        return fileStreamTransformer;
    }

    @Override
    public String getParameterName() {
        return parameterName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public byte[] getData() {
        return data;
    }

}