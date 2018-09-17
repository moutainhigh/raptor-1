package com.mo9.raptor.utils.upload;

import java.io.InputStream;

/**
 * @Authro qygu.
 * @Email qiyao.gu@qq.com.
 * @Date 2017/6/15.
 */
public interface FileStreamTransformer {

    /**
     * 参数名称
     */
    String getParameterName();

    /**
     * 文件名称
     */
    String getFileName();

    /**
     * 文件流
     */
    InputStream getInputStream();

    /**
     * contentType
     */
    String getContentType();

    /**
     * 文件大小
     */
    long getSize();

    /**
     * 文件数据
     */
    byte[] getData();

}