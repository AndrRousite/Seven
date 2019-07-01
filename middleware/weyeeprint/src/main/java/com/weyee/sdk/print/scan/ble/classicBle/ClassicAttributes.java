package com.weyee.sdk.print.scan.ble.classicBle;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wuqi by 2019-06-16.
 */
@SuppressWarnings({"unused"})
final class ClassicAttributes {
    public static final Map<String, String> attributes = new HashMap<>();
    //Classic Services
    public static final String UUID_SECURE_DEVICE = "00001800-0000-1000-8000-00805F9B34FB";
    public static final String UUID_INSECURE_DEVICE = "00001800-0000-1000-8000-00805F9B34FB";

    static {
        attributes.put(UUID_SECURE_DEVICE, "bluetooth secure uuid");
        attributes.put(UUID_INSECURE_DEVICE, "bluetooth insecure uuid");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid.toUpperCase());
        return name == null ? defaultName : name;
    }
}
