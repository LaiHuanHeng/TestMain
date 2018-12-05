package com.welcare.hjkblelibrary.bleoperation;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.vise.baseble.ViseBle;
import com.vise.baseble.callback.IBleCallback;
import com.vise.baseble.callback.IConnectCallback;
import com.vise.baseble.common.PropertyType;
import com.vise.baseble.core.BluetoothGattChannel;
import com.vise.baseble.core.DeviceMirror;
import com.vise.baseble.exception.BleException;
import com.vise.baseble.model.BluetoothLeDevice;
import com.welcare.hjkblelibrary.bleinterface.BleOperation;
import com.welcare.hjkblelibrary.bleinterface.BraceletDataCallback;
import com.welcare.hjkblelibrary.service.BleOperationService;
import com.welcare.hjkblelibrary.utile.HandlerThreadManager;
import com.welcare.hjkblelibrary.utile.LogUtile;
import com.welcare.hjkblelibrary.utile.hex.Hex;

import java.util.List;
import java.util.UUID;

/**
 * Created by admin on 2018/11/26.
 * 蓝牙操作基类
 */

public class BaseBleOperation implements BleOperation {

    private final   String TAG = getClass().getName();//"BaseBleOperation";
    private static final int MSG_WHAT_RECONNECT = 10001;//重连id
    private static final int MSG_WHAT_CONNECT = 10002;//连接id
    private static final int MSG_WHAT_DISCONNECT = 10003;//断开连接id
    private static final int MSG_WHAT_SEND_DATA = 10004;//发送数据ID
    private static final int MSG_WHAT_READ_DATA = 10005;//读取数据ID

    private static final int MSG_WHAT_BACK_CONNECT_SUCCESS = 20001;//连接回调
    private static final int MSG_WHAT_BACK_CONNECT_FAIL = 20002;//连接失败回调
    private static final int MSG_WHAT_BACK_DISCONNECT = 20003;//断开连接回调
    private static final int MSG_WHAT_BACK_DATA_RECEPTION_SUCCESS = 20004;//接收数据成功
    private static final int MSG_WHAT_BACK_DATA_RECEPTION_FAIL= 20005;//接收数据失败




    private DeviceMirror mDeviceMirror;//设备操作模型
    private String mMac;//设备物理地址
    private BluetoothLeDevice mBluetoothLeDevice;//需要连接的设备
    private int reconnectMaxCount = 10; //最大重连次数
    private int reconnectCount = 0;//当前重连次数
    private int reconnectTime = 10 * 1000;//重连间隔时间
    private boolean isReconnect = false;//是否开启重连
    private boolean isDisconnect = false;//是否主动断开
    private IConnectCallback iConnectCallback;//外置连接回调
    private IBleCallback mIBleCallback;//外置数据通知回调
    private UUID serviceUUID;//主服务UUID
    private UUID readUUID;//读取UUID
    private UUID notifyUUID;//数据通道UUID
    private UUID writeUUID;//写入UUID
    private boolean isIndicate; //是否指示器
    private boolean isInitServiceUUID = false;//是否初始化了
    private static int sendIntervalTime = 400; //发送数据间隔
    private long sendDataIntervalTime = sendIntervalTime;//发送数据队列间隔

    private BluetoothGattCharacteristic
            readBluetoothGattCharacteristic,//读取通道
            notifyBluetoothGattCharacteristic,//数据回调通道或者读取特征通道
            writeBluetoothGattCharacteristic;//数据写入通道
//    private BluetoothGattChannel mBluetoothGattChannel;//连接的设备信息

