package com.weyee.sdk.print.scan.ble.classicBle;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import com.weyee.sdk.print.scan.ble.IBluetoothAble;
import com.weyee.sdk.print.scan.ble.IBluetoothListener;
import com.weyee.sdk.util.Tools;

import java.util.*;

/**
 * @author wuqi by 2019-06-19.
 */
public class BluetoothClassicService extends Service implements IBluetoothAble {
    private final IBinder mBinder = new BluetoothClassicService.LocalBinder();

    private BluetoothAdapter mBluetoothAdapter;

    private List<BluetoothDevice> mScanLeDeviceList = new ArrayList<>();
    private boolean isScanning;

    private IBluetoothListener bluetoothClassicListener;
    private BluetoothReceiver bluetoothReceiver;


    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SCAN) {
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
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
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

    @Override
    public void destroy() {
        cancelDiscovery();
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
}
