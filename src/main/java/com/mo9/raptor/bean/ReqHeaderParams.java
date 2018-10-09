package com.mo9.raptor.bean;

/**
 * @author jyou
 * 请求头参数
 */
public interface ReqHeaderParams {

    String ACCOUNT_CODE = "Account-Code";

    String ACCESS_TOKEN = "Access-Token";

    String CLIENT_ID = "Client-Id";

    String CLIENT_VERSION = "Client-Version";

    String LANGUAGE = "Language";

    String DEVICE_ID = "Device-Id";

    String TIMESTAMP = "Timestamp";

    String SIGN = "Sign";

    String X_FORWARDED_FOR = "X-Forwarded-For";

    String PROXY_CLIENT_IP = "Proxy-Client-IP";

    String X_REAL_IP = "X-Real-IP";

    String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";

    String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
}
