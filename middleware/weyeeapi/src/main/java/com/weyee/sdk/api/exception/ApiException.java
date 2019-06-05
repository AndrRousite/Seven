package com.weyee.sdk.api.exception;

/**
 * @author wuqi
 * Created by liu-feng on 2018/12/7 0007
 * 异常类型 : 后台接口自定义的错误类型
 */
public class ApiException extends Exception {

    private final int code;
    private String message;

    public ApiException(int code, String msg) {
        super(msg);
        this.code = code;
        this.message = msg;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public static ApiException handleException(int code, String msg) {
        return new ApiException(code, msg);
    }
}
