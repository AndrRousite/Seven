package com.weyee.sdk.print.scan.ble.leBle;

/**
 * @author wuqi by 2019-06-16.
 */
@SuppressWarnings({"unused"})
public final class GattAttributes {
    /**
     * 服务的UUID
     */
    public static final String SERVICE_UUID = "00006a00-0000-1000-8000-00805f9b34fb";
    /**
     * 订阅通知的UUID
     */
    public static final String NOTIFY_UUID = "00006a02-0000-1000-8000-00805f9b34fb";
    /**
     * 写出数据的UUID
     */
    public static final String WRITE_UUID = "00006a02-0000-1000-8000-00805f9b34fb";

    /**
     * NOTIFY里面的Descriptor UUID
     */
    public static final String NOTIFY_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb";
}
