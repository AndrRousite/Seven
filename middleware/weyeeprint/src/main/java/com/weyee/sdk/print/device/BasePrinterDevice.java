package com.weyee.sdk.print.device;

import androidx.annotation.Nullable;
import com.weyee.sdk.print.Interface.IConnectAble;
import com.weyee.sdk.print.Interface.IPrintAble;
import com.weyee.sdk.print.Interface.IPrintManagerAble;
import com.weyee.sdk.print.constant.PrintType;
import com.weyee.sdk.print.listener.PrintConnectListener;
import com.weyee.sdk.print.manager.BlePrintManager;
import com.weyee.sdk.print.manager.SataPrintManager;
import com.weyee.sdk.print.manager.UsbPrintManager;
import com.weyee.sdk.print.manager.WifiPrintManager;
import com.weyee.sdk.toast.ToastUtils;

import java.util.Objects;

/**
 * @author wuqi by 2019-06-14.
 */
public abstract class BasePrinterDevice implements IPrintAble, IConnectAble {
    protected int deviceCode;
    protected int printType;
    protected @Nullable
    IPrintManagerAble printManagerAble;

    BasePrinterDevice(int deviceCode, int printType) {
        this.deviceCode = deviceCode;
        this.printType = printType;
        switch (printType) {
            case PrintType.BLE:
                printManagerAble = new BlePrintManager();
                break;
            case PrintType.USB:
                printManagerAble = new UsbPrintManager();
                break;
            case PrintType.WIFI:
                printManagerAble = new WifiPrintManager();
                break;
            case PrintType.SATA:
                printManagerAble = new SataPrintManager();
                break;
            default:
                //throw new IllegalArgumentException("print manager must not null");
        }
    }

    @Override
    public void onCreate() {
        Objects.requireNonNull(printManagerAble).create();
    }

    @Override
    public int getDeviceCode() {
        return deviceCode;
    }

    @Override
    public int getPrintType() {
        return printType;
    }

    @Override
    public void connect(String address, PrintConnectListener listener) {
        Objects.requireNonNull(printManagerAble).connect(address, listener);
    }

    @Override
    public void disconnect() {
        Objects.requireNonNull(printManagerAble).disconnect();
    }

    @Override
    public void resetConfig() {
        Objects.requireNonNull(printManagerAble).resetConfig();
    }

    @Override
    public void onStar() {
        switch (printType) {
            case PrintType.BLE:
                blePrint();
                break;
            case PrintType.USB:
                usbPrint();
                break;
            case PrintType.WIFI:
                wifiPrint();
                break;
            case PrintType.SATA:
                sataPrint();
                break;
            default:
                otherPrint();
                break;
        }
    }

    @Override
    public void onFail() {
        Objects.requireNonNull(printManagerAble).fail();
    }

    @Override
    public void onFinish() {
        ToastUtils.show("打印完成");
    }

    @Override
    public void blePrint() {

    }

    @Override
    public void usbPrint() {

    }

    @Override
    public void wifiPrint() {

    }

    @Override
    public void sataPrint() {

    }

    @Override
    public void otherPrint() {

    }

    @Override
    public void onDestroy() {
        Objects.requireNonNull(printManagerAble).destroy();
    }
}
