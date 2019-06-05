package com.weyee.sdk.util.device;

/**
 * @author wuqi by 2019/6/4.
 */
public class DeviceHelper {

    private static DeviceHelper helper;
    private String info;

    private DeviceHelper() {
        info = new DeviceInfo().toString();
    }

    public static String getDeviceInfo() {
        if (helper == null) {
            synchronized (DeviceHelper.class) {
                if (helper == null) {
                    helper = new DeviceHelper();
                }
            }
        }
        return helper.info;
    }
}
