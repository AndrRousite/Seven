package com.weyee.sdk.print.Interface;

import com.weyee.sdk.print.listener.PrintConnectListener;

/**
 * 打印机连接接口
 *
 * @author wuqi by 2019-06-14.
 */
public interface IConnectAble {

    /**
     * 连接打印机
     *
     * @param address  连接地址端口，连接状态回调
     * @param listener
     */
    void connect(String address, PrintConnectListener listener);

    /**
     * 重置打印机配置
     */
    void resetConfig();


    /**
     * 关闭连接端口
     */
    void disconnect();
}
