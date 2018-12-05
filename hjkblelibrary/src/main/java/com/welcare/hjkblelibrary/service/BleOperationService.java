package com.welcare.hjkblelibrary.service;

import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.vise.baseble.ViseBle;
import com.vise.baseble.callback.IBleCallback;
import com.vise.baseble.callback.IConnectCallback;
import com.vise.baseble.callback.scan.IScanCallback;
import com.vise.baseble.callback.scan.ListFilterScanCallback;
import com.vise.baseble.callback.scan.ScanCallback;
import com.vise.baseble.callback.scan.UuidFilterScanCallback;
import com.vise.baseble.model.BluetoothLeDevice;
import com.vise.baseble.model.BluetoothLeDeviceStore;
import com.welcare.hjkblelibrary.bleinterface.BleOperation;
import com.welcare.hjkblelibrary.bleinterface.BraceletDataCallback;
import com.welcare.hjkblelibrary.bleoperation.BaseBleOperation;
import com.welcare.hjkblelibrary.bleoperation.DZ800BleOperation;
import com.welcare.hjkblelibrary.bleoperation.QS80X4Y2BleOperation;
import com.welcare.hjkblelibrary.entity.DeviceType;
import com.welcare.hjkblelibrary.bleoperation.OtherBleOperation;
import com.welcare.hjkblelibrary.utile.HandlerThreadManager;
import com.welcare.hjkblelibrary.utile.LogUtile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by admin on 2018/11/26.
 * 蓝牙操作服务
 */

public class BleOperationService extends Service {
    private static final String TAG = "BleOperationService";
    private static final int MSG_WHAT_SCAN_ALL = 1001;//扫描所有设备
    private static final int MSG_WHAT_SCAN_NAME = 1002;//扫描指定名称设备
    private static final int MSG_WHAT_SCAN_UUID = 1003;//扫描指定UUID Service 设备
    private static final int MSG_WHAT_SCAN_STOP = 1004;//停止扫描

    private static final int MSG_WHAT_BACK_SCAN_DEVICE = 2001;//发现的设备
    private static final int MSG_WHAT_BACK_SCAN_FINISH = 2002;//完成扫描
    private static final int MSG_WHAT_BACK_SCAN_TIMEOUT = 2003;//扫描超时


    private MyBinder myBinder = new MyBinder();//服务回调
    private int scanType = -1;//扫描类型
    private HashMap<String, BleOperation> devices = new HashMap<>();//连接的设备列表
    private ArrayList<String> scanDeviceName = new ArrayList<>();//扫描的设备名称
    private ArrayList<IScanCallback> mIScanCallbacks = new ArrayList<>();//扫描的回调;
    private UUID scanUUID = null;//扫描的服务UUID
    private ScanCallback mScanCallback = null;//扫描所有设备回调监听
    private ListFilterScanCallback mListFilterScanCallback = null;//扫描指定设备回调监听
    private UuidFilterScanCallback mUUIDFilterScanCallback = null;//扫描指定UUID设备回调监听

    public static final String BLE_OPERATION_THREAD_NAME = "BleOperation";//子线程名称
    private Handler mHandler = new Handler(HandlerThreadManager.getInstance().getHanderThread(BLE_OPERATION_THREAD_NAME).getLooper())//子线程
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_WHAT_SCAN_ALL://扫描所有设备
                    scanType = msg.what;
                    ViseBle.getInstance().startScan(mScanCallback);
                    break;
                case MSG_WHAT_SCAN_NAME://扫描指定名称设备
                    scanType = msg.what;
                    ViseBle.getInstance().startScan(mListFilterScanCallback);
                    break;
                case MSG_WHAT_SCAN_UUID://扫描指定UUID Service 设备
                    scanType = msg.what;
                    ViseBle.getInstance().startScan(mUUIDFilterScanCallback);
                    break;
                case MSG_WHAT_SCAN_STOP://停止扫描
                    switch (scanType) {
                        case MSG_WHAT_SCAN_ALL://停止扫描所有设备
                            ViseBle.getInstance().stopScan(mScanCallback);
                            break;
                        case MSG_WHAT_SCAN_NAME://停止扫描指定名称设备
                            ViseBle.getInstance().stopScan(mListFilterScanCallback);
                            break;
                        case MSG_WHAT_SCAN_UUID://停止扫描指定UUID Service 设备
                            ViseBle.getInstance().stopScan(mUUIDFilterScanCallback);
                            break;
                    }
                    scanType = -1;
                    break;

