package com.mo9.raptor.bean.req;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 */
public class FileReq {

    /**
     * 文件
     */
    @NotNull(message = "文件不能为空")
    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
