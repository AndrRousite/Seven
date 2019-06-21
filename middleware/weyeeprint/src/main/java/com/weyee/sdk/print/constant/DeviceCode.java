package com.weyee.sdk.print.constant;

import com.weyee.sdk.util.sp.SpUtils;

/**
 * 打印机设置编号，不同打印机连接、打印、模板等 不一致
 *
 * @author wuqi by 2019-06-14.
 */
public class DeviceCode {
    public static final int DEVICE_NULL = 0; //无连接
    public static final int DEVICE_TPS900 = 1; //58mm 手持pos
    public static final int DEVICE_HUIZHI = 2; // 58mm 手持pos 华智
    public static final int DEVICE_GPRINTER = 3; //80mm 佳博
    public static final int DEVICE_QS = 4; // 80mm 群索
    public static final int DEVICE_ZONERICH = 5; //110mm 中绮
    public static final int DEVICE_CS = 6; // 110mm 芝科
    public static final int DEVICE_JOLIMARK = 7; //150mm 映美
    public static final int DEVICE_EPSON = 8; //210mm 爱普生
    public static final int DEVICE_DS = 9; // 210mm得实


    public static void saveDeviceCode(int code) {
        SpUtils.getDefault().put(DeviceCode.class.getSimpleName(), code);
    }

    public static int getDeviceCode() {
        return SpUtils.getDefault().getInt(DeviceCode.class.getSimpleName(), DEVICE_NULL);
    }
}
