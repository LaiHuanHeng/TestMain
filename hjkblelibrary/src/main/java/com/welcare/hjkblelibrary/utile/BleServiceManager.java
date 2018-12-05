package com.welcare.hjkblelibrary.utile;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import com.vise.baseble.ViseBle;
import com.vise.baseble.callback.IBleCallback;
import com.vise.baseble.callback.IConnectCallback;
import com.vise.baseble.callback.scan.IScanCallback;
import com.vise.baseble.core.BluetoothGattChannel;
import com.vise.baseble.core.DeviceMirror;
import com.vise.baseble.exception.BleException;
import com.vise.baseble.model.BluetoothLeDevice;
import com.welcare.hjkblelibrary.bleinterface.BleOperation;
import com.welcare.hjkblelibrary.bleinterface.BraceletDataCallback;
import com.welcare.hjkblelibrary.bleoperation.OtherBleOperation;
import com.welcare.hjkblelibrary.entity.BloodPressure;
import com.welcare.hjkblelibrary.entity.DeviceType;
import com.welcare.hjkblelibrary.entity.HeartData;
import com.welcare.hjkblelibrary.entity.OxyData;
import com.welcare.hjkblelibrary.entity.SleepData;
import com.welcare.hjkblelibrary.entity.StepData;
import com.welcare.hjkblelibrary.service.BleOperationService;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by admin on 2018/11/29.
 * 蓝牙服务管理器
 */

public class BleServiceManager {

    private final String TAG = getClass().getName();
    private static BleServiceManager instance = null;//蓝牙管理器
    private Context mContext;
    private BleOperationService mBleOperationService = null;//蓝牙操作服务
    private String mMAC = null;//当前连接MAC地址
    private BluetoothLeDevice mBluetoothLeDevice = null;//当前连接的设备
    private BraceletDataCallback braceletDataCallback;//外放数据监听接口
    private IConnectCallback iConnectCallback;//外放连接监听接口

    private DeviceMirror mDeviceMirror;//设备模型
    private int scanTime;//扫描时间
    private boolean isScan = false;//是否在扫描状态
    private ArrayList<IScanCallback> mIScanCallbacks = new ArrayList<>();
    private IConnectCallback mIConnectCallback = new IConnectCallback() {//当前使用的接口
        @Override
        public void onConnectSuccess(DeviceMirror deviceMirror) {
            mMAC = deviceMirror.getBluetoothLeDevice().getAddress();
            mBluetoothLeDevice = deviceMirror.getBluetoothLeDevice();
            mDeviceMirror = deviceMirror;

            if (iConnectCallback != null) {
                iConnectCallback.onConnectSuccess(deviceMirror);
            }

        }

        @Override
        public void onConnectFailure(BleException exception) {
            if (iConnectCallback != null) {
                iConnectCallback.onConnectFailure(exception);
            }
        }

        @Override
        public void onDisconnect(boolean isActive) {
            if (iConnectCallback != null) {
                iConnectCallback.onDisconnect(isActive);
            }
        }
    };

