package com.weyee.sdk.print.listener;

/**
 * 打印机连接状态回调
 *
 * @author wuqi by 2019-06-14.
 */
public interface PrintConnectListener {

    /**
     * 开始连接
     */
    void onStart();

    /**
     * 连接中
     */
    void onProcess();

    /**
     * 连接成功
     */
    void onSuccess();

    /**
     * 连接失败
     */
    void onError(int code, String msg);


    /**
     * 连接完成
     */
    void onComplete();
}
