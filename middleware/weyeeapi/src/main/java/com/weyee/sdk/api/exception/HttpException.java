package com.weyee.sdk.api.exception;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.NotSerializableException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

/**
 * @author wuqi
 * Created by liu-feng on 2018/12/7 0007
 * 异常类型
 */
public class HttpException extends Exception {

    private final int code;
    private String message;

    public HttpException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
        this.message = throwable.getMessage();
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public static HttpException handleException(Throwable e) {
        HttpException ex;
        if (e instanceof retrofit2.HttpException) {
            retrofit2.HttpException httpException = (retrofit2.HttpException) e;
            ex = new HttpException(httpException, httpException.code());
            ex.message = "网络错误";
        } else if (e instanceof SocketTimeoutException) {
            ex = new HttpException(e, ERROR.TIMEOUT_ERROR);
            ex.message = "连接超时";
        } else if (e instanceof ConnectException) {
            ex = new HttpException(e, ERROR.TIMEOUT_ERROR);
            ex.message = "连接异常";
        } else if (e instanceof ConnectTimeoutException) {
            ex = new HttpException(e, ERROR.TIMEOUT_ERROR);
            ex.message = "连接超时";
        } else if (e instanceof UnknownHostException) {
            ex = new HttpException(e, ERROR.TIMEOUT_ERROR);
            ex.message = "连接异常";
        } else if (e instanceof NullPointerException) {
            ex = new HttpException(e, ERROR.NULL_POINTER_EXCEPTION);
            ex.message = "空指针异常";
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new HttpException(e, ERROR.SSL_ERROR);
            ex.message = "证书验证失败";
        } else if (e instanceof ClassCastException) {
            ex = new HttpException(e, ERROR.CAST_ERROR);
            ex.message = "类型转换错误";
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof JsonSerializer
                || e instanceof NotSerializableException
                || e instanceof ParseException) {
            ex = new HttpException(e, ERROR.PARSE_ERROR);
            ex.message = "解析错误";
        } else if (e instanceof IllegalStateException) {
            ex = new HttpException(e, ERROR.ILLEGAL_STATE_ERROR);
            ex.message = e.getMessage();
        } else {
            ex = new HttpException(e, ERROR.UNKNOWN);
            ex.message = "网络出了点小差错~";
        }
        return ex;
    }

    /**
     * 约定异常
     */
    public static class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = 1001;
        /**
         * 空指针错误
         */
        public static final int NULL_POINTER_EXCEPTION = 1002;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1003;

        /**
         * 类转换错误
         */
        public static final int CAST_ERROR = 1004;

        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1005;

        /**
         * 非法数据异常
         */
        public static final int ILLEGAL_STATE_ERROR = 1006;

    }
}