    private BraceletDataCallback mBraceletDataCallback = new BraceletDataCallback() {//数据回调监听
        @Override
        public void onBindDeviceState(boolean success) {
            if (braceletDataCallback != null) {
                braceletDataCallback.onBindDeviceState(success);
            }
        }

        @Override
        public void onLoginDeviceState(boolean success) {
            if (braceletDataCallback != null) {
                braceletDataCallback.onLoginDeviceState(success);
            }
        }

        @Override
        public void onHeart(HeartData heart) {
            if (braceletDataCallback != null) {
                braceletDataCallback.onHeart(heart);
            }
        }

        @Override
        public void onStep(StepData step) {
            if (braceletDataCallback != null) {
                braceletDataCallback.onStep(step);
            }
        }

        @Override
        public void onOXY(OxyData oxy) {
            if (braceletDataCallback != null) {
                braceletDataCallback.onOXY(oxy);
            }
        }

        @Override
        public void onBloodPressure(BloodPressure bloodPressure) {
            if (braceletDataCallback != null) {
                braceletDataCallback.onBloodPressure(bloodPressure);
            }
        }

        @Override
        public void onSleep(SleepData sleepData) {
            if (braceletDataCallback != null) {
                braceletDataCallback.onSleep(sleepData);
            }
        }

        @Override
        public void onHeartOXYBPHistory(ArrayList<HeartData> hearts, ArrayList<OxyData> oxys, ArrayList<BloodPressure> bloodPressures) {
            if (braceletDataCallback != null) {
                braceletDataCallback.onHeartOXYBPHistory(hearts, oxys, bloodPressures);
            }
        }

        @Override
        public void onStepHistory(ArrayList<StepData> steps) {
            if (braceletDataCallback != null) {
                braceletDataCallback.onStepHistory(steps);
            }
        }

        @Override
        public void onElectricQuantity(int electricQuantity, int state) {
            if (braceletDataCallback != null) {
                braceletDataCallback.onElectricQuantity(electricQuantity, state);
            }
        }

        @Override
        public void onVersion(String version) {
            if (braceletDataCallback != null) {
                braceletDataCallback.onVersion(version);
            }
        }

        @Override
        public void onSuccess(byte[] data, BluetoothGattChannel bluetoothGattChannel, BluetoothLeDevice bluetoothLeDevice) {
            if (braceletDataCallback != null) {
                braceletDataCallback.onSuccess(data, bluetoothGattChannel, bluetoothLeDevice);
            }
        }

        @Override
        public void onFailure(BleException exception) {
            if (braceletDataCallback != null) {
                braceletDataCallback.onFailure(exception);
            }
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {//绑定服务的对象
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                mBleOperationService = ((BleOperationService.MyBinder) service).getService();
                if (mBleOperationService != null) {
                    if (mIScanCallbacks != null) {
                        for (IScanCallback iScanCallback : mIScanCallbacks) {
                            mBleOperationService.addIScanCallback(iScanCallback);
                        }
                        mIScanCallbacks.clear();
                    }
                }
            } catch (Exception e) {
                setLog("绑定服务异常");
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBleOperationService = null;
            setLog("服务关闭");
        }
    };

    private BleServiceManager(Context context) {
        this.mContext = context;
//        init();

    }

    /**
     * 初始化
     */
    public void init() {
        initBleConfig();
        initBleService();
    }

    /**
     * 初始化蓝牙配置参数
     */
    private void initBleConfig() {
        //蓝牙相关配置修改
        ViseBle.config()
                .setScanTimeout(-1)//扫描超时时间，这里设置为永久扫描
                .setConnectTimeout(20 * 1000)//连接超时时间
                .setOperateTimeout(5 * 1000)//设置数据操作超时时间
                .setConnectRetryCount(0)//设置连接失败重试次数
                .setConnectRetryInterval(1000)//设置连接失败重试间隔时间
                .setOperateRetryCount(3)//设置数据操作失败重试次数
                .setOperateRetryInterval(1000)//设置数据操作失败重试间隔时间
                .setMaxConnectCount(1);//设置最大连接设备数量
        //蓝牙信息初始化，全局唯一，必须在应用初始化时调用
        ViseBle.getInstance().init(mContext.getApplicationContext());

    }

    /**
     * 初始化蓝牙服务
     */
    private void initBleService() {
        Intent intent = new Intent(mContext, BleOperationService.class);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 获取蓝牙管理单例
     *
     * @param context
     * @return
     */
    public static BleServiceManager getInstance(Context context) {
        if (instance == null) {
            synchronized (BleServiceManager.class) {
                if (instance == null) {
                    instance = new BleServiceManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * 设置日志
     *
     * @param msg 打印的信息
     */
    private void setLog(String msg) {
        LogUtile.i(TAG, msg);
    }

    /**
     * 蓝牙操作服务是否可用
     *
     * @return
     */
    private boolean isBleOperationAvailable() {
        if (!isHasConnect()) {
            return false;
        }
        if (mMAC == null) {
            setLog("未连接蓝牙");
            return false;
        }

        return true;
    }

    /**
     * 连接前的判断设备
     *
     * @return
     */
    private boolean isHasConnect() {
        if (mBleOperationService == null) {
            setLog("未初蓝牙操作始化服务");
            return false;
        }
        return true;
    }

    /**
     * 蓝牙功能是否可用
     *
     * @return
     */
    private boolean isBleAvailable() {
        return true;
    }

    /**
     * 延时停止扫描
     */
    private void delayedStopScan() {
        if (scanTime <= 0 || !isScan) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        }, scanTime);
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        mBleOperationService.stopScan();
        isScan = false;
    }

    /**
     * 扫描所有设备
     */
    public void scanALL() {
        if (!isHasConnect()) {
            return;
        }
        mBleOperationService.scanALL();
        isScan = true;
        delayedStopScan();

    }

    /**
     * 扫描指定名称设备,内置过滤列表,不在列表上的设备将不显示
     */
    public void scanName() {
        if (!isHasConnect()) {
            return;
        }
        mBleOperationService.scanName();
        isScan = true;
        delayedStopScan();
    }

    /**
     * 扫描指定UUID设备
     *
     * @param uuid 扫描的UUID
     */
    public void scanUUID(String uuid) {
        if (!isHasConnect()) {
            return;
        }

        mBleOperationService.scanUUID(uuid);
        isScan = true;
        delayedStopScan();

    }

    /**
     * 扫描指定UUID设备
     *
     * @param scanUUID 扫描的UUID
     */
    public void scanUUID(UUID scanUUID) {
        if (!isHasConnect()) {
            return;
        }
        mBleOperationService.scanUUID(scanUUID);
        isScan = true;
        delayedStopScan();
    }

    /**
     * 添加过滤的扫描名称
     *
     * @param name
     */
    public void addScanName(String name) {
        if (!isHasConnect()) {
            return;
        }
        mBleOperationService.addScanName(name);
        return;

    }

    /**
     * 删除添加的扫描名称
     */
    public BleServiceManager removeScanName() {
        if (!isHasConnect()) {
            return this;
        }
        mBleOperationService.removeScanName();
        return this;
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
        if (!isHasConnect()) {
            return false;
        }
        this.iConnectCallback = iConnectCallback;
        this.braceletDataCallback = iBleCallback;
        return mBleOperationService.connect(deviceType, mac, mIConnectCallback, mBraceletDataCallback);
    }

    /**
     * 连接已知的设备进行通讯
     *
     * @param deviceType 设备类型 调用 DeviceType.getType(bleName);获取
     * @param mac        设备物理地址
     * @return
     */
    public boolean connect(DeviceType deviceType, String mac) {
        if (!isHasConnect()) {
            return false;
        }
        return mBleOperationService.connect(deviceType, mac, mIConnectCallback, mBraceletDataCallback);
    }


    /**
     * 连接已知设备进行通讯
     *
     * @param bluetoothLeDevice 需要连接的设备
     * @param iConnectCallback  连接监听
     * @param iBleCallback      数据回调监听
     * @return
     */
    public boolean connect(BluetoothLeDevice bluetoothLeDevice, IConnectCallback iConnectCallback, BraceletDataCallback iBleCallback) {
        if (!isHasConnect()) {
            return false;
        }
        this.iConnectCallback = iConnectCallback;
        this.braceletDataCallback = iBleCallback;
        return mBleOperationService.connect(DeviceType.getType(bluetoothLeDevice.getName()), bluetoothLeDevice, mIConnectCallback, mBraceletDataCallback);
    }

    /**
     * 连接已知设备进行通讯
     *
     * @param bluetoothLeDevice 需要连接的设备
     * @return
     */
    public boolean connect(BluetoothLeDevice bluetoothLeDevice) {
        if (!isHasConnect()) {
            return false;
        }
        return mBleOperationService.connect(DeviceType.getType(bluetoothLeDevice.getName()), bluetoothLeDevice, mIConnectCallback, mBraceletDataCallback);
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
        if (!isHasConnect()) {
            return null;
        }
        this.iConnectCallback = iConnectCallback;
        this.braceletDataCallback = iBleCallback;
        return mBleOperationService.connect(mac, mIConnectCallback, mBraceletDataCallback, serviceUUID);
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
        if (!isHasConnect()) {
            return null;
        }
        this.iConnectCallback = iConnectCallback;
        this.braceletDataCallback = iBleCallback;

        return mBleOperationService.connect(bluetoothLeDevice, mIConnectCallback, mBraceletDataCallback, serviceUUID);
    }


    /**
     * 连接未知的其它设备进行通讯
     *
     * @param bluetoothLeDevice 需要连接的设备
     * @param serviceUUID       主通讯UUID
     * @return
     */
    public BleOperation connect(BluetoothLeDevice bluetoothLeDevice, String serviceUUID) {
        if (!isHasConnect()) {
            return null;
        }
        return mBleOperationService.connect(bluetoothLeDevice, mIConnectCallback, mBraceletDataCallback, serviceUUID);
    }

    /**
     * 连接未知的其它设备进行通讯
     *
     * @param mac         需要连接的设备
     * @param serviceUUID 主通讯UUID
     * @return
     */
    public BleOperation connect(String mac, String serviceUUID) {
        if (!isHasConnect()) {
            return null;
        }
        return mBleOperationService.connect(mac, mIConnectCallback, mBraceletDataCallback, serviceUUID);
    }

    /**
     * 再次连接
     */
    public void againConnect() {
        if (!isBleOperationAvailable()) {
            setLog("未连接过设备");
            return;
        }
        mBleOperationService.againConnect(mMAC);
    }

    /**
     * 断开连接
     *
     * @return
     */
    public boolean disConnect() {
        if (!isBleOperationAvailable()) {
            return false;
        }
        return mBleOperationService.disConnect(mMAC);
    }


    /**
     * 发送数据给设备
     *
     * @param data 发送的数据
     * @return
     */
    public boolean sendData(byte[] data) {
        if (!isBleOperationAvailable()) {
            return false;
        }

        return mBleOperationService.sendData(mMAC, data);
    }

    /**
     * 发送数据给设备
     *
     * @param data 发送的数据
     * @return
     */
    public boolean sendData(String data) {
        if (!isBleOperationAvailable()) {
            return false;
        }

        return mBleOperationService.sendData(mMAC, data);
    }

    /**
     * 请求设备回传数据
     *
     * @return
     */
    public boolean readData() {
        if (!isBleOperationAvailable()) {
            return false;
        }

        return mBleOperationService.readData(mMAC);
    }


    /**
     * 设置主服务UUID
     *
     * @param serviceUUID 主通讯服务UUID
     */
    public BleServiceManager setServiceUUID(String serviceUUID) {
        if (!isBleOperationAvailable()) {
            return this;
        }
        mBleOperationService.setServiceUUID(mMAC, serviceUUID);
        return this;

    }


    /**
     * 设置连接监听
     *
     * @param iConnectCallback 连接状态回调
     */
    public BleServiceManager setOnConnectCallback(IConnectCallback iConnectCallback) {
        if (!isBleOperationAvailable()) {
            return this;
        }
        mBleOperationService.setOnConnectCallback(mMAC, iConnectCallback);
        return this;
    }


    /**
     * 设置数据监听回调
     *
     * @param iBleCallback 监听数据回传回调
     */
    public BleServiceManager setOnNotifyAndReadCallback(IBleCallback iBleCallback) {
        if (!isBleOperationAvailable()) {
            return this;
        }
        mBleOperationService.setOnNotifyAndReadCallback(mMAC, iBleCallback);
        return this;
    }

    /**
     * 设置是否开启重连
     *
     * @param isOpen 是否开启重连
     */
    public BleServiceManager setReconnect(boolean isOpen) {
        if (!isBleOperationAvailable()) {
            return this;
        }

        mBleOperationService.setReconnect(mMAC, isOpen);
        return this;
    }


    /**
     * 设置重连最大次数
     *
     * @param count 重连最大次数
     */
    public BleServiceManager setReconnectMaxCount(int count) {
        if (!isBleOperationAvailable()) {
            return this;
        }
        mBleOperationService.setReconnectMaxCount(mMAC, count);
        return this;
    }

    /**
     * 判断是否连接
     *
     * @return
     */
    public boolean isConnect() {
        if (!isBleOperationAvailable()) {
            return false;
        }


        return mBleOperationService.isConnect(mMAC);
    }

    /**
     * 判断是否连接
     *
     * @param bluetoothLeDevice 连接的设备
     * @return
     */
    public boolean isConnect(BluetoothLeDevice bluetoothLeDevice) {
        if (!isBleOperationAvailable()) {
            return false;
        }
        return mBleOperationService.isConnect(mMAC);
    }


    /**
     * 添加扫描监听
     *
     * @param iScanCallback
     */
    public void addIScanCallback(IScanCallback iScanCallback) {
        if (!isHasConnect()) {
            mIScanCallbacks.add(iScanCallback);
            return;
        }
        mBleOperationService.addIScanCallback(iScanCallback);
    }

    /**
     * 移除扫描监听
     *
     * @param iScanCallback
     */
    public void removeIScanCallback(IScanCallback iScanCallback) {
        if (!isHasConnect()) {
            return;
        }
        mBleOperationService.removeIScanCallback(iScanCallback);
    }

    /**
     * 设置扫描的时间 -1为一直扫描,直到调用停止stopScan
     *
     * @param scanTime
     * @return
     */
    public BleServiceManager setScanTime(int scanTime) {
        this.scanTime = scanTime;
        delayedStopScan();
        return this;
    }


    /**
     * 设置设备数据监听
     *
     * @param braceletDataCallback
     */
    public void setBraceletDataCallback(BraceletDataCallback braceletDataCallback) {
        this.braceletDataCallback = braceletDataCallback;
    }

    /**
     * 设置设备连接状态监听
     *
     * @param iConnectCallback
     */
    public void setiConnectCallback(IConnectCallback iConnectCallback) {
        this.iConnectCallback = iConnectCallback;
    }

    /**
     * 获取当前使用主服务UUID通道
     *
     * @return
     */
    public UUID getServiceUUID() {
        if (!isHasConnect()) {
            return null;
        }
        return mBleOperationService.getServiceUUID(mMAC);
    }

    /**
     * 获取当前读取数据的通道
     *
     * @return
     */
    public BluetoothGattCharacteristic getReadBluetoothGattCharacteristic() {
        if (!isHasConnect()) {
            return null;
        }
        return  mBleOperationService.getReadBluetoothGattCharacteristic(mMAC);
    }

    /**
     * 获取当前接收数据的通道
     *
     * @return
     */
    public BluetoothGattCharacteristic getNotifyBluetoothGattCharacteristic() {
        if (!isHasConnect()) {
            return null;
        }
        return  mBleOperationService.getNotifyBluetoothGattCharacteristic(mMAC);
    }

    /**
     * 获取当前写入数据的通道
     *
     * @return
     */
    public BluetoothGattCharacteristic getWriteBluetoothGattCharacteristic() {
        if (!isHasConnect()) {
            return null;
        }
        return  mBleOperationService.getWriteBluetoothGattCharacteristic(mMAC);
    }

}
