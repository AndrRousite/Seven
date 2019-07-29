package com.weyee.sdk.print.device.external;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.*;
import com.weyee.sdk.log.LogUtils;
import com.weyee.sdk.print.constant.ConnectStatus;
import com.weyee.sdk.print.utils.Utils;
import com.weyee.sdk.util.Tools;

import java.util.Arrays;
import java.util.Queue;

/**
 * Usb打印管理类
 *
 * @author wuqi by 2019-06-14.
 */
public class UsbPrintManager extends BasePrintManager {
    private static final String ACTION_USB_DEVICE_PERMISSION = "com.weyee.sdk.usb.USB_PERMISSION";

    private UsbManager usbManager;
    private UsbDevice mUsbDevice;
    private UsbDeviceConnection usbDeviceConnection;

    @Override
    public void create() {
        super.create();
        usbManager = (UsbManager) Tools.getApp().getSystemService(Context.USB_SERVICE);
    }

    @Override
    public void connect(String address, int port) {
        super.connect(address, port);
        if (address == null) return;
        if (usbManager != null) {
            for (UsbDevice device : usbManager.getDeviceList().values()) {
                // 必须保证VendorId和ProductId一致
                if (address.equals(String.valueOf(device.getVendorId())) && String.valueOf(port).equals(String.valueOf(device.getProductId()))) {
                    mUsbDevice = device; // 获取USBDevice
                    break;
                }
            }
            connectThreadExecutor.execute(new ConnectRunnable(mUsbDevice));
        }
    }

    @Override
    public void write(byte[] bytes) {
        super.write(bytes);
        writeThreadExecutor.execute(new WriteRunnable(bytes));
    }

    /**
     * USB bulkTransfer通讯必须注意的问题:bulk buffer size（16K）
     *
     * @return
     */
    @Override
    public int splitLength() {
        return 16 * 1024;
    }

    @Override
    public void disconnect() {
        super.disconnect();
        if (usbDeviceConnection != null) {
            usbDeviceConnection.close();
            usbDeviceConnection = null;
        }
        mUsbDevice = null;
    }

    @Override
    public boolean isConnect() {
        return usbDeviceConnection != null;
    }

    /**
     * 连接线程
     */
    final class ConnectRunnable implements Runnable {
        UsbDevice usbDevice;

        ConnectRunnable(UsbDevice usbDevice) {
            this.usbDevice = usbDevice;
        }

        @Override
        public void run() {
            try {
                if (usbManager.hasPermission(usbDevice)) {
                    //有权限，那么打开
                    usbDeviceConnection = usbManager.openDevice(usbDevice);
                } else {
                    usbManager.requestPermission(usbDevice, PendingIntent.getBroadcast(Tools.getApp(), 0, new Intent(ACTION_USB_DEVICE_PERMISSION), 0));
                    if (usbManager.hasPermission(usbDevice)) { //权限获取成功
                        usbDeviceConnection = usbManager.openDevice(usbDevice);
                    }
                }
                mHandler.obtainMessage(ConnectStatus.STATE_CONNECTED, -1, -1, null).sendToTarget();
            } catch (Exception ignore) {
                mHandler.obtainMessage(ConnectStatus.STATE_DISCONNECTED, -1, -1, null).sendToTarget();
            }
        }
    }

    /**
     * 该类支持阻塞性的发送数据
     */
    final class WriteRunnable implements Runnable {
        byte[] bytes;

        WriteRunnable(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public void run() {
            // 先判断数据和是否连接
            if (bytes == null || !isConnect()) {
                mHandler.obtainMessage(ConnectStatus.STATE_DISCONNECTED, -1, -1, null).sendToTarget();
                return;
            }

            mHandler.obtainMessage(ConnectStatus.STATE_WRITE_BEGIN, -1, -1, null).sendToTarget();

            if (mUsbDevice.getInterfaceCount() > 0) {
                UsbInterface usbInterface = mUsbDevice.getInterface(0);
                UsbEndpoint epBulkOut = null, epBulkIn = null;
                loop:
                for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                    UsbEndpoint ep = usbInterface.getEndpoint(i);
                    switch (ep.getType()) {
                        case UsbConstants.USB_ENDPOINT_XFER_BULK://USB端口传输
                            if (UsbConstants.USB_DIR_OUT == ep.getDirection()) {//输出
                                epBulkOut = ep;
                                LogUtils.e("获取发送数据的端点");
                                break loop;
                            } else {
                                epBulkIn = ep;
                                LogUtils.e("获取接受数据的端点");
                            }
                            break;
                        case UsbConstants.USB_ENDPOINT_XFER_CONTROL://控制
                            break;
                        case UsbConstants.USB_ENDPOINT_XFER_INT://中断
                            if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {//输出
                                //
                            }
                            if (ep.getDirection() == UsbConstants.USB_DIR_IN) {
                                //
                            }
                            break;
                        default:
                            break;
                    }
                }

                if (usbDeviceConnection.claimInterface(usbInterface, true)) {
                    if (epBulkOut != null) {
                        // 每次发送16k字节
                        Queue<byte[]> queue = Utils.splitByte(bytes, splitLength());

                        while (!queue.isEmpty()) {

                            //写入你需要传递给外设的特征值（即传递给外设的信息）
                            byte[] data = queue.poll();
                            LogUtils.e("byte=" + Arrays.toString(data));
                            //0 或者正数表示发送成功，严谨点的话这里是需要处理发送失败的
                            usbDeviceConnection.bulkTransfer(epBulkOut, data, data.length, 0);
                        }

                        mHandler.obtainMessage(ConnectStatus.STATE_WRITE_SUCCESS, -1, -1, null).sendToTarget();
                        disconnect();
                        return;
                    }
                }
            }

            mHandler.obtainMessage(ConnectStatus.STATE_WRITE_FAILURE, -1, -1, null).sendToTarget();

            // 最后不管成功失败，断开连接
            disconnect();

        }
    }
}
