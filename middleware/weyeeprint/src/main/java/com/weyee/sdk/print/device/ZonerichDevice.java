package com.weyee.sdk.print.device;

import com.weyee.sdk.print.constant.DeviceCode;
import com.weyee.sdk.print.constant.PrintType;

/**
 * 中琦打印机110mm
 *
 * @author wuqi by 2019-06-14.
 */
public class ZonerichDevice extends BasePrinterDevice {

    public ZonerichDevice() {
        super(DeviceCode.DEVICE_ZONERICH, PrintType.NULL);
    }

    ZonerichDevice(int deviceCode, int printType) {
        super(deviceCode, printType);
    }

    @Override
    public void otherPrint() {

    }
}
