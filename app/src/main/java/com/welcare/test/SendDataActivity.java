package com.welcare.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.vise.baseble.core.BluetoothGattChannel;
import com.vise.baseble.exception.BleException;
import com.vise.baseble.model.BluetoothLeDevice;
import com.welcare.hjkblelibrary.bleinterface.BraceletDataCallback;
import com.welcare.hjkblelibrary.entity.BloodPressure;
import com.welcare.hjkblelibrary.entity.DeviceType;
import com.welcare.hjkblelibrary.entity.HeartData;
import com.welcare.hjkblelibrary.entity.OxyData;
import com.welcare.hjkblelibrary.entity.SleepData;
import com.welcare.hjkblelibrary.entity.StepData;
import com.welcare.hjkblelibrary.utile.BleServiceManager;
import com.welcare.hjkblelibrary.utile.BraceletInstructions;
import com.welcare.hjkblelibrary.utile.hex.Hex;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SendDataActivity extends AppCompatActivity {

    @BindView(R.id.uuid_text)
    TextView uuidText;
    @BindView(R.id.message)
    TextView message;
    private Menu mMenu;
    private BluetoothLeDevice bluetoothLeDevice;


    private BraceletDataCallback mBraceletDataCallback = new BraceletDataCallback() {
        @Override
        public void onBindDeviceState(boolean success) {
            setMessage("绑定状态:"+success);
        }

        @Override
        public void onLoginDeviceState(boolean success) {
            setMessage("登录状态:"+success);
        }

        @Override
        public void onHeart(HeartData heart) {
            setMessage("心率测量时间:"+heart.time+"\t心率:"+heart.heart);
        }

        @Override
        public void onStep(StepData step) {
            setMessage("运动时间:"+step.time+"\t运动距离:"+step.km+" km\t运动卡路里:"+step.kcal+"\t运动步数:"+step.step);
        }

        @Override
        public void onOXY(OxyData oxy) {
            setMessage("血氧测量时间:"+oxy.time+"\t血氧:"+oxy.oxy);
        }

        @Override
        public void onBloodPressure(BloodPressure bloodPressure) {
            setMessage("血压测量时间:"+bloodPressure.time+"\t血压收缩压:"+bloodPressure.systolic+"\t血压舒张压:"+bloodPressure.diastolic);
        }

        @Override
        public void onSleep(SleepData sleepData) {
            setMessage("睡眠数据保存时间:"+sleepData.time+"\t深睡:"+sleepData.deepSleep+"分钟\t浅睡:"+sleepData.lightSleep+"分钟\t未睡:"+sleepData.notAsleep+"分钟");
        }

        @Override
        public void onHeartOXYBPHistory(ArrayList<HeartData> hearts, ArrayList<OxyData> oxys, ArrayList<BloodPressure> bloodPressures) {
            for(HeartData heartData :hearts)
            {
                setMessage("心率历史数据:测量时间:"+heartData.time+"\t心率:"+heartData.heart);
            }
            for(OxyData oxyData :oxys)
            {
                setMessage("血氧历史:测量时间:"+oxyData.time+"\t血氧:"+oxyData.oxy);
            }
            for(BloodPressure bloodPressure :bloodPressures)
            {
                setMessage("血压历史:测量时间:"+bloodPressure.time+"\t收缩压:"+bloodPressure.systolic+"\t舒张压:"+bloodPressure.diastolic);
            }
            sendData(BraceletInstructions.getDZ800BPOXYHEARTHistorySuccessInstructions());
        }

        @Override
        public void onStepHistory(ArrayList<StepData> steps) {
            for(StepData stepData :steps)
            {
                setMessage("运动历史:时间:"+stepData.time+"\t运动距离:"+stepData.km+" km\t运动卡路里:"+stepData.kcal+"\t运动步数:"+stepData.step);

            }
            sendData(BraceletInstructions.getDZ800MotionHistorySuccessInstructions());
        }

        @Override
        public void onElectricQuantity(int electricQuantity, int state) {
            setMessage("当前电量:"+electricQuantity+"%\t当前状态:"+(state==0?"未充电":state==1?"充电完成":"正在充电"));

        }

        @Override
        public void onVersion(String version) {
            setMessage("当前版本:"+version);
        }

        @Override
        public void onSuccess(byte[] data, BluetoothGattChannel bluetoothGattChannel, BluetoothLeDevice bluetoothLeDevice) {
//            setMessage("接收到的原始数据:"+ new String(Hex.encodeHex(data)));
        }

        @Override
        public void onFailure(BleException exception) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_data);
        ButterKnife.bind(this);
        BleConnectActivity.sendDataActivity = this;
        init();
    }

    /**
     * 初始化
     */
    private void init()
    {
        BleServiceManager.getInstance(this).setBraceletDataCallback(mBraceletDataCallback);
        uuidText.setText(
                "uuidService:"+(BleServiceManager.getInstance(this).getServiceUUID()!=null?BleServiceManager.getInstance(this).getServiceUUID().toString():"")
                        +"\nreadCharacteristic:"+(BleServiceManager.getInstance(this).getReadBluetoothGattCharacteristic()!=null?BleServiceManager.getInstance(this).getReadBluetoothGattCharacteristic().getUuid().toString():"")
                        +"\nwriteCharacteristic:"+(BleServiceManager.getInstance(this).getWriteBluetoothGattCharacteristic()!=null?BleServiceManager.getInstance(this).getWriteBluetoothGattCharacteristic().getUuid().toString():"")
                        +"\nnotifyService:"+(BleServiceManager.getInstance(this).getNotifyBluetoothGattCharacteristic()!=null?BleServiceManager.getInstance(this).getNotifyBluetoothGattCharacteristic().getUuid().toString():"")
        );
        message.setMovementMethod(ScrollingMovementMethod.getInstance());
        bluetoothLeDevice = getIntent().getParcelableExtra("device");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        initMenu(mMenu);
        return super.onCreateOptionsMenu(menu);
    }
   private static final int MENU_BIND_ID = 10001; //绑定命令

   private static final int MENU_LOGIN_ID = 10002;//登录命令
   private static final int MENU_FIND_DEVICE_ID = 10003;//查找手环命令
