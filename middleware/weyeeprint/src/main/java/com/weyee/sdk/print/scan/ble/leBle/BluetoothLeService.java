package com.weyee.sdk.print.scan.ble.leBle;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.blankj.utilcode.util.LogUtils;
import com.weyee.sdk.print.scan.ble.IBluetoothAble;
import com.weyee.sdk.print.scan.ble.IBluetoothListener;
import com.weyee.sdk.util.Tools;

import java.util.*;

/**
 * 低功耗蓝牙连接
 *
 * @author wuqi by 2019-06-16.
 */
public class BluetoothLeService extends Service implements IBluetoothAble {
    private final IBinder mBinder = new LocalBinder();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private List<BluetoothDevice> mScanLeDeviceList = new ArrayList<>();
    private boolean isScanning;

    private IBluetoothListener bluetoothLeListener;
    private BluetoothReceiver bluetoothReceiver;


    @Override
    public boolean initialize() {
        //For API level 18 and above, get a reference to BluetoothAdapter through BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }

        bluetoothReceiver = new BluetoothReceiver();
        IntentFilter btFilter = new IntentFilter();
        btFilter.addAction(BluetoothDevice.ACTION_FOUND);
        btFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        btFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        btFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        Tools.getApp().registerReceiver(bluetoothReceiver, btFilter);

        return true;
    }

    @Override
    public boolean isSupportBle() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
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
        this.bluetoothLeListener = bluetoothLeListener;
    }

    @Override
    public void startDiscovery() {
        startDiscovery(10000); // 10秒超时
    }

    @SuppressLint("MissingPermission")
    @Override
    public void startDiscovery(long scanPeriod) {
        if (isEnableBluetooth()) {
            if (isScanning()) return;
            //Stop scanning after a predefined scan period.
            new Handler().postDelayed(() -> {
                changeScanState(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
                } else {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, scanPeriod);
            mScanLeDeviceList.clear();
            changeScanState(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothAdapter.getBluetoothLeScanner().startScan(mScanCallback);
            } else {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        } else {
            cancelDiscovery();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void cancelDiscovery() {
        if (mBluetoothAdapter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mBluetoothAdapter.getBluetoothLeScanner() != null) {
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
            } else {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
        mScanLeDeviceList.clear();
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
        if (bluetoothLeListener != null) {
            bluetoothLeListener.onScanStateChange(isScanning);
        }
    }

    @Override
    public void destroy() {
        cancelDiscovery();
        if (bluetoothReceiver != null) {
            Tools.getApp().unregisterReceiver(bluetoothReceiver);
            bluetoothReceiver = null;
        }
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
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        destroy();
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    /**
     * Device scan callback.
     * <p>
     * Use mScanCallback if Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP,
     * else use mLeScanCallback.
     */
    private ScanCallback mScanCallback;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;

    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mScanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    if (mScanLeDeviceList.contains(result.getDevice())) return;
                    mScanLeDeviceList.add(result.getDevice());
                    if (bluetoothLeListener != null) {
                        bluetoothLeListener.onLeScan(result.getDevice(), result.getRssi(), Objects.requireNonNull(result.getScanRecord()).getBytes());
                    }
                }
            };
        } else {
            mLeScanCallback = (device, rssi, scanRecord) -> {
                if (device == null || mScanLeDeviceList.contains(device)) return;
                mScanLeDeviceList.add(device);
                if (bluetoothLeListener != null) {
                    bluetoothLeListener.onLeScan(device, rssi, scanRecord);
                }
            };
        }
    }

    class BluetoothReceiver extends BroadcastReceiver {

        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (action) {
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
                            if (bluetoothLeListener != null)
                                bluetoothLeListener.onBondStateChange(device, BluetoothDevice.BOND_BONDING);
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            LogUtils.d("完成配对");
                            if (bluetoothLeListener != null)
                                bluetoothLeListener.onBondStateChange(device, BluetoothDevice.BOND_BONDED);
                            break;
                        case BluetoothDevice.BOND_NONE:
                            //取消配对/未配对
                            LogUtils.d("取消配对");
                            if (bluetoothLeListener != null)
                                bluetoothLeListener.onBondStateChange(device, BluetoothDevice.BOND_NONE);
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
