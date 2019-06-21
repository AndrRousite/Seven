package com.weyee.sdk.print.device;

import com.weyee.sdk.print.constant.DeviceCode;
import com.weyee.sdk.print.constant.PrintType;

/**
 * 华智POS自带打印机58mm
 *
 * @author wuqi by 2019-06-14.
 */
public class HuaZhiDevice extends BasePrinterDevice{

    public HuaZhiDevice() {
        super(DeviceCode.DEVICE_HUIZHI, PrintType.NULL);
    }

    private HuaZhiDevice(int deviceCode, int printType) {
        super(deviceCode, printType);
    }

    @Override
    public void otherPrint() {

    }
}
