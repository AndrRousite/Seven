package com.weyee.sdk.print.Interface;

/**
 * 打印接口
 *
 * @author wuqi by 2019-06-14.
 */
public interface IPrintAble {
    /**
     * 打印初始化设置
     */
    void onCreate();

    /**
     * 可做资源销毁处理
     */
    void onDestroy();

    /**
     * 获取设备代号.定义在{@link com.weyee.sdk.print.constant.DeviceCode}
     */
    int getDeviceCode();

    /**
     * 获取设备连接类型.定义在{@link com.weyee.sdk.print.constant.PrintType}
     */
    int getPrintType();
}
