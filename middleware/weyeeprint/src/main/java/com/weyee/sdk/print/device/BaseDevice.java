package com.weyee.sdk.print.device;

import android.util.SparseArray;
import androidx.annotation.Nullable;
import com.weyee.sdk.print.Interface.IConnectAble;
import com.weyee.sdk.print.Interface.IExternalAble;
import com.weyee.sdk.print.Interface.IPrintAble;
import com.weyee.sdk.print.constant.PrintType;
import com.weyee.sdk.print.device.external.*;
import com.weyee.sdk.print.listener.PrintConnectListener;
import com.weyee.sdk.util.number.MNumberUtil;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author wuqi by 2019-06-14.
 */
public abstract class BaseDevice implements IPrintAble, IConnectAble {
    protected int deviceCode;
    protected int printType;
    protected @Nullable
    IExternalAble iExternalAble;  // 当前选中的打印方式
    private SparseArray<IExternalAble> externalAbleSparseArray = new SparseArray<>();

    BaseDevice(int deviceCode) {
        this.deviceCode = deviceCode;
    }

    /**
     * @hide 不要手动调用，否则很有坑空指针
     */
    @Override
    public void onCreate() {
        Objects.requireNonNull(iExternalAble).create();
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
    public void connect(String var1, PrintConnectListener listener) {
        var1 = var1.trim().toUpperCase();

        String address = null;
        int port = 0;


        if (Pattern.compile("[0-9,A-F,a-f]{2}:[0-9,A-F,a-f]{2}:[0-9,A-F,a-f]{2}:[0-9,A-F,a-f]{2}:[0-9,A-F,a-f]{2}:[0-9,A-F,a-f]{2}").matcher(var1).matches()) {
            // 蓝牙
            address = var1;
            printType = PrintType.BLE;
        } else if (Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}:[0-9]{1,6}").matcher(var1).matches()) {
            // WiFi
            int length = var1.indexOf(58);
            address = var1.substring(0, length);
            port = MNumberUtil.convertToint(var1.substring(length + 1));
            printType = PrintType.WIFI;
        } else if (Pattern.compile("((TTYS)|(TTYUSB)|(S3C2410_SERIAL))[0-9]:((2400)|(4800)|(9600)|(19200)|(38400)|(57600)|(115200))").matcher(var1).matches()) {
            // COM
            int length = var1.indexOf(58);
            address = var1.substring(0, length);
            address = address.replace("TTYS", "ttyS").replace("TTYUSB", "ttyUSB").replace("S3C2410_SERIAL", "s3c2410_serial");
            port = MNumberUtil.convertToint(var1.substring(length + 1));
            printType = PrintType.SATA;
        } else if (Pattern.compile("USB[0-9]+\\|[0-9]+").matcher(var1).matches()) {
            // USB
            var1 = var1.replace("USB", "");
            int length = var1.indexOf(124);
            address = var1.substring(0, length);
            port = MNumberUtil.convertToint(var1.substring(length + 1));
            printType = PrintType.USB;
        } else {
            // throw new Exception("该设备不支持");
        }

        IExternalAble able = externalAbleSparseArray.get(printType);
        if (able == null) {
            switch (printType) {
                case PrintType.BLE:
                    iExternalAble = new BlePrintManager();
                    break;
                case PrintType.USB:
                    iExternalAble = new UsbPrintManager();
                    break;
                case PrintType.WIFI:
                    iExternalAble = new WifiPrintManager();
                    break;
                case PrintType.SATA:
                    iExternalAble = new SataPrintManager();
                    break;
                default:
                    //throw new IllegalArgumentException("print manager must not null");
            }
            externalAbleSparseArray.put(printType, iExternalAble);
        } else {
            iExternalAble = able;
        }

        // 设置监听->初始化连接->连接
        Objects.requireNonNull(iExternalAble).callback(listener);
        Objects.requireNonNull(iExternalAble).create();
        Objects.requireNonNull(iExternalAble).connect(address, port);
    }

    @Override
    public void disconnect() {
        Objects.requireNonNull(iExternalAble).disconnect();
    }

    @Override
    public void resetConfig() {
        Objects.requireNonNull(iExternalAble).resetConfig();
    }

    @Override
    public void onDestroy() {
        Objects.requireNonNull(iExternalAble).destroy();
    }

    @Override
    public void write(byte[] bytes) {
        Objects.requireNonNull(iExternalAble).write(bytes);
    }
}