    //连接状态监听
    private IConnectCallback mIConnectCallback = new IConnectCallback() {
        @Override
        public void onConnectSuccess(DeviceMirror deviceMirror) {
            setLog("连接成功");
            mHandler.removeMessages(MSG_WHAT_RECONNECT);
            mDeviceMirror = deviceMirror;
            reconnectCount = 0;
            initBleGattCharacteristic(deviceMirror);

        }

        @Override
        public void onConnectFailure(BleException exception) {
            setLog("连接失败");
            mHandler.removeMessages(MSG_WHAT_SEND_DATA);//删除所有发送数据
            mHandler.sendEmptyMessageDelayed(MSG_WHAT_RECONNECT, reconnectTime);
            if(!isReconnect || reconnectCount == reconnectMaxCount) {
                mHandlerCallback.sendMessage(Message.obtain(mHandlerCallback, MSG_WHAT_BACK_CONNECT_FAIL, exception));
            }
        }

        @Override
        public void onDisconnect(boolean isActive) {
            setLog("断开连接" + isActive);
                mHandler.removeMessages(MSG_WHAT_SEND_DATA);//删除所有发送数据
                mHandler.sendEmptyMessageDelayed(MSG_WHAT_RECONNECT, reconnectTime);
            if(!isReconnect || reconnectCount == reconnectMaxCount) {
                mHandlerCallback.sendMessage(Message.obtain(mHandlerCallback, MSG_WHAT_BACK_DISCONNECT, isActive));
            }
        }
    };

    /**
     * 绑定UUID回调
     */
    private IBleCallback mBindNotifyCallbace = new IBleCallback() {
        @Override
        public void onSuccess(byte[] data, BluetoothGattChannel bluetoothGattChannel, BluetoothLeDevice bluetoothLeDevice) {
            setLog("绑定服务成功:" + bluetoothGattChannel.getCharacteristicUUID().toString());
            UUID uuid = bluetoothGattChannel.getCharacteristicUUID();
            if(uuid != null)
            {
                if(writeUUID!= null && uuid.toString().equalsIgnoreCase(writeUUID.toString()))
                {
                    setLog("绑定写入通道成功");
                }
                if(readUUID!= null && uuid.toString().equalsIgnoreCase(readUUID.toString()))
                {
                    setLog("绑定读取通道成功");
                }
                if(notifyUUID!= null && uuid.toString().equalsIgnoreCase(notifyUUID.toString()))
                {
                    setLog("绑定数据回调通道成功");
//                  mBluetoothGattChannel = BluetoothGattChannel;
                    bindNotifyCallbace(bluetoothGattChannel);
                }


            }

        }

        @Override
        public void onFailure(BleException exception) {
            LogUtile.i(TAG, "绑定服务失败:" + exception.toString());
        }
    };
    /**
     * 蓝牙数据回调通知
     */
    private IBleCallback mNotifyBleCallback = new IBleCallback() {
        @Override
        public void onSuccess(byte[] data, BluetoothGattChannel bluetoothGattChannel, BluetoothLeDevice bluetoothLeDevice) {
            LogUtile.i(TAG, "数据回调:" + new String(Hex.encodeHex(data)) + "\n回调UUID通道:" + bluetoothGattChannel.getCharacteristicUUID().toString());
            Object[] os = {data,bluetoothGattChannel,bluetoothLeDevice};
            mHandlerCallback.sendMessage(Message.obtain(mHandlerCallback,MSG_WHAT_BACK_DATA_RECEPTION_SUCCESS,os));
        }

        @Override
        public void onFailure(BleException exception) {
            LogUtile.i(TAG, "数据回调失败:" + exception.toString());
            mHandlerCallback.sendMessage(Message.obtain(mHandlerCallback,MSG_WHAT_BACK_DATA_RECEPTION_FAIL,exception));
        }
    };


