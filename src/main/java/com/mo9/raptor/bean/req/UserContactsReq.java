package com.mo9.raptor.bean.req;

import javax.validation.constraints.NotBlank;

/**
 * Created by jyou on 2018/9/17.
 *
 * @author jyou
 */
public class UserContactsReq {

    @NotBlank(message = "数据不能为空")
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
