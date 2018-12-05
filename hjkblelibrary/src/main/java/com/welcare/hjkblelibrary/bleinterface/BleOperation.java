package com.welcare.hjkblelibrary.bleinterface;

import android.os.Handler;
import android.os.HandlerThread;

import com.vise.baseble.callback.IBleCallback;
import com.vise.baseble.callback.IConnectCallback;
import com.vise.baseble.model.BluetoothLeDevice;

/**
 * Created by admin on 2018/11/26.
 * 蓝牙操作接口
 */

public interface BleOperation {

    /**连接设备
     * @param mac 连接设备的物理地址
     * @return
     */
    public boolean connect(String mac);

    /**连接设备
     * @param bluetoothLeDevice 连接的设备
     * @return
     */
    public boolean connect(BluetoothLeDevice bluetoothLeDevice);

    /**断开连接
     * @return
     */
    public boolean disConnect();

    /**发送数据
     * @param data
     * @return
     */
    public boolean sendData(byte[] data);
    /**发送数据
     * @param data
     * @return
     */
    public boolean sendData(String data);

    /**读取数据
     * @return
     */
    public boolean readData();

    /**设置主通道服务UUID
     * @param serviceUUID
     * @return
     */
    public BleOperation setServiceUUID(String serviceUUID);

    /**设置连接状态监听
     * @param iConnectCallback
     * @return
     */
    public BleOperation setOnConnectCallback(IConnectCallback iConnectCallback);

    /**设置数据回调监听
     * @param iBleCallback
     * @return
     */
    public BleOperation setOnNotifyAndReadCallback(IBleCallback iBleCallback);

    /**设置是否重连
     * @param isOpen
     * @return
     */
    public BleOperation setReconnect(boolean isOpen);

    /**设置重连最大次数
     * @param count
     * @return
     */
    public BleOperation setReconnectMaxCount(int count);

    /**判断是否连接
     * @return
     */
    public boolean isConnect();

    /**
     * 释放资源
     */
    public void close();

    /**
     * 断开后再次调用连接
     */
    public void againConnect();

    /**获取当前蓝牙操作对象
     * @return
     */
    public BleOperation getBleOperation();
//    public BleOperation setHandler(HandlerThread handlerThread);


}