//    private static final int MENU_GET_V_ID = 10004;//获取手环版本
    private static final int MENU_GET_ELECTRICITY = 10005;//获取手环电量
    private static final int MENU_GET_HEART = 10006;//获取心率血压血氧
    private static final int MENU_GET_STEP = 10007;//获取运动数据
    private static final int MENU_SYN_STEP = 10008;//设置运动实时同步数据
    private static final int MENU_SYN_HEART = 10009;//设置心率实时同步数据
    private static final int MENU_HISTORY_STEP = 10010;//获取运动历史数据
    private static final int MENU_HISTORY_HEART = 10011;//获取心率实时同步数据
    private static final int MENU_GET_SLEEP = 10012;//获取睡眠数据
    private static final int MENU_HBC = 10013;//开关HBC
    private static final int MENU_HBC_OPEN = 100131;//开启HBC
    private static final int MENU_HBC_CLOSE= 100132;//关闭HBC
    private static final int MENU_SYN_GOLD = 10014;//同步金币运动数据
    private static final int MENU_SYN_TIME = 10015;//同步时间
    private static final int MENU_UN_BIND_ID = 100016; //解绑命令

    private void initMenu(Menu menu)
    {
        if(menu == null)
        {
            return;
        }
        DeviceType deviceType = DeviceType.getType(bluetoothLeDevice.getName());
        menu.add(0,MENU_BIND_ID,0,"绑定设备");
        menu.add(0,MENU_UN_BIND_ID,0,"解绑设备");
        menu.add(0,MENU_LOGIN_ID,0,"登录设备");
        menu.add(0,MENU_SYN_TIME,0,"同步时间");
        menu.add(0,MENU_FIND_DEVICE_ID,0,"查找手环");
//        menu.add(0,MENU_GET_V_ID,0,"获取手环版本");
        menu.add(0,MENU_GET_ELECTRICITY,0,"获取手环电量");
        menu.add(0,MENU_GET_HEART,0,"获取心率血压血氧");
        menu.add(0,MENU_GET_STEP,0,"获取运动数据");
        menu.add(0,MENU_GET_STEP,0,"获取运动数据");
        if(deviceType == DeviceType.QS80) {
            menu.add(0, MENU_SYN_STEP, 0, "设置运动实时同步数据");
            menu.add(0, MENU_SYN_HEART, 0, "设置心率实时同步数据");
        }
        if(deviceType == DeviceType.DZ800) {
            menu.add(0, MENU_HISTORY_STEP, 0, "获取运动历史数据");
            menu.add(0, MENU_HISTORY_HEART, 0, "获取心率历史数据");
            menu.add(0, MENU_HBC, 0, "开关HBC");
            menu.add(MENU_HBC,MENU_HBC_OPEN,0,"开启HBC");
            menu.add(MENU_HBC,MENU_HBC_CLOSE,0,"关闭HBC");
            menu.add(0, MENU_SYN_GOLD, 0, "同步金币运动数据");

        }
        menu.add(0,MENU_GET_SLEEP,0,"获取睡眠数据");


//        static java.lang.String 	getDZ800BPOXYHEARTHistoryRequestInstructions()
//        心率血压血氧历史请求
//        static java.lang.String 	getDZ800BPOXYHEARTHistorySuccessInstructions()
//        心率血压血氧历史数据接收成功
//        static java.lang.String 	getDZ800HBCInstructions(boolean state)
//        开关HBC模式指令
//        static java.lang.String 	getDZ800MotionHistoryRequestInstructions()
//        DZ800运动历史数据请求
//        static java.lang.String 	getDZ800MotionHistorySuccessInstructions()
//        DZ800运动历史数据接收成功
//        static java.lang.String 	getDZUserInfoInstructions(int sex, int age, int height, int weight)
//        获取用户信息设置命令
//        static java.lang.String 	getElectricityInstructions()
//        获取电量命令
//        static java.lang.String 	getFindBraceletInstructions()
//        查找手环命令
//        static java.lang.String 	getFirmwareMsgInstructions()
//        获取取消升级固件命令
//        static java.lang.String 	getFirmwareUpCancelInstructions()
//        获取取消升级固件命令
//        static java.lang.String 	getFirmwareUpStatrtsInstructions(byte[] datas)
//        获取升级固件命令
//        static java.lang.String 	getGoldCoinInstructions(int goldCoin, int step, double kcal, double km)
//        获取发送金币数据信息 此命令DZ800有效
//        static java.lang.String 	getHeartRateInstructions()
//        心率数据接收命令
//        static java.lang.String 	getHeartRateInstructions(boolean state)
//        心率数据接收成功或者失败命令
//        static java.lang.String 	getHeartRateTestInstructions(boolean state)
//        心率测试
//        static java.lang.String 	getLanguageInstructions(boolean state)
//        设置语言
//        static java.lang.String 	getLeftGestureControlInstructions(boolean gestureControlOpen, boolean writhingOpen)
//        获取设置左手抬手亮屏指令
//        static java.lang.String 	getLeftWrithingBrightScreenInstructions(boolean writhingOpen, boolean gestureControlOpen)
//        获取设置左手翻腕亮屏指令
//        static java.lang.String 	getLoginInstructions(java.lang.String id, java.lang.String mac)
//        获取登入运动手环的命令
//        static java.lang.String 	getMotionCameraSwitchInstructions(boolean state)
//        拍照开关
//        static java.lang.String 	getMotionInstructions()
//        获取运动数据命令
//        static java.lang.String 	getMotionSynsSetInstructions(boolean state)
//        运动数据同步设置
//        static java.lang.String 	getMotorTestInstructions(boolean state)
//        马达测试
//        static java.lang.String 	getPedometerTestInstructions(boolean state)
//        计步器测试
//        static java.lang.String 	getPowerCloseInstructions()
//        关机测试
//        static java.lang.String 	getPressureInstructions()
//        开启/关闭实时同步数据
//        static java.lang.String 	getRestartInstructions()
//        重启测试
//        static java.lang.String 	getRightGestureControlInstructions(boolean gestureControlOpen, boolean writhingOpen)
//        获取设置右手抬手亮屏指令
//        static java.lang.String 	getRightWrithingBrightScreenInstructions(boolean writhingOpen, boolean gestureControlOpen)
//        获取设置右手翻腕亮屏指令
//        static java.lang.String 	getScreenTestInstructions(boolean state)
//        屏幕测试
//        static java.lang.String 	getSedentaryInstructions(boolean open)
//        获取设置久坐提醒命令 默认为开始时间为9点至晚上20点
//        static java.lang.String 	getSedentaryInstructions(boolean open, int startHour, int endHour, boolean[] repeats, int time)
//        获取设置久坐提醒命令
//        static java.lang.String 	getSendBitmapInstructions(android.content.Context context, java.lang.String instructions, java.lang.String name, java.lang.String phone)
//        获取发送图片命令
//        static java.lang.String 	getSetAutoHeartRateInstructions(boolean open, int interval, java.util.Date... dates)
//        获取设置自动测量心率命令
//        static java.lang.String 	getSetMotionSyncInstructions(boolean state)
//        运动数据同步设置命令
//        static java.lang.String 	getSleepInstructions()
//        睡眠数据接收命令
//        static java.lang.String 	getSleepInstructions(boolean state)
//        睡眠数据接收成功或者失败命令
//        static java.lang.String 	getSMSRemindInstructions(boolean open)
//        获取接收到短信然后震动的指令
//        static java.lang.String 	getTime(java.lang.String[] strs)
//        获取数据的测量时间
//        static java.lang.String 	getTime(java.lang.String[] strs, int monthIndex, int dayIndex, int... yearIndex)
//        获取数据的测量时间
//        static java.lang.String 	getTimeSynsInstructions()
//        获取同步手环时间命令
//        static java.lang.String 	getUltravioletRaysInstructions()
//        紫外线数据接收命令
//        static java.lang.String 	getUltravioletRaysInstructions(boolean state)
//        紫外线数据接收成功或失败命令
//        static java.lang.String 	getUnbundingInstructions()
//        获取解除绑定命令
//        static java.lang.String 	getUnbundingTestInstructions()
//        强制解绑测试
//        static java.lang.String 	getUserInfoInstructions(int sex, int age, int height, int weight)
//        获取用户信息设置命令
//        static java.lang.String 	hex10_16(java.lang.String val)
    }

    private void menuFunction(int menuId)
    {
        switch (menuId)
        {
            case  MENU_BIND_ID : //绑定命令
                sendData(BraceletInstructions.getBindingInstructions("000000000001",bluetoothLeDevice.getAddress()));
                break;
            case MENU_UN_BIND_ID://解绑命令
                sendData(BraceletInstructions.getUnbundingInstructions());
                break;
            case  MENU_LOGIN_ID  ://登录命令
                sendData(BraceletInstructions.getLoginInstructions("000000000001",bluetoothLeDevice.getAddress()));
                break;
            case  MENU_FIND_DEVICE_ID  ://查找手环命令
                sendData(BraceletInstructions.getFindBraceletInstructions());
                break;
//            case  MENU_GET_V_ID  ://获取手环版本
//
//                break;
            case  MENU_GET_ELECTRICITY  ://获取手环电量
                sendData(BraceletInstructions.getElectricityInstructions());
                break;
            case  MENU_GET_HEART  ://获取心率血压血氧
                sendData(BraceletInstructions.getHeartRateInstructions());
                break;
            case  MENU_GET_STEP  ://获取运动数据
                sendData(BraceletInstructions.getMotionInstructions());
                break;
            case  MENU_SYN_STEP ://设置运动实时同步数据
                sendData(BraceletInstructions.getRealTimeSyncInstructions(BraceletInstructions.REAL_TIME_SYNC_MOTION));
                break;
            case  MENU_SYN_HEART  ://设置心率实时同步数据
                sendData(BraceletInstructions.getRealTimeSyncInstructions(BraceletInstructions.REAL_TIME_SYNC_HEART_RATE));
                break;
            case  MENU_HISTORY_STEP  ://获取运动历史数据
                sendData(BraceletInstructions.getDZ800MotionHistoryRequestInstructions());
                break;
            case  MENU_HISTORY_HEART ://获取心率历史数据
                sendData(BraceletInstructions.getDZ800BPOXYHEARTHistoryRequestInstructions());
                break;
            case  MENU_GET_SLEEP  ://获取睡眠数据
                sendData(BraceletInstructions.getSleepInstructions());
                break;
            case  MENU_HBC  ://开关HBC

                break;
            case  MENU_HBC_OPEN  ://开HBC

                sendData(BraceletInstructions.getDZ800HBCInstructions(true));
                break;
            case  MENU_HBC_CLOSE  ://关HBC
                sendData(BraceletInstructions.getDZ800HBCInstructions(false));
                break;
            case  MENU_SYN_GOLD  ://同步金币运动数据
                sendData(BraceletInstructions.getGoldCoinInstructions(5580,1234,2200,4200));

                break;
            case  MENU_SYN_TIME ://同步时间
                sendData(BraceletInstructions.getTimeSynsInstructions());
                break;
        }
    }
    private void sendData(String string)
    {
        setMessage("发送命令:"+string);
        BleServiceManager.getInstance(this).sendData(string);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        menuFunction(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    /**显示数据
     * @param text
     */
    private void setMessage(final String text)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                message.append(text+"\n");
                int offset = message.getLineCount() * message.getLineHeight();
                if(offset > message.getHeight()){
                    message.scrollTo(0,offset - message.getHeight());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleConnectActivity.sendDataActivity = null;
    }
}
