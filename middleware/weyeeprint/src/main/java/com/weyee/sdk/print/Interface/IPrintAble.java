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

    /**
     * 开始单次打印
     */
    void onStar();

    /**
     * 失败单次打印
     */
    void onFail();

    /**
     * 结束单次打印: 正常流程
     */
    void onFinish();

    /**
     * 串口打印
     */
    void sataPrint();

    /**
     * WiFi打印
     */
    void wifiPrint();

    /**
     * usb 打印
     */
    void usbPrint();

    /**
     * 蓝牙打印
     */
    void blePrint();

    /**
     * 打印机封装的打印SDK处理
     */
    void otherPrint();
}
