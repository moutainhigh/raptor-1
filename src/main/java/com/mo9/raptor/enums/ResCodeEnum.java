package com.mo9.raptor.enums;

/**
 * @author jyou
 */
public enum ResCodeEnum {

    /**
     * 包含所有需要返回的错误码，请及时补充注释，其实格式如下：
     * 1115xxyy，1115：代表业务系统， xx代表业务模块， yy：代表具体错误码
     * <p>
     */
    SUCCESS(11140000, "成功", "成功"),

    ;
    /**
     * 错误码
     */
    private int code;

    /**
     * 注释说明, 用于实际说明问题, 与code一对一, 不可重复
     */
    private String explain;

    /**
     * 描述, 用于返回给前端, 可以重复
     */
    private String message;

    ResCodeEnum(int code, String explain, String message) {
        this.code = code;
        this.explain = explain;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getExplain() {
        return explain;
    }

    public String getMessage() {
        return message;
    }
}
