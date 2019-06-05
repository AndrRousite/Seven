/*
 *
 *  Copyright 2017 liu-feng
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  imitations under the License.
 *
 */

package com.weyee.sdk.util;

import android.annotation.SuppressLint;
import android.app.Application;
import com.blankj.utilcode.util.*;
import com.weyee.sdk.util.device.DeviceHelper;

import java.io.File;

/**
 * @author wuqi by 2019/3/12.
 */
public class Tools {

    public static Application getApp() {
        return Utils.getApp();
    }

    @SuppressLint("MissingPermission")
    public static boolean isConnected() {
        return NetworkUtils.isConnected();
    }

    public static int getScreenWidth() {
        return ScreenUtils.getScreenWidth();
    }

    public static int getScreenHeight() {
        return ScreenUtils.getScreenHeight();
    }

    public static boolean createOrExistsDir(File file) {
        return FileUtils.createOrExistsDir(file);
    }

    public static String encodeSha1(final String data, final String key) {
        return EncryptUtils.encryptHmacSHA1ToString(data, key);
    }

    public static String getVersionName() {
        return AppUtils.getAppVersionName();
    }

    @SuppressLint("MissingPermission")
    public static String getIMEI() {
        return PhoneUtils.getIMEI();
    }

    @SuppressLint("MissingPermission")
    public static String getSerial() {
        return PhoneUtils.getSerial();
    }

    public static String getAndroidId() {
        return DeviceUtils.getModel();
    }

    public static String getDeviceInfo() {
        return DeviceHelper.getDeviceInfo();
    }
}
