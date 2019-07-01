package com.weyee.sdk.print.Interface;

import com.weyee.sdk.print.listener.PrintConnectListener;

/**
 * 打印扩展接口，支持多种连接方式
 *
 * @author wuqi by 2019-06-14.
 */
public interface IExternalAble {
    void create();

    void fail();

    void destroy();

    /**
     * 连接打印机
     *
     * @param address 连接地址，连接状态回调
     * @param port    连接端口
     */
    void connect(String address, int port);

    /**
     * 写入数据,是否是要切割数据
     */
    void write(byte[] bytes);

    /**
     * 重置打印机配置
     */
    void resetConfig();


    /**
     * 关闭连接端口
     */
    void disconnect();

    /**
     * 判断当前端口是否已经连接
     *
     * @return
     */
    boolean isConnect();

    /**
     * 数据切割的大小
     * @return
     */
    int splitLength();

    /**
     * 连接状态 => 通过回调处理
     *
     * @param listener
     */
    void callback(PrintConnectListener listener);
}
