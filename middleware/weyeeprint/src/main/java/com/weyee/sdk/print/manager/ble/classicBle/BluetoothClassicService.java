package com.weyee.sdk.print.manager.ble.classicBle;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import androidx.annotation.Nullable;
import com.blankj.utilcode.util.LogUtils;
import com.weyee.sdk.print.manager.ble.IBluetoothAble;
import com.weyee.sdk.print.manager.ble.IBluetoothListener;
import com.weyee.sdk.util.Tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author wuqi by 2019-06-19.
 */
public class BluetoothClassicService extends Service implements IBluetoothAble {
    //Debug
    private static final String TAG = BluetoothClassicService.class.getName();

    private final IBinder mBinder = new BluetoothClassicService.LocalBinder();

    private BluetoothAdapter mBluetoothAdapter;

    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    /**
     * 记住上次连接的设备
     */
    private String mBluetoothDeviceAddress;

    private List<BluetoothDevice> mScanLeDeviceList = new ArrayList<>();
    private boolean isScanning;

    private IBluetoothListener bluetoothClassicListener;
    private BluetoothReceiver bluetoothReceiver;


    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECT:
                    if (bluetoothClassicListener != null) {
                        bluetoothClassicListener.onConnectionStateChange(mBluetoothAdapter.getRemoteDevice(mBluetoothDeviceAddress), -1, msg.arg1);
                    }
                    break;
                case WRITE:
                    if (bluetoothClassicListener != null) {
                        bluetoothClassicListener.onConnectionStateChange(mBluetoothAdapter.getRemoteDevice(mBluetoothDeviceAddress), -1, msg.arg1);
                        if (bluetoothClassicListener instanceof BluetoothClassicListener)
                            ((BluetoothClassicListener) bluetoothClassicListener).write(mBluetoothAdapter.getRemoteDevice(mBluetoothDeviceAddress), new String((byte[]) msg.obj));
                    }
                    break;
                case READ:
                    if (bluetoothClassicListener != null) {
                        bluetoothClassicListener.onConnectionStateChange(mBluetoothAdapter.getRemoteDevice(mBluetoothDeviceAddress), -1, msg.arg1);
                        if (bluetoothClassicListener instanceof BluetoothClassicListener)
                            ((BluetoothClassicListener) bluetoothClassicListener).read(mBluetoothAdapter.getRemoteDevice(mBluetoothDeviceAddress), new String((byte[]) msg.obj, 0, msg.arg2));
                    }
                    break;
                case SCAN:
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) msg.obj;
                    if (bluetoothDevice == null) {
                        changeScanState(false);
                    } else {
                        switch (msg.arg1) {
                            case BluetoothDevice.BOND_BONDED:
                            case BluetoothDevice.BOND_NONE:
                            case BluetoothDevice.BOND_BONDING:
                                if (bluetoothClassicListener != null) {
                                    bluetoothClassicListener.onConnectionStateChange(bluetoothDevice, -1, msg.arg1);
                                }
                            default:
                                if (mScanLeDeviceList.contains(bluetoothDevice)) break;
                                mScanLeDeviceList.add(bluetoothDevice);
                                if (bluetoothClassicListener != null) {
                                    bluetoothClassicListener.onClassicScan(bluetoothDevice);
                                }
                                break;
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public boolean initialize() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        bluetoothReceiver = new BluetoothReceiver();

        IntentFilter btFilter = new IntentFilter();
        btFilter.addAction(BluetoothDevice.ACTION_FOUND);
        btFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        btFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        btFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        Tools.getApp().registerReceiver(bluetoothReceiver, btFilter);

        return mBluetoothAdapter != null;
    }

    @Override
    public boolean isSupportBle() {
        return Tools.getApp().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean isEnableBluetooth() {
        return mBluetoothAdapter.isEnabled();
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean enableBluetooth(boolean enable) {
        if (enable) {
            if (!mBluetoothAdapter.isEnabled()) {
                return mBluetoothAdapter.enable();
            }
            return true;
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                return mBluetoothAdapter.disable();
            }
            return false;
        }
    }

    @Override
    public void setIBluetoothListener(IBluetoothListener bluetoothLeListener) {
        this.bluetoothClassicListener = bluetoothLeListener;
    }

    @Override
    public void startDiscovery() {
        startDiscovery(10000);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void startDiscovery(long scanPeriod) {
        if (isEnableBluetooth()) {
            if (isScanning()) return;
            cancelDiscovery();

            mScanLeDeviceList.clear();
            changeScanState(true);
            mBluetoothAdapter.startDiscovery();
        } else {
            cancelDiscovery();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void cancelDiscovery() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    @Override
    public boolean isScanning() {
        return isScanning;
    }

    /**
     * 改变蓝牙扫描的状态
     *
     * @param isScanning
     */
    private void changeScanState(boolean isScanning) {
        this.isScanning = isScanning;
        if (bluetoothClassicListener != null) {
            bluetoothClassicListener.onScanStateChange(isScanning);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean connect(String address) {
        disconnect();
        connectThread = new ConnectThread(address, true);
        connectThread.start();
        return true;
    }

    @Override
    public void write(byte[] bytes) {
        if (connectedThread != null) {
            connectedThread.write(bytes);
        }
    }

    @Override
    public void disconnect() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
    }

    @Override
    public void close() {
        disconnect();
    }

    @Override
    public void destroy() {
        cancelDiscovery();
        close();
        if (bluetoothReceiver != null) {
            Tools.getApp().unregisterReceiver(bluetoothReceiver);
            bluetoothReceiver = null;
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    @SuppressLint("MissingPermission")
    @Override
    public Set<BluetoothDevice> getBondedDevices() {
        if (mBluetoothAdapter == null) {
            return Collections.emptySet();
        }
        return mBluetoothAdapter.getBondedDevices();
    }

    @Override
    public Set<BluetoothDevice> getScanDevices() {
        Set<BluetoothDevice> deviceSet = new HashSet<>(mScanLeDeviceList);
        return Collections.unmodifiableSet(deviceSet);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        destroy();
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public BluetoothClassicService getService() {
            return BluetoothClassicService.this;
        }
    }

    class BluetoothReceiver extends BroadcastReceiver {

        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    LogUtils.d(String.format("蓝牙扫描中......: %s", device.getName()));
                    mHandler.obtainMessage(SCAN, -1, -1, device).sendToTarget();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    LogUtils.d("蓝牙扫描结束");
                    mHandler.obtainMessage(SCAN, -1, -1, null).sendToTarget();
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                    switch (state) {
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            LogUtils.d("蓝牙关闭中......");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            LogUtils.d("蓝牙已关闭");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            LogUtils.d("蓝牙已开启");
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            LogUtils.d("蓝牙开启中......");
                            break;
                        default:
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    switch (device.getBondState()) {
                        case BluetoothDevice.BOND_BONDING:
                            LogUtils.d("正在配对......");
                            mHandler.obtainMessage(SCAN, BluetoothDevice.BOND_BONDING, -1, device).sendToTarget();
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            LogUtils.d("完成配对");
                            mHandler.obtainMessage(SCAN, BluetoothDevice.BOND_BONDED, -1, device).sendToTarget();
                            break;
                        case BluetoothDevice.BOND_NONE:
                            //取消配对/未配对
                            LogUtils.d("取消配对");
                            mHandler.obtainMessage(SCAN, BluetoothDevice.BOND_NONE, -1, device).sendToTarget();
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket bluetoothSocket;
        private BluetoothDevice bluetoothDevice;

        @SuppressLint("MissingPermission")
        ConnectThread(String address, boolean secure) {

            mHandler.obtainMessage(CONNECT, BluetoothProfile.STATE_CONNECTING, -1, null).sendToTarget();

            if (isScanning()) {
                cancelDiscovery();
            }
            if (mBluetoothAdapter == null || address == null) {
                return;
            }
            //Previously connected device.  Try to reconnect.
            if (bluetoothSocket != null && address.equals(mBluetoothDeviceAddress)) {
                if (bluetoothSocket.isConnected()) {
                    mHandler.obtainMessage(CONNECT, BluetoothProfile.STATE_CONNECTED, -1, null).sendToTarget();
                    return;
                }
            }
            bluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
            if (bluetoothDevice == null) {
                return;
            }
            mBluetoothDeviceAddress = address;
            //We want to directly connect to the device, so we are setting the autoConnect
            // parameter to false.
            try {
                if (secure) {
                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(ClassicAttributes.UUID_SECURE_DEVICE));
                } else {
                    bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(ClassicAttributes.UUID_INSECURE_DEVICE));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {

            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                if (!bluetoothSocket.isConnected()) {
                    bluetoothSocket.connect();
                }
                mHandler.obtainMessage(CONNECT, BluetoothProfile.STATE_CONNECTED, -1, null).sendToTarget();
            } catch (IOException ignore) {
                try {
                    Method m = bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                    bluetoothSocket = (BluetoothSocket) m.invoke(bluetoothDevice, 1);
                    bluetoothSocket.connect();
                    mHandler.obtainMessage(CONNECT, BluetoothProfile.STATE_CONNECTED, -1, null).sendToTarget();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    // Close the socket
                    try {
                        bluetoothSocket.close();
                    } catch (IOException ignore2) {
                    }
                    mHandler.obtainMessage(CONNECT, BluetoothProfile.STATE_DISCONNECTED, -1, null).sendToTarget();
                    return;
                }

            }
            // Reset the ConnectThread because we're done
            synchronized (BluetoothClassicService.this) {
                connectThread = null;
            }
            if (connectedThread != null) {
                connectedThread.cancel();
                connectedThread = null;
            }
            connectedThread = new ConnectedThread(bluetoothSocket);
            connectedThread.start();
        }

        public void cancel() {
            try {
                if (bluetoothSocket != null)
                    bluetoothSocket.close();
            } catch (IOException ignore) {
            }
            mHandler.obtainMessage(CONNECT, BluetoothProfile.STATE_DISCONNECTED, -1, null).sendToTarget();
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSockt;
        private InputStream mmInStream;
        private OutputStream mmOutStream;

        @SuppressLint("MissingPermission")
        ConnectedThread(BluetoothSocket socket) {
            this.mmSockt = socket;

            // Get the BluetoothSocket input and output streams
            try {
                mmInStream = mmSockt.getInputStream();
                mmOutStream = mmSockt.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void run() {
//            byte[] buffer = new byte[1024];
//            int bytes;

            // Keep listening to the InputStream while connected
//            while (mmSockt != null && mmSockt.isConnected()) {
//                try {
//                    // Read from the InputStream
//                    bytes = mmInStream.read(buffer);
//
//                    // Send the obtained bytes to the UI Activity
//                    mHandler.obtainMessage(READ, STATE_READ_COMPLETED, bytes, buffer).sendToTarget();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    mHandler.obtainMessage(CONNECT, BluetoothProfile.STATE_DISCONNECTING, -1, null).sendToTarget();
//                    cancel();
//                    break;
//                }
//            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                if (mmOutStream != null)
                    mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.postDelayed(() -> mHandler.obtainMessage(WRITE, STATE_WRITE_COMPLETED, -1, buffer).sendToTarget(), 2000);
            } catch (IOException e) {
                e.printStackTrace();
                mHandler.obtainMessage(CONNECT, BluetoothProfile.STATE_DISCONNECTING, -1, null).sendToTarget();
                // 延时处理断开连接
                mHandler.postDelayed(() -> mHandler.obtainMessage(CONNECT, BluetoothProfile.STATE_DISCONNECTED, -1, null).sendToTarget(), 1000);
            }
        }

        public void cancel() {
            try {
                if (mmInStream != null)
                    mmInStream.close();
            } catch (IOException ignore) {
            }
            try {
                if (mmOutStream != null)
                    mmOutStream.close();
            } catch (IOException ignore) {
            }
            try {
                if (mmSockt != null)
                    mmSockt.close();
            } catch (IOException ignore) {
            }
            mHandler.obtainMessage(CONNECT, BluetoothProfile.STATE_DISCONNECTED, -1, null).sendToTarget();
        }
    }
}
