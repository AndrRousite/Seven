package com.weyee.sdk.print.device.external;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import com.weyee.sdk.print.constant.ConnectStatus;
import com.weyee.sdk.print.utils.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Queue;
import java.util.UUID;

/**
 * 蓝牙ESC打印
 *
 * @author wuqi by 2019-06-14.
 */
public class BlePrintManager extends BasePrintManager {
    private static final String UUID_SECURE_DEVICE = "00001800-0000-1000-8000-00805F9B34FB";
    private static final String UUID_INSECURE_DEVICE = "00001800-0000-1000-8000-00805F9B34FB";

    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothSocket bluetoothSocket = null;
    private OutputStream outputStream = null;

    @Override
    public void create() {
        super.create();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void connect(String address, int port) {
        super.connect(address, port);
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        if (bluetoothAdapter == null || address == null) {
            return;
        }

        if (bluetoothSocket != null && address.equals(bluetoothSocket.getRemoteDevice().getAddress())) {
            if (bluetoothSocket.isConnected()) {
                mHandler.obtainMessage(ConnectStatus.STATE_CONNECTED, -1, -1, null).sendToTarget();
                return;
            }
        }

        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
        if (bluetoothDevice == null) {
            return;
        }

        try {
            if (Boolean.TRUE) {
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(UUID_SECURE_DEVICE));
            } else {
                bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(UUID_INSECURE_DEVICE));
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        connectThreadExecutor.execute(new ConnectRunnable(bluetoothDevice));
    }

    @Override
    public void write(byte[] bytes) {
        super.write(bytes);
        writeThreadExecutor.execute(new WriteRunnable(bytes));
    }

    @Override
    public void disconnect() {
        super.disconnect();
        try {
            if (outputStream != null)
                outputStream.close();
        } catch (IOException ignore) {
        }
        try {
            if (bluetoothSocket != null)
                bluetoothSocket.close();
        } catch (IOException ignore) {
        }
    }

    @Override
    public boolean isConnect() {
        return bluetoothSocket != null && bluetoothSocket.isConnected();
    }

    @Override
    public int splitLength() {
        return super.splitLength();
    }

    /**
     * 连接线程
     */
    final class ConnectRunnable implements Runnable {
        BluetoothDevice bluetoothDevice;

        ConnectRunnable(BluetoothDevice bluetoothDevice) {
            this.bluetoothDevice = bluetoothDevice;
        }

        @Override
        public void run() {
            try {
                bluetoothSocket.connect();
                mHandler.obtainMessage(ConnectStatus.STATE_CONNECTED, -1, -1, null).sendToTarget();
            } catch (IOException ignore) {
                try {
                    Method m = bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                    bluetoothSocket = (BluetoothSocket) m.invoke(bluetoothDevice, 1);
                    bluetoothSocket.connect();
                    mHandler.obtainMessage(ConnectStatus.STATE_CONNECTED, -1, -1, null).sendToTarget();
                } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    try {
                        // Close the socket
                        bluetoothSocket.close();
                    } catch (IOException ignore2) {
                    }
                    mHandler.obtainMessage(ConnectStatus.STATE_DISCONNECTED, -1, -1, null).sendToTarget();
                }
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
            try {
                outputStream = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            // 先判断数据和是否连接
            if (bytes == null || !isConnect()) {
                mHandler.obtainMessage(ConnectStatus.STATE_DISCONNECTED, -1, -1, null).sendToTarget();
                return;
            }

            mHandler.obtainMessage(ConnectStatus.STATE_WRITE_BEGIN, -1, -1, null).sendToTarget();

            if (outputStream != null) {
                // 每次发送20个字节
                Queue<byte[]> queue = Utils.splitByte(bytes, splitLength());
                while (!queue.isEmpty()) {
                    try {
                        outputStream.write(queue.poll());
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mHandler.obtainMessage(ConnectStatus.STATE_WRITE_SUCCESS, -1, -1, null).sendToTarget();
            } else {
                mHandler.obtainMessage(ConnectStatus.STATE_WRITE_FAILURE, -1, -1, null).sendToTarget();
            }

        }
    }
}
