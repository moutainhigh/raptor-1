package com.mo9.raptor.bean;


import com.mo9.raptor.enums.ResCodeEnum;

/**
 * 基础响应前端服务封装类
 * @author jyou
 */
public class BaseResponse<T> {

    public int code;

    public String message;

    public T data;

    public BaseResponse() {
    }

    public BaseResponse(T data) {
        this.code = 0;
        this.message = "操作成功";
        this.data = data;
    }

    public BaseResponse(String message, T data) {
        this.code = 0;
        this.message = message;
        this.data = data;
    }

    public BaseResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BaseResponse<T> buildFailureResponse(ResCodeEnum resCodeEnum) {
        this.code = resCodeEnum.getCode();
        this.message = resCodeEnum.getExplain();
        return this;
    }

    public BaseResponse<T> buildSuccessResponse(T data) {
        this.code = 0;
        this.message = "操作成功";
        this.data = data;
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