    private Handler  mHandler = new Handler(HandlerThreadManager.getInstance().getHanderThread(BleOperationService.BLE_OPERATION_THREAD_NAME).getLooper()) {//子线程
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case MSG_WHAT_RECONNECT:
                    if(!isDeviceConnect()) {
                        reconnectConnect();
                    }
                    break;
                case MSG_WHAT_CONNECT:
                    connect();
                    break;
                case MSG_WHAT_DISCONNECT:
                    if(mDeviceMirror == null)
                    {
                        setLog("设备未连接");
                        break;
                    }
                    mDeviceMirror.disconnect();
                    ViseBle.getInstance().disconnect(mDeviceMirror.getBluetoothLeDevice());
                    break;
                case MSG_WHAT_SEND_DATA:
                    if(writeUUID != null && isDeviceConnect())
                    {
                        byte[] datas = (byte[]) msg.obj;
                        mDeviceMirror.writeData(datas);

                    }
                    sendDataIntervalTime-=sendIntervalTime;
                    if(sendDataIntervalTime <=sendIntervalTime)
                    {
                        sendDataIntervalTime = sendIntervalTime;
                    }
                    break;
                case MSG_WHAT_READ_DATA:
                    if(readUUID == null||!isDeviceConnect())
                    {
                        setLog("此设备不支持读取功能或未连接");
                        break;
                    }
                    mDeviceMirror.readData();
                    break;
            }


        }
    };
    protected Handler mHandlerCallback = new Handler(HandlerThreadManager.getInstance().getHanderThread(BleOperationService.BLE_OPERATION_THREAD_NAME).getLooper())//外置子线程回调
    {
        @Override
        public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MSG_WHAT_BACK_CONNECT_SUCCESS://连接回调
                if(msg.obj instanceof  DeviceMirror)
                {
                    notifyIConnectCallback((DeviceMirror)msg.obj,null,null);
                    notifyConnectState(true);
                }
                break;
            case MSG_WHAT_BACK_CONNECT_FAIL://连接失败回调
                if(msg.obj instanceof  BleException)
                {
                    notifyIConnectCallback(null, (BleException) msg.obj,null);
                }
                break;
            case MSG_WHAT_BACK_DISCONNECT://断开连接回调
                if(msg.obj instanceof  Boolean)
                {
                    notifyIConnectCallback(null, null, (Boolean) msg.obj);
                    notifyConnectState(false);
                }
                break;
            case MSG_WHAT_BACK_DATA_RECEPTION_SUCCESS:;//接收数据成功
                if(msg.obj instanceof Object[])
                {
                    Object[] data = (Object[]) msg.obj;
                    notifyIBleCallback((byte[]) data[0],(BluetoothGattChannel) data[1],(BluetoothLeDevice) data[2],null);
                }
                break;
            case MSG_WHAT_BACK_DATA_RECEPTION_FAIL://接收数据失败
                if(msg.obj instanceof  BleException)
                {
                    notifyIBleCallback(null, null,null, (BleException) msg.obj);
                }
                break;
        }
    }
    };
    public BaseBleOperation() {

    }

    /**
     * 重连操作
     */
    private void reconnectConnect() {
        if(!isReconnect||isDisconnect)
        {
            if(!isDisconnect) {
                LogUtile.i(TAG, "主动断开,默认关闭重连");
            }else {LogUtile.i(TAG, "已经断开并且关闭重连,请重新连接");}
            return;
        }
        if (reconnectCount >= reconnectMaxCount) {
            return;
        }
        reconnectCount++;
        if(mDeviceMirror == null) {
         connect();
        }else {
            mDeviceMirror.connect(mIConnectCallback);
        }
        mHandler.sendEmptyMessageDelayed(MSG_WHAT_RECONNECT,reconnectTime);

    }

    /**初始化蓝牙数据传输通道
     * @param deviceMirror
     */
    private void initBleGattCharacteristic(DeviceMirror deviceMirror) {
        if (serviceUUID == null && serviceUUID.toString().length() <= 10) {
            LogUtile.i(TAG, "服务主UUID为空或不正确");
            return;
        }
        List<BluetoothGattCharacteristic> bluetoothGattCharacteristics = deviceMirror.getGattCharacteristicList(serviceUUID);
        for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattCharacteristics) {
            int charaProp = bluetoothGattCharacteristic.getProperties();
            // 可读
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                if (readUUID == null) {
                    readUUID = bluetoothGattCharacteristic.getUuid();
                    readBluetoothGattCharacteristic = bluetoothGattCharacteristic;
                }
            }
            // 可写，注：要 & 其可写的两个属性
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0
                    || (charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                if (writeUUID == null) {
                    writeUUID = bluetoothGattCharacteristic.getUuid();
                    writeBluetoothGattCharacteristic = bluetoothGattCharacteristic;
                }

            }
            // 可通知，可指示
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0
                    || (charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0
                    ) {

                if (notifyUUID == null) {
                    if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
                        isIndicate = true;
                    }
                    notifyUUID = bluetoothGattCharacteristic.getUuid();
                    notifyBluetoothGattCharacteristic = bluetoothGattCharacteristic;
                }
            }

        }

        if (writeUUID != null) {
            BluetoothGattChannel bluetoothGattChannel = new BluetoothGattChannel.Builder()
                    .setBluetoothGatt(deviceMirror.getBluetoothGatt())
                    .setPropertyType(PropertyType.PROPERTY_WRITE)
                    .setServiceUUID(serviceUUID)
                    .setCharacteristicUUID(writeUUID)
//                    .setDescriptorUUID(descriptorUUID)
                    .builder();
            deviceMirror.bindChannel(mBindNotifyCallbace, bluetoothGattChannel);
        }

        if (isIndicate) {
            BluetoothGattChannel bluetoothGattChannel = new BluetoothGattChannel.Builder()
                    .setBluetoothGatt(deviceMirror.getBluetoothGatt())
                    .setPropertyType(PropertyType.PROPERTY_INDICATE)
                    .setServiceUUID(serviceUUID)
                    .setCharacteristicUUID(notifyUUID)
//                    .setDescriptorUUID(descriptorUUID)
                    .builder();
            deviceMirror.bindChannel(mBindNotifyCallbace, bluetoothGattChannel);
            deviceMirror.registerNotify(isIndicate);
        } else {
            if (notifyUUID != null) {
                BluetoothGattChannel bluetoothGattChannel = new BluetoothGattChannel.Builder()
                        .setBluetoothGatt(deviceMirror.getBluetoothGatt())
                        .setPropertyType(PropertyType.PROPERTY_NOTIFY)
                        .setServiceUUID(serviceUUID)
                        .setCharacteristicUUID(notifyUUID)
//                    .setDescriptorUUID(descriptorUUID)
                        .builder();
                deviceMirror.bindChannel(mBindNotifyCallbace, bluetoothGattChannel);
                deviceMirror.registerNotify(false);

            }
        }

        if (readUUID != null) {
            BluetoothGattChannel bluetoothGattChannel = new BluetoothGattChannel.Builder()
                    .setBluetoothGatt(deviceMirror.getBluetoothGatt())
                    .setPropertyType(PropertyType.PROPERTY_READ)
                    .setServiceUUID(serviceUUID)
                    .setCharacteristicUUID(readUUID)
//                      .setDescriptorUUID(descriptorUUID)
                    .builder();
            deviceMirror.bindChannel(mBindNotifyCallbace, bluetoothGattChannel);
        }
        LogUtile.i(TAG, "当前特征:writeUUID:" + (writeUUID != null ? writeUUID.toString() : "null")
                + "\nnotifyUUID:" + (notifyUUID != null ? notifyUUID.toString() : "null")
                + "\nreadUUID:" + (readUUID != null ? readUUID.toString() : "null")

        );
        if (writeUUID == null || notifyUUID == null) {
            LogUtile.i(TAG, "当前ServiceUUID找不到主要特征:writeUUID:" + (writeUUID != null ? writeUUID.toString() : "null")
                    + "\nnotifyUUID:" + (notifyUUID != null ? notifyUUID.toString() : "null"));
        }
        isInitServiceUUID = true;
    }

    /**处理设备回调的数据
     * @param bytes
     */
    public void handlerDeviceCallback(byte[] bytes)
    {

    }
    /**
     * 绑定通知回调
     */
    private void bindNotifyCallbace(BluetoothGattChannel bluetoothGattChannel)
    {
        if(mDeviceMirror == null || bluetoothGattChannel == null)
        {
            setLog("设备未连接");
            return;
        }
        mDeviceMirror.setNotifyListener(bluetoothGattChannel.getGattInfoKey(), mNotifyBleCallback);
        mHandlerCallback.sendMessage(Message.obtain(mHandlerCallback,MSG_WHAT_BACK_CONNECT_SUCCESS,mDeviceMirror));
    }


    /**
     * 连接设备
     */
    private void connect()
    {
        if(mBluetoothLeDevice == null && mMac == null)
        {
            setLog("连接的设备为NULL");
            return;
        }

        if(mBluetoothLeDevice != null)
        {
            ViseBle.getInstance().connect(mBluetoothLeDevice,mIConnectCallback);
        }else
        {
            if(mMac.length() == 17)
            {
                ViseBle.getInstance().connectByMac(mMac,mIConnectCallback);
            }else
            {
                setLog("连接MAC地址错误:"+mMac);
            }
        }

    }


    /**获取当前MAC
     * @return
     */
    private String getMac()
    {
        if(mMac!=null)
        {
            return mMac;
        }
        if(mBluetoothLeDevice!=null)
        {
            return mBluetoothLeDevice.getAddress();
        }
        if(mDeviceMirror!=null)
        {
            return mDeviceMirror.getBluetoothLeDevice().getAddress();
        }

        return "";
    }

    /**通知外置连接回调
     * @param deviceMirror 设备模型
     * @param e             异常
     * @param isActive     断开连接
     */
    private void notifyIConnectCallback(DeviceMirror deviceMirror,BleException e,Boolean isActive)
    {
        if(iConnectCallback!=null)
        {
                 if(deviceMirror!=null) {
                     iConnectCallback.onConnectSuccess(deviceMirror);
                 }
                 if(e!=null)
                 {
                     iConnectCallback.onConnectFailure(e);
                 }
                 if(isActive != null)
                 {
                     iConnectCallback.onDisconnect(isActive);
                 }
        }
    }

    /**通知外置接收数据的回调
     * @param data
     * @param bluetoothGattChannel
     * @param bluetoothLeDevice
     * @param e
     */
    private void notifyIBleCallback(byte[] data, BluetoothGattChannel bluetoothGattChannel, BluetoothLeDevice bluetoothLeDevice,BleException e)
    {
        if(mIBleCallback != null)
        {
            if(e != null)
            {
                mIBleCallback.onFailure(e);
            }else
             {
                 handlerDeviceCallback(data);
                 mIBleCallback.onSuccess(data,bluetoothGattChannel,bluetoothLeDevice);

             }

        }
    }

    /**通知连接状态
     * @param isConnect true 已连接 false 未连接
     */
    public void notifyConnectState(boolean isConnect)
    {

    }

    /**打印日志
     * @param msg
     */
    public void setLog(String msg)
    {
        LogUtile.i(TAG,getMac()+"\t"+msg);
    }

    /**
     * 连接操作
     *
     * @param mac
     * @return
     */
    @Override
    public boolean connect(String mac) {
        if(mac ==null || mac.length()!=17)
        {
            setLog("MAC地址错误");
            return false;
        }
        reconnectCount = 0;
        isDisconnect = false;
        this.mMac = mac;
        mHandler.sendEmptyMessage(MSG_WHAT_CONNECT);
        return true;
    }

    @Override
    public boolean connect(BluetoothLeDevice bluetoothLeDevice) {
        if(bluetoothLeDevice == null)
        {
            setLog("连接设备为NULL");
            return false;
        }
        reconnectCount = 0;
        isDisconnect = false;
        this.mBluetoothLeDevice = bluetoothLeDevice;
        mHandler.sendEmptyMessage(MSG_WHAT_CONNECT);
        return true;
    }

    /**
     * 断开操作
     *
     * @return
     */
    @Override
    public boolean disConnect() {
        isReconnect = false;
        reconnectCount = 0;
        mHandler.sendEmptyMessage(MSG_WHAT_DISCONNECT);

        return true;
    }

    /**
     * 发送数据操作
     *
     * @param data
     * @return
     */
    @Override
    public boolean sendData(byte[] data) {
        if(mDeviceMirror == null || writeUUID ==null)
        {
            return false;
        }
        if(data.length>20)
        {
            sendDataLenGreaterThan20(data);
            return true;
        }
        mHandler.sendMessageDelayed(Message.obtain(mHandler,MSG_WHAT_SEND_DATA,data),sendDataIntervalTime);
        sendDataIntervalTime+= sendIntervalTime;
        return true;
    }


    /**
     * 发送数据给设备
     *
     * @param data
     * @return
     */
    public boolean sendData(String data) {

        byte[] bytes = null;
        try {
            bytes = Hex.decodeHex(data.toCharArray());
            return sendData(bytes);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }

    /**发送超过20个长度的数据
     * @param datas
     */
    public  void sendDataLenGreaterThan20(byte[] datas)
    {
        int len = 0;
        if(datas.length%20 == 0)
        {
            len = datas.length/20;
        }else
        {
                len = datas.length/20+1;
        }
        for(int i = 0;i<len;i++)
        {
            byte[] data  = new byte[20];
            System.arraycopy(datas,i*20,data,0,data.length);
            sendData(data);
        }

    };

    /**
     * 读取数据操作
     *
     * @return
     */
    @Override
    public boolean readData() {
        mHandler.sendEmptyMessage(MSG_WHAT_READ_DATA);
        return true;
    }


    /**
     * 设置设备通讯主通道服务UUID
     *
     * @param serviceUUID
     * @return
     */
    @Override
    public BleOperation setServiceUUID(String serviceUUID) {
        if(serviceUUID == null || (this.serviceUUID!=null&&this.serviceUUID.toString().equalsIgnoreCase(serviceUUID)))
        {
            return this;
        }
        this.serviceUUID = UUID.fromString(serviceUUID);
        if(isDeviceConnect()&&isInitServiceUUID)
        {
            initBleGattCharacteristic(mDeviceMirror);
        }
        return this;
    }

    /**
     * 设置连接监听
     *
     * @param iConnectCallback
     * @return
     */
    @Override
    public BleOperation setOnConnectCallback(IConnectCallback iConnectCallback) {
        this.iConnectCallback = iConnectCallback;
        return this;
    }

    /**
     * 设置数据监听
     *
     * @param iBleCallback
     * @return
     */
    @Override
    public BleOperation setOnNotifyAndReadCallback(IBleCallback iBleCallback) {
        this.mIBleCallback = iBleCallback;
        return this;
    }

    /**获取外置回调的监听
     * @return
     */
    public IBleCallback getOnNotifyAndReadCallback() {
       return mIBleCallback;
    }
    /**
     * 设置是否重连
     *
     * @param isOpen
     * @return
     */
    @Override
    public BleOperation setReconnect(boolean isOpen) {
        isReconnect = isOpen;
        return this;
    }

    /**
     * 设置最大重连次数
     *
     * @param count
     * @return
     */
    @Override
    public BleOperation setReconnectMaxCount(int count) {
        reconnectMaxCount = count;
        return this;
    }

    /**设备是否连接
     * @return
     */
    private boolean isDeviceConnect()
    {
        if(mDeviceMirror == null)
        {
            setLog("未连接");
            return false;
        }
        boolean isConnect = ViseBle.getInstance().isConnect(mDeviceMirror.getBluetoothLeDevice());
        return isConnect;
    }

    @Override
    public boolean isConnect() {
      if(isDeviceConnect())
      {
          return true;
      }

        return false;
    }
    @Override
    public void close() {
        disConnect();


    }

    /**
     * 再次连接
     */
    @Override
    public void againConnect() {
        if(mDeviceMirror.isConnected())
        {
          return;
        }
        if(reconnectCount != 0 &&reconnectCount<10)
        {
            setLog("正在重连中");
            return;
        }
        connect();
    }

    @Override
    public BleOperation getBleOperation() {
        return this;
    }

    /**获取当前使用主服务UUID通道
     * @return
     */
    public UUID getServiceUUID() {
        return serviceUUID;
    }

    /**获取当前读取数据的通道
     * @return
     */
    public BluetoothGattCharacteristic getReadBluetoothGattCharacteristic() {
        return readBluetoothGattCharacteristic;
    }

    /**获取当前接收数据的通道
     * @return
     */
    public BluetoothGattCharacteristic getNotifyBluetoothGattCharacteristic() {
        return notifyBluetoothGattCharacteristic;
    }

    /**获取当前写入数据的通道
     * @return
     */
    public BluetoothGattCharacteristic getWriteBluetoothGattCharacteristic() {
        return writeBluetoothGattCharacteristic;
    }

    //    @Override
//    public BleOperation getBleOperation() {
//        return this;
//    }


}
