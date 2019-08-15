package com.weyee.sdk.print.scan.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.blankj.utilcode.util.ServiceUtils;
import com.weyee.sdk.print.constant.Brand;
import com.weyee.sdk.print.constant.PaperSize;
import com.weyee.sdk.print.scan.ble.classicBle.BluetoothClassicService;
import com.weyee.sdk.print.scan.ble.leBle.BluetoothLeService;
import com.weyee.sdk.toast.ToastUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 蓝牙工具类
 *
 * @author wuqi by 2019-06-18.
 */
public class BluetoothUtils {
    private IBluetoothAble iBluetoothAble;
    private int currentPaperSize;
    private IBluetoothListener listener;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof BluetoothLeService.LocalBinder) {
                iBluetoothAble = ((BluetoothLeService.LocalBinder) service).getService();
            } else if (service instanceof BluetoothClassicService.LocalBinder) {
                iBluetoothAble = ((BluetoothClassicService.LocalBinder) service).getService();
            }
            if (iBluetoothAble == null) return;
            if (iBluetoothAble.initialize()) {
                if (iBluetoothAble.enableBluetooth(true)) {
                    callback();
                    iBluetoothAble.startDiscovery();
                }
            } else {
                ToastUtils.show("not support Bluetooth");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iBluetoothAble = null;
        }
    };

    public BluetoothUtils(int currentPaperSize, IBluetoothListener listener) {
        this.currentPaperSize = currentPaperSize;
        this.listener = listener;
    }

    /**
     * 绑定服务
     */
    public void bindService() {
        ServiceUtils.bindService(BluetoothClassicService.class, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 解绑服务
     */
    public void unBindService() {
        try {
            ServiceUtils.unbindService(serviceConnection);
        } catch (IllegalArgumentException ignore) {
        }
    }

    /**
     * 开始搜索设备
     */
    public void startDiscovery() {
        if (iBluetoothAble != null) {
            iBluetoothAble.startDiscovery();
        }
    }

    /**
     * 结束搜索设备
     */
    public void cancelDiscovery() {
        if (iBluetoothAble != null) {
            iBluetoothAble.cancelDiscovery();
        }
    }

    /**
     * 蓝牙取消配对
     *
     * @param device
     */
    public void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception ignore) {

        }
    }

    /**
     * 获取过滤后的已配对的设备列表`
     *
     * @return
     */
    public Set<BluetoothDevice> getBondedDevices() {
        if (iBluetoothAble != null) {
            Set<BluetoothDevice> deviceSet = new HashSet<>(iBluetoothAble.getBondedDevices());
            if (!deviceSet.isEmpty()) {
                Iterator<BluetoothDevice> iterator = deviceSet.iterator();
                while (iterator.hasNext()) {
                    if (!filterDevice(iterator.next())) {
                        iterator.remove();
                    }
                }
            }

            return deviceSet;
        }
        return Collections.emptySet();
    }

    public boolean isScanning() {
        if (iBluetoothAble != null) return iBluetoothAble.isScanning();
        return false;
    }

    private void callback() {
        if (iBluetoothAble != null) {
            iBluetoothAble.setIBluetoothListener(new IBluetoothListener() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (listener != null && filterDevice(device) && filterDeviceByState(device)) {
                        listener.onLeScan(device, rssi, scanRecord);
                    }
                }

                @Override
                public void onClassicScan(BluetoothDevice device) {
                    if (listener != null && filterDevice(device) && filterDeviceByState(device)) {
                        listener.onClassicScan(device);
                    }
                }

                @Override
                public void onScanStateChange(boolean isScanning) {
                    if (listener != null) listener.onScanStateChange(isScanning);
                }

                @Override
                public void onBondStateChange(BluetoothDevice device, int newState) {
                    if (listener != null) listener.onBondStateChange(device, newState);
                }

                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    if (listener != null) listener.onConnectionStateChange(gatt, status, newState);
                }

                @Override
                public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                    if (listener != null) listener.onConnectionStateChange(device, status, newState);
                }
            });
        }
    }

    /**
     * 过滤蓝牙设备，根据选择的打印机尺寸过滤规则
     */
    private boolean filterDevice(@NonNull BluetoothDevice device) {
        @SuppressLint("MissingPermission") String deviceName = device.getName();
        if (TextUtils.isEmpty(deviceName)) return false;
        switch (currentPaperSize) {
            case PaperSize.PAPER_SIZE_58:
                return false;
            case PaperSize.PAPER_SIZE_80:
                return deviceName.toUpperCase().contains(Brand.QS_MODEL.toUpperCase()) || deviceName.toUpperCase().contains(Brand.GPRINTER_MODEL.toUpperCase());
            case PaperSize.PAPER_SIZE_110:
                return deviceName.toUpperCase().contains(Brand.ZONERICH_AB_341M_MODEL.toUpperCase()) || deviceName.toUpperCase().contains(Brand.CS4_MODEL.toUpperCase());
            case PaperSize.PAPER_SIZE_150:
                return deviceName.toUpperCase().contains(Brand.JOLIMARK_MODEL.toUpperCase())
                        || deviceName.toUpperCase().contains(Brand.JOLIMARK_LQ_200KIII_MODEL.toUpperCase())
                        || deviceName.toUpperCase().contains(Brand.JOLIMARK_LQ_200KIII_MODEL_OTHER.toUpperCase())
                        || deviceName.toUpperCase().contains(Brand.JOLIMARK_LQ_200KIII_MODEL_NEW.toUpperCase());
            case PaperSize.PAPER_SIZE_210:
                return deviceName.toUpperCase().contains(Brand.EPSON_MODEL.toUpperCase()) || deviceName.toUpperCase().contains(Brand.EPSON_MODEL_NEW.toUpperCase()) || deviceName.toUpperCase().startsWith(Brand.DS_MODEL.toUpperCase());
            default:
                return deviceName.toUpperCase().contains(Brand.QS_MODEL.toUpperCase()) || deviceName.toUpperCase().contains(Brand.GPRINTER_MODEL.toUpperCase())
                        || deviceName.toUpperCase().contains(Brand.JOLIMARK_MODEL.toUpperCase())
                        || deviceName.toUpperCase().contains(Brand.JOLIMARK_LQ_200KIII_MODEL.toUpperCase())
                        || deviceName.toUpperCase().contains(Brand.JOLIMARK_LQ_200KIII_MODEL_OTHER.toUpperCase())
                        || deviceName.toUpperCase().contains(Brand.JOLIMARK_LQ_200KIII_MODEL_NEW.toUpperCase())
                        || deviceName.toUpperCase().contains(Brand.ZONERICH_AB_341M_MODEL.toUpperCase()) || deviceName.toUpperCase().contains(Brand.CS4_MODEL.toUpperCase())
                        || deviceName.toUpperCase().contains(Brand.EPSON_MODEL.toUpperCase()) || deviceName.toUpperCase().contains(Brand.EPSON_MODEL_NEW.toUpperCase()) || deviceName.toUpperCase().startsWith(Brand.DS_MODEL.toUpperCase());
        }
    }

    @SuppressLint("MissingPermission")
    private boolean filterDeviceByState(@NonNull BluetoothDevice device) {
        return device.getBondState() != BluetoothDevice.BOND_BONDED;
    }
}
