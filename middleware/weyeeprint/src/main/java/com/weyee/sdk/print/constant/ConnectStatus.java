package com.weyee.sdk.print.constant;

/**
 * 配对->连接->打印->结束
 * 一整套打印流程的状态
 *
 * @author wuqi by 2019-06-27.
 */
public class ConnectStatus {
    public static final int STATE_CONNECTING = 0;   // 正在连接
    public static final int STATE_CONNECTED = 1;     // 已连接
    public static final int STATE_DISCONNECTING = 3; // 断开连接中
    public static final int STATE_DISCONNECTED = 2;  // 已断开连接


    public static final int STATE_WRITE_SUCCESS = 5;  // 写入数据成功
    public static final int STATE_WRITE_FAILURE = 6;  // 写入数据失败
    public static final int STATE_WRITE_BEGIN = 4;  // 正在写入数据
}
