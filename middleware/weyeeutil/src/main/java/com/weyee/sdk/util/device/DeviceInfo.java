package com.weyee.sdk.util.device;

import android.os.Build;
import com.weyee.sdk.util.Tools;

/**
 * 设备信息
 * IMEI
 * Mac 地址
 * ANDROID_ID
 * Serial Number, SN(设备序列号)
 * UniquePsuedoID
 *
 * @author wuqi by 2019/6/4.
 */
final class DeviceInfo {
    /**
     * 应用版本号
     */
    private String versionName;
    /**
     * 手机系统版本
     */
    private String os;
    /**
     * sdk数字版本
     */
    private int sdkVersion;

    /**
     * 手机品牌
     */
    private String brand;
    /**
     * 手机型号
     */
    private String model;
    /**
     * 设备厂商
     */
    private String manufacturer;
    /**
     * IMEI码
     */
    private String IMEI;
    /**
     * 设备序列号(Android系统2.3版本以上)
     */
    private String serial;

    /**
     * 屏幕尺寸
     */
    private String screen;

    /**
     * Android Id
     */
    private String androidId;

    public DeviceInfo() {
        versionName = Tools.getVersionName();
        os = "Android" + Build.VERSION.RELEASE;
        sdkVersion = Build.VERSION.SDK_INT;

        brand = Build.BRAND;
        serial = Tools.getSerial();
        model = Build.MODEL;
        manufacturer = Build.MANUFACTURER;

        try {
            IMEI = Tools.getIMEI();
        } catch (Exception e) {
            e.printStackTrace();
        }

        screen = Tools.getScreenWidth() + "x" + Tools.getScreenHeight();

        androidId = Tools.getAndroidId();
    }

    @Override
    public String toString() {
        return "{" +
                "versionName='" + versionName + '\'' +
                ", os='" + os + '\'' +
                ", sdkVersion=" + sdkVersion +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", IMEI='" + IMEI + '\'' +
                ", serial='" + serial + '\'' +
                ", screen='" + screen + '\'' +
                ", androidId='" + androidId + '\'' +
                '}';
    }
}
