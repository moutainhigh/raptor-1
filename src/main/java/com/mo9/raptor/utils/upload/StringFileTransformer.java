package com.mo9.raptor.utils.upload;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @Authro qygu.
 * @Email qiyao.gu@qq.com.
 * @Date 2017/8/15.
 */
public class StringFileTransformer implements FileStreamTransformer {

    private byte[] data;
    private String fileName;

    public StringFileTransformer(String fileName, String content) {
        this.fileName = fileName;
        this.data = content.getBytes();
    }

    @Override
    public String getParameterName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(data);
    }

    @Override
    public long getSize() {
        return data.length;
    }

    @Override
    public byte[] getData() {
        return data;
    }
}