package com.weyee.sdk.print.Interface;

import com.weyee.sdk.print.listener.PrintConnectListener;

/**
 * @author wuqi by 2019-06-14.
 */
public interface IPrintManagerAble {
    void create();

    void fail();

    void destroy();

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