                case MSG_WHAT_BACK_SCAN_DEVICE://发现的设备
                    if (msg.obj instanceof BluetoothLeDevice) {
                        BluetoothLeDevice bluetoothLeDevice = (BluetoothLeDevice) msg.obj;
                        setLog("扫描到设备:name:" + bluetoothLeDevice.getName() + "\tmac:" + bluetoothLeDevice.getAddress());
                        notifyIScanCallback(bluetoothLeDevice, null, null);
                    }
                    break;
                case MSG_WHAT_BACK_SCAN_FINISH://完成扫描
                    if (msg.obj instanceof BluetoothLeDeviceStore) {
                        BluetoothLeDeviceStore bluetoothLeDeviceStore = (BluetoothLeDeviceStore) msg.obj;
                        setLog("扫描设备完成:" + bluetoothLeDeviceStore.toString());
                        notifyIScanCallback(null, bluetoothLeDeviceStore, null);
                    }

                    break;
                case MSG_WHAT_BACK_SCAN_TIMEOUT://扫描超时
                    setLog("扫描超时");
                    notifyIScanCallback(null, null, "timeOut");
                    break;

            }
        }
    };
    private IScanCallback mIScanCallback = new IScanCallback() {//扫描回调监听
        @Override
        public void onDeviceFound(BluetoothLeDevice bluetoothLeDevice) {
            mHandler.sendMessage(Message.obtain(mHandler, MSG_WHAT_BACK_SCAN_DEVICE, bluetoothLeDevice));

        }

        @Override
        public void onScanFinish(BluetoothLeDeviceStore bluetoothLeDeviceStore) {
            mHandler.sendMessage(Message.obtain(mHandler, MSG_WHAT_BACK_SCAN_FINISH, bluetoothLeDeviceStore));
        }

        @Override
        public void onScanTimeout() {
            mHandler.sendMessage(Message.obtain(mHandler, MSG_WHAT_BACK_SCAN_TIMEOUT));
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThreadManager.getInstance().createHandler(BLE_OPERATION_THREAD_NAME);
//        scanDeviceName.add("YunChen");
        scanDeviceName.add("Y2");
        scanDeviceName.add("X4");
//        scanDeviceName.add("LiKing Fit");
        scanDeviceName.add("QS80");
        scanDeviceName.add("DZ800");
        scanDeviceName.add("DZ900");
        scanDeviceName.add("PPlusOTA");
        mScanCallback = new ScanCallback(mIScanCallback);//扫描所有设备回调监听
        mListFilterScanCallback = new ListFilterScanCallback(mIScanCallback);//扫描指定设备回调监听
        mListFilterScanCallback.setDeviceNameList(scanDeviceName);
        mUUIDFilterScanCallback = new UuidFilterScanCallback(mIScanCallback);//扫描指定UUID设备回调监听

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return myBinder;
    }


    /**
     * 获取蓝牙操作对象
     *
     * @param deviceType 设备类型
     * @return
     */
    private BleOperation getBleOperation(DeviceType deviceType) {
        BleOperation bleOperation = null;
        switch (deviceType) {
            case DZ800:
                bleOperation = new DZ800BleOperation();
                break;
            case X4:
            case Y2:
            case QS80:
                bleOperation = new QS80X4Y2BleOperation();
                break;
        }

        return bleOperation;
    }

    ;

    /**
     * 通知各个监听扫描数据回调的对象
     *
     * @param bluetoothLeDevice      扫描到的设备
     * @param bluetoothLeDeviceStore 扫描完成的设备集合
     * @param timeout                //扫描超时
     */
    private void notifyIScanCallback(BluetoothLeDevice bluetoothLeDevice, BluetoothLeDeviceStore bluetoothLeDeviceStore, String timeout) {

        for (IScanCallback iScanCallback : mIScanCallbacks) {
            if (bluetoothLeDevice != null) {
                iScanCallback.onDeviceFound(bluetoothLeDevice);
            }
            if (bluetoothLeDeviceStore != null) {
                iScanCallback.onScanFinish(bluetoothLeDeviceStore);
            }
            if (timeout != null) {
                iScanCallback.onScanTimeout();
            }

        }
    }

    /**
     * 扫描所有设备
     */
    public void scanALL() {
        mHandler.sendEmptyMessage(MSG_WHAT_SCAN_ALL);
    }

    /**
     * 扫描指定名称设备
     */
    public void scanName() {
        mHandler.sendEmptyMessage(MSG_WHAT_SCAN_NAME);
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        mHandler.sendEmptyMessage(MSG_WHAT_SCAN_STOP);
    }

    /**
     * 扫描指定UUID设备
     *
     * @param uuid 扫描的UUID
     */
    public void scanUUID(String uuid) {
        scanUUID(UUID.fromString(uuid));
    }

    /**
     * 扫描指定UUID设备
     *
     * @param scanUUID 扫描的UUID
     */
    public void scanUUID(UUID scanUUID) {
        this.scanUUID = scanUUID;
        mUUIDFilterScanCallback.setUuid(scanUUID);
        mHandler.sendEmptyMessage(MSG_WHAT_SCAN_UUID);
    }

    /**
     * 添加过滤的扫描名称
     *
     * @param name
     */
    public void addScanName(String name) {
        scanDeviceName.add(name);

    }

    /**
     * 删除添加的扫描名称
     */
    public void removeScanName() {
        if (scanDeviceName.size() > 6) {
            for (int i = 6; i < scanDeviceName.size(); ) {
                scanDeviceName.remove(i);
            }
        }

    }

    /**
     * 连接已知的设备进行通讯
     *
     * @param deviceType       设备类型 调用 DeviceType.getType(bleName);获取
     * @param mac              设备物理地址
     * @param iConnectCallback 连接监听
     * @param iBleCallback     数据回调监听
     * @return
     */
    public boolean connect(DeviceType deviceType, String mac, IConnectCallback iConnectCallback, BraceletDataCallback iBleCallback) {
        if (devices.containsKey(mac) && isConnect(mac)) {
            setLog("此设备未断开");
            return false;
        }
        BleOperation bleOperation = getBleOperation(deviceType);
        if (bleOperation == null) {
            setLog("此设备不在已知类型里");
            return false;
        }
        devices.put(mac, bleOperation);
        bleOperation.setOnConnectCallback(iConnectCallback).setOnNotifyAndReadCallback(iBleCallback);
        return bleOperation.connect(mac);
    }

    /**
     * 连接未知的其它的设备
     *
     * @param mac              设备物理地址
     * @param iConnectCallback 连接监听
     * @param iBleCallback     数据回调监听
     * @param serviceUUID      主通讯服务通道
     * @return
     */
    public BleOperation connect(String mac, IConnectCallback iConnectCallback, BraceletDataCallback iBleCallback, String serviceUUID) {
        if (devices.containsKey(mac) && isConnect(mac)) {
            setLog("此设备未断开");
            return devices.get(mac);
        }

        BleOperation bleOperation = new OtherBleOperation();
        devices.put(mac, bleOperation);
        bleOperation.connect(mac);
        bleOperation.setOnConnectCallback(iConnectCallback)
                .setOnNotifyAndReadCallback(iBleCallback)
                .setServiceUUID(serviceUUID);
        return bleOperation;
    }


    /**
     * 连接已知设备进行通讯
     *
     * @param deviceType        设备类型 调用 DeviceType.getType(bleName);获取
     * @param bluetoothLeDevice 需要连接的设备
     * @param iConnectCallback  连接监听
     * @param iBleCallback      数据回调监听
     * @return
     */
    public boolean connect(DeviceType deviceType, BluetoothLeDevice bluetoothLeDevice, IConnectCallback iConnectCallback, BraceletDataCallback iBleCallback) {
        if (devices.containsKey(bluetoothLeDevice.getAddress()) && isConnect(bluetoothLeDevice.getAddress())) {
            setLog("此设备未断开");
            return false;
        }
        BleOperation bleOperation = getBleOperation(deviceType);
        if (bleOperation == null) {
            setLog("此设备不在本公司的列表里");
            return false;
        }
        devices.put(bluetoothLeDevice.getAddress(), bleOperation);
        bleOperation.setOnConnectCallback(iConnectCallback).setOnNotifyAndReadCallback(iBleCallback);
        return bleOperation.connect(bluetoothLeDevice);
    }

    /**
     * 连接未知的其它设备进行通讯
     *
     * @param bluetoothLeDevice 需要连接的设备
     * @param iConnectCallback  连接监听
     * @param iBleCallback      数据回调监听
     * @param serviceUUID       主通讯UUID
     * @return
     */
    public BleOperation connect(BluetoothLeDevice bluetoothLeDevice, IConnectCallback iConnectCallback, BraceletDataCallback iBleCallback, String serviceUUID) {
        if (devices.containsKey(bluetoothLeDevice.getAddress()) && isConnect(bluetoothLeDevice.getAddress())) {
            setLog("此设备未断开");
            return devices.get(bluetoothLeDevice.getAddress());
        }
        BleOperation bleOperation = new OtherBleOperation();
        devices.put(bluetoothLeDevice.getAddress(), bleOperation);
        bleOperation.setServiceUUID(serviceUUID).setOnConnectCallback(iConnectCallback).setOnNotifyAndReadCallback(iBleCallback);
        bleOperation.connect(bluetoothLeDevice);
        return bleOperation;
    }

    /**
     * 断开连接
     *
     * @param mac 连接的设备的物理地址
     * @return
     */
    public boolean disConnect(String mac) {
        if (!devices.containsKey(mac)) {
            setLog("不存在此设备");
            return false;
        }
        devices.get(mac).disConnect();
        devices.get(mac).close();
        return true;
    }

    /**
     * 再次连接
     *
     * @param bluetoothLeDevice
     * @return
     */
    public boolean againConnect(BluetoothLeDevice bluetoothLeDevice) {

        return againConnect(bluetoothLeDevice.getAddress());
    }

    /**
     * 再次连接
     *
     * @param mac
     * @return
     */
    public boolean againConnect(String mac) {
        if (!devices.containsKey(mac)) {
            setLog("不存在此设备");
            return false;
        }
        devices.get(mac).againConnect();
        return true;
    }

    /**
     * 断开连接
     *
     * @param bluetoothLeDevice 连接的设备
     * @return
     */
    public boolean disConnect(BluetoothLeDevice bluetoothLeDevice) {

        return disConnect(bluetoothLeDevice.getAddress());
    }

    /**
     * 发送数据给设备
     *
     * @param mac  连接过的设备物理地址
     * @param data 发送的数据
     * @return
     */
    public boolean sendData(String mac, byte[] data) {
        if (!devices.containsKey(mac)) {
            setLog("不存在此设备");
            return false;
        }

        return devices.get(mac).sendData(data);
    }

    /**
     * 发送数据给设备
     *
     * @param mac  连接过的设备物理地址
     * @param data 发送的数据
     * @return
     */
    public boolean sendData(String mac, String data) {
        if (!devices.containsKey(mac)) {
            setLog("不存在此设备");
            return false;
        }

        return devices.get(mac).sendData(data);
    }

    /**
     * 发送数据给设备
     *
     * @param bluetoothLeDevice 连接的设备
     * @param data              发送的数据
     * @return
     */
    public boolean sendData(BluetoothLeDevice bluetoothLeDevice, byte[] data) {
        return sendData(bluetoothLeDevice.getAddress(), data);
    }

    /**
     * 请求设备回传数据
     *
     * @param mac 连接过的设备物理地址
     * @return
     */
    public boolean readData(String mac) {
        if (!devices.containsKey(mac)) {
            setLog("不存在此设备");
            return false;
        }

        return devices.get(mac).readData();
    }

    /**
     * 请求设备回传数据
     *
     * @param bluetoothLeDevice 连接的设备
     * @return
     */
    public boolean readData(BluetoothLeDevice bluetoothLeDevice) {
        return devices.get(bluetoothLeDevice.getAddress()).readData();
    }

    /**
     * 设置主服务UUID
     *
     * @param mac         连接过的设备物理地址
     * @param serviceUUID 主通讯服务UUID
     */
    public void setServiceUUID(String mac, String serviceUUID) {
        if (!devices.containsKey(mac)) {
            setLog("不存在此设备");
            return;
        }
        devices.get(mac).setServiceUUID(serviceUUID);

    }

    /**
     * 设置主服务UUID
     *
     * @param bluetoothLeDevice 连接的设备
     * @param serviceUUID       主通讯服务UUID
     */
    public void setServiceUUID(BluetoothLeDevice bluetoothLeDevice, String serviceUUID) {
        setServiceUUID(bluetoothLeDevice.getAddress(), serviceUUID);
    }

    /**
     * 设置连接监听
     *
     * @param mac              连接过的设备物理地址
     * @param iConnectCallback 连接状态回调
     */
    public void setOnConnectCallback(String mac, IConnectCallback iConnectCallback) {
        if (!devices.containsKey(mac)) {
            setLog("不存在此设备");
            return;
        }
        devices.get(mac).setOnConnectCallback(iConnectCallback);
    }

    /**
     * 设置连接监听
     *
     * @param bluetoothLeDevice 连接的设备
     * @param iConnectCallback  连接状态回调
     */
    public void setOnConnectCallback(BluetoothLeDevice bluetoothLeDevice, IConnectCallback iConnectCallback) {
        setOnConnectCallback(bluetoothLeDevice.getAddress(), iConnectCallback);
    }

    /**
     * 设置数据监听回调
     *
     * @param mac          连接过的设备物理地址
     * @param iBleCallback 监听数据回传回调
     */
    public void setOnNotifyAndReadCallback(String mac, IBleCallback iBleCallback) {
        if (!devices.containsKey(mac)) {
            setLog("不存在此设备");
            return;
        }
        devices.get(mac).setOnNotifyAndReadCallback(iBleCallback);
    }

    /**
     * 设置数据监听回调
     *
     * @param bluetoothLeDevice 连接的设备
     * @param iBleCallback      监听数据回传回调
     */
    public void setOnNotifyAndReadCallback(BluetoothLeDevice bluetoothLeDevice, IBleCallback iBleCallback) {
        setOnNotifyAndReadCallback(bluetoothLeDevice.getAddress(), iBleCallback);
    }

    /**
     * 设置是否开启重连
     *
     * @param mac    连接过的设备物理地址
     * @param isOpen 是否开启重连
     */
    public void setReconnect(String mac, boolean isOpen) {
        if (!devices.containsKey(mac)) {
            setLog("不存在此设备");
            return;
        }
        devices.get(mac).setReconnect(isOpen);
    }

    /**
     * 设置是否开启重连
     *
     * @param bluetoothLeDevice 连接的设备
     * @param isOpen            是否开启重连
     */
    public void setReconnect(BluetoothLeDevice bluetoothLeDevice, boolean isOpen) {
        setReconnect(bluetoothLeDevice.getAddress(), isOpen);
    }

    /**
     * 设置重连最大次数
     *
     * @param mac   连接过的设备物理地址
     * @param count 重连最大次数
     */
    public void setReconnectMaxCount(String mac, int count) {
        if (!devices.containsKey(mac)) {
            setLog("不存在此设备");
            return;
        }
        devices.get(mac).setReconnectMaxCount(count);
    }

    /**
     * 设置重连最大次数
     *
     * @param bluetoothLeDevice 连接的设备
     * @param count             重连最大次数
     */
    public void setReconnectMaxCount(BluetoothLeDevice bluetoothLeDevice, int count) {
        setReconnectMaxCount(bluetoothLeDevice.getAddress(), count);
    }

    /**
     * 判断是否连接
     *
     * @param mac 连接过的设备物理地址
     * @return
     */
    public boolean isConnect(String mac) {
        if (!devices.containsKey(mac)) {
            setLog("不存在此设备");
            return false;
        }

        return devices.get(mac).isConnect();
    }

    /**
     * 判断是否连接
     *
     * @param bluetoothLeDevice 连接的设备
     * @return
     */
    public boolean isConnect(BluetoothLeDevice bluetoothLeDevice) {

        return isConnect(bluetoothLeDevice.getAddress());
    }

    /**
     * 添加扫描监听
     *
     * @param iScanCallback
     */
    public void addIScanCallback(IScanCallback iScanCallback) {
        if (mIScanCallbacks.contains(iScanCallback)) {
            setLog("已存在此对象");
            return;
        }
        mIScanCallbacks.add(iScanCallback);
    }


    /**
     * 移除扫描监听
     *
     * @param iScanCallback
     */
    public void removeIScanCallback(IScanCallback iScanCallback) {
        mIScanCallbacks.remove(iScanCallback);
    }


    public class MyBinder extends Binder {
        public BleOperationService getService() {
            return BleOperationService.this;
        }
    }

    /**
     * 打印日志
     *
     * @param msg
     */
    public void setLog(String msg) {
        LogUtile.i(TAG, msg);
    }

    /**
     * 获取当前读取数据的通道
     *
     * @return
     */
    public BluetoothGattCharacteristic getReadBluetoothGattCharacteristic(String mac) {
        if (devices.containsKey(mac) && devices.get(mac) instanceof BaseBleOperation) {
            return ((BaseBleOperation) devices.get(mac)).getReadBluetoothGattCharacteristic();
        }

        return null;
    }

    /**
     * 获取当前接收数据的通道
     *
     * @return
     */
    public BluetoothGattCharacteristic getNotifyBluetoothGattCharacteristic(String mac) {
        if (devices.containsKey(mac) && devices.get(mac) instanceof BaseBleOperation) {
            return ((BaseBleOperation) devices.get(mac)).getNotifyBluetoothGattCharacteristic();
        }

        return null;
    }

    /**
     * 获取当前写入数据的通道
     *
     * @return
     */
    public BluetoothGattCharacteristic getWriteBluetoothGattCharacteristic(String mac) {
        if (devices.containsKey(mac) && devices.get(mac) instanceof BaseBleOperation) {
            return ((BaseBleOperation) devices.get(mac)).getWriteBluetoothGattCharacteristic();
        }

        return null;
    }

    /**
     * 获取当前使用主服务UUID通道
     *
     * @return
     */
    public UUID getServiceUUID(String mac) {
        if (devices.containsKey(mac) && devices.get(mac) instanceof BaseBleOperation) {
            return ((BaseBleOperation) devices.get(mac)).getServiceUUID();
        }
        return null;
    }

}
