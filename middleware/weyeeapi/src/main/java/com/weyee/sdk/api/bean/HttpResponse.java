package com.weyee.sdk.api.bean;

/**
 * <p>
 * 返回数据基类
 *
 * @author wuqi
 * @describe ...
 * @date 2018/12/7 0007
 */
public class HttpResponse<T> {

    private int status;

    private String error;

    private T data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
