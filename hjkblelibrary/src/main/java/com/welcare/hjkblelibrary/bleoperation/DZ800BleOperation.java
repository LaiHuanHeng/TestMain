package com.welcare.hjkblelibrary.bleoperation;

import android.os.Handler;
import android.os.Message;

import com.vise.baseble.core.BluetoothGattChannel;
import com.vise.baseble.exception.BleException;
import com.vise.baseble.model.BluetoothLeDevice;
import com.welcare.hjkblelibrary.bleinterface.BraceletDataCallback;
import com.welcare.hjkblelibrary.entity.BloodPressure;
import com.welcare.hjkblelibrary.entity.HeartData;
import com.welcare.hjkblelibrary.entity.OxyData;
import com.welcare.hjkblelibrary.entity.SleepData;
import com.welcare.hjkblelibrary.entity.StepData;
import com.welcare.hjkblelibrary.service.BleOperationService;
import com.welcare.hjkblelibrary.utile.BraceletInstructions;
import com.welcare.hjkblelibrary.utile.HandlerThreadManager;
import com.welcare.hjkblelibrary.utile.LogUtile;
import com.welcare.hjkblelibrary.utile.hex.Hex;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2018/11/26.
 * DZ800手环操作
 */

public class DZ800BleOperation extends BaseBleOperation {
    private static final String SERVICE_UUID = "C3E6FEA0-E966-1000-8000-BE99C223DF6A";
    private static final int MSG_WHAT_GET_STEP = 10001;//获取运动ID
    private static final int GET_SREP_INTERVAL_TIME = 15*1000;//间隔15秒获取一次数据
    private int currentStep = -1;
    private StringBuffer dataString = new StringBuffer();
    private BraceletDataCallback mBraceletDataCallback;
    private boolean isConnect = false;
    public DZ800BleOperation(){
        setServiceUUID(SERVICE_UUID);
    }

    private Handler mGetStepHandler = new Handler(HandlerThreadManager.getInstance().getHanderThread(BleOperationService.BLE_OPERATION_THREAD_NAME).getLooper())
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(isConnect)
            {
                sendData(BraceletInstructions.getMotionInstructions());
                mGetStepHandler.sendEmptyMessageDelayed(MSG_WHAT_GET_STEP,10);
            }

        }
    };

    @Override
    public void handlerDeviceCallback(byte[] bytes) {
        super.handlerDeviceCallback(bytes);
        String str = new String(Hex.encodeHex(bytes));
        setLog("接收到的数据:"+str);
        if(str.toUpperCase().startsWith(BraceletInstructions.INSTRUCTIONS_HEAD) && BraceletInstructions.proofreadingCRC(str))
        {
            dataString.setLength(0);
            dataString.append(str);
            byte[] datas = null;
            try{
                datas = Hex.decodeHex(dataString.toString().toCharArray());
            }catch (Exception e){}
            analysisAllData(dataString.toString(),datas);
        }else if(str.toUpperCase().startsWith(BraceletInstructions.INSTRUCTIONS_HEAD) )
        {
            dataString.setLength(0);
            dataString.append(str);
            return;
        }else if(dataString.length()>10)
        {
            if(str.length()>=40) {
                dataString.append(str);
                if (!BraceletInstructions.proofreadingCRC(dataString.toString() )){
                    return;
                }
            }else{
                if(BraceletInstructions.proofreadingCRC(dataString.toString()+str))
                {
                    dataString.append(str);

                }else
                {
                    return;
                }


            }
            byte[] datas = null;
            try{
                datas = Hex.decodeHex(dataString.toString().toCharArray());
            }catch (Exception e){}
            analysisAllData(dataString.toString(),datas);

        }



    }

    /**
     * 解析所有数据
     *
     * @param string
     */
    private synchronized void analysisAllData(String string,byte... bytes) {
        setLog("待解析数据"+dataString.toString());
        if (string.toUpperCase().startsWith(BraceletInstructions.INSTRUCTIONS_HEAD + BraceletInstructions.INSTRUCTIONS_HEART_RATE_RETURN)) {
            String[] strs = BraceletInstructions.analysisData(string);
            if (strs.length >= 11) {
                String measureTime = BraceletInstructions.getTime(strs);
                int measureLen = Integer.valueOf(strs[7], 16);
                String[][] datas = BraceletInstructions.getMeasureData(strs, measureLen, 6);
                if (datas == null) {
                    datas = BraceletInstructions.getMeasureData(strs, measureLen, 5);
                }
                if (datas == null) {
                    datas = BraceletInstructions.getMeasureData(strs, measureLen, 3);
                }

                int heart = 0;      //心率
                int bpSystolic = 0;//收缩压  s 收缩压90-120，
                int bpDiastole = 0;//舒张压  d 舒张压为60-90
                int oxy = 0;
                try {
                    if (datas != null) {
                        for (String[] strings : datas) {


                            if (strings.length >= 3) {
                                // TODO: 2018/10/10 心率
                                int h1 = Integer.valueOf(strings[0], 16);
                                int m1 = Integer.valueOf(strings[1], 16);
                                String mt = (h1 < 10 ? "0" + h1 : h1 + "") + ":" + (m1 < 10 ? "0" + m1 : m1 + "");
                                int h = Integer.valueOf(strings[2] + "", 16);
                                if (h > 30) {

                                    HeartData heartData = new HeartData(measureTime+" "+mt,h);
                                   notifyCallback(heartData,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            -1,
                                            null,
                                            null);
                                }
                                // TODO: 2018/10/10 血压
                                boolean isSava = false;
                                int d = 0, s = 0;
                                if (strings.length == 5) {
                                    d = Integer.valueOf(strings[3] + "", 16);
                                    s = Integer.valueOf(d + "", 16);
                                    isSava = true;
                                }
                                if (strings.length == 6) {
                                    d = Integer.valueOf(strings[3] + "", 16);
                                    s = Integer.valueOf(strings[4] + "", 16);
                                    isSava = true;
                                }
                                if (d <= 30 || s <= 30) {
                                    isSava = false;
                                }
                                if (isSava) {
                                    BloodPressure bloodPressure = new BloodPressure(measureTime+" "+mt,s,d);
                                    notifyCallback(null,
                                            null,
                                            null,
                                            bloodPressure,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            -1,
                                            null,
                                            null);
                                }
                                // TODO: 2018/10/10 血氧

                                boolean isYX = false; //是否有效数据
                                int OXY = 0;
                                if (strings.length >= 6) {
                                    OXY = Integer.valueOf(strings[5] + "", 16);
                                    isYX = true;
                                } else if (strings.length >= 5) {
                                    OXY = Integer.valueOf(strings[4] + "", 16);
                                    isYX = true;
                                }
                                if (isYX) {
                                    if (OXY > 80) {
                                        OxyData oxyData = new OxyData(measureTime+" "+mt,OXY);
                                        notifyCallback(null,
                                                null,
                                                oxyData,
                                                null,
                                                null,
                                                null,
                                                null,
                                                null,
                                                null,
                                                null,
                                                -1,
                                                null,
                                                null);
                                    }

                                }

                            }
                        }

                    }
                    // TODO: 2018/10/10 体脂数据
//                        if (heart > 0) {
//                            int avgHeart = 0;
//                            int totalHeart = 0;
//                            for (MeasureData.HeartMeasure h : measureData.getHeartMeasures()) {
//                                totalHeart += h.getHeart();
//
//                            }
//                            if (measureData.getHeartMeasures().size() > 0) {
//                                avgHeart = totalHeart / measureData.getHeartMeasures().size();
//                                if (avgHeart == 0) {
//                                    avgHeart = 70;
//                                }
//                            }
//                            BodyFat mbodyFat = new BodyFat();
//                            UserInfo userInfo = CurrencyDataManage.getInstance().getLoginUserInfo();
//                            if (userInfo != null) {
//                                double heartWeight = userInfo.getWeight();
//                                if (avgHeart > heart) {
//                                    double w = (avgHeart - heart) / (avgHeart * 100);
//                                    heartWeight = heartWeight - (heartWeight * w);
//                                } else {
//                                    double w = (heart - avgHeart) / (avgHeart * 100);
//                                    heartWeight = heartWeight + (heartWeight * w);
//                                }
//
//                                LogUtils.d(TAG, "analysisBodyData: " + heartWeight);
//
//                                mbodyFat.BodyFat(userInfo.getHeight(), userInfo.getAge(), (float) userInfo.getWeight(), 650, userInfo.getSex());
//                                BodyFatUtil bodyFatUtil = new BodyFatUtil();
//                                //体重
//                                double weight = Double.parseDouble(String.format("%.1f ", heartWeight));
//                                bodyFatUtil.setWeight(weight);
//                                //BMI
//                                double bmi = Double.parseDouble(String.format("%.1f", mbodyFat.getBMI()));
//                                bodyFatUtil.setBmi(bmi);
//                                //体脂
//                                double bodyFatPercent = Double.parseDouble(String.format("%.1f", mbodyFat.getBodyFatPercent()));
//                                bodyFatUtil.setBodyFatPercent(bodyFatPercent);
//                                //脂肪重量
//                                double bodyFatWeight = Double.parseDouble(String.format("%.1f ", mbodyFat.getBodyFatPercent() * weight / 100));
//                                bodyFatUtil.setBodyFatWeight(bodyFatWeight);
//                                //内脏脂肪指数
//                                double bodyViscera = Double.parseDouble(String.format("%.1f", mbodyFat.getBodyViscera()));
//                                bodyFatUtil.setBodyViscera(bodyViscera);
//                                //水分%
//                                double bodyWater = Double.parseDouble(String.format("%.1f", mbodyFat.getBodyWater()));
//                                bodyFatUtil.setBodyWater(bodyWater);
//                                //肌肉KG
//                                double bodyMuscle = Double.parseDouble(String.format("%.1f", mbodyFat.getBodyMuscle()));
//                                bodyFatUtil.setBodyMuscle(bodyMuscle);
//                                //蛋白质
//                                int protein = (int) mbodyFat.getBodyProtein();
//                                bodyFatUtil.setProtein(protein);
//                                //身体年龄
//                                int bodyAge = (int) mbodyFat.getBodyAge();
//                                bodyFatUtil.setBodyAge(bodyAge);
//                                measureData.setBodyFatUtils(bodyFatUtil);
//                                CurrencyDataManage.getInstance().setSpecifiedDateMeasureData(measureData, measureTime);
//
//    //                                    setShowData(bodyFatUtil);
//
//
//                                //体脂数据上传
//    //                                    upBodyFatData(bodyFatUtil);
//
//                            }
//
//                        }




                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (string.toUpperCase().startsWith(BraceletInstructions.INSTRUCTIONS_HEAD + BraceletInstructions.INSTRUCTIONS_SLEEP_RETURN)) {
            // TODO: 2018/10/10  睡眠数据
            String[] strs = BraceletInstructions.analysisData(string);
            if (strs.length >= 11) {
//                LogUtils.i(TAG, "睡眠数据数量:" + strs[7] + "");
//                LogUtils.i(TAG, "睡眠模式:" + strs[8] + "");
//                LogUtils.i(TAG, "睡眠时长-分钟:" + strs[9] + " " + strs[10]);
                String measureTime = BraceletInstructions.getTime(strs);
                int measureLen = Integer.valueOf(strs[7], 16);
                String[][] datas = BraceletInstructions.getMeasureData(strs, measureLen, 3);

                int sleepShallow = 0;//浅睡时间
                int sleepDeep = 0;//深睡
                int sleepNot = 0;//未睡

                try {
                    if (datas != null) {
                        for (String[] strings : datas) {
                            if (strings.length >= 3) {
                                int ms = Integer.valueOf(strings[0], 16);
                                int sleep = Integer.valueOf(strings[2] + strings[1] + "", 16);
                                if (ms == 0) {
                                    sleepNot = sleep;
                                } else if (ms == 1) {
                                    sleepShallow = sleep;
                                } else if (ms == 2) {
                                    sleepDeep = sleep;
                                }
                            }
                        }

                    }
//                    int ms = Integer.valueOf(strs[8], 16);
//                    double ss = 0;
//                    double sd = 0;
//                    double sn = 0;
//                    if (sleepShallow > 0) {
//                        ss = sleepShallow / 6 / 10.0;
//                    }
//                    if (sleepDeep > 0) {
//                        sd = sleepDeep / 6 / 10.0;
//                    }
//                    if (sleepNot > 0) {
//                        sn = sleepNot / 6 / 10.0;//((int)(ss*10+sd*10))/10.0;//
//                    }
                  if(sleepShallow > 0 || sleepDeep > 0 || sleepNot > 0) {
                      SleepData sleepData = new SleepData(measureTime, sleepDeep, sleepShallow, sleepNot);
                      notifyCallback(null,
                              null,
                              null,
                              null,
                              sleepData,
                              null,
                              null,
                              null,
                              null,
                              null,
                              -1,
                              null,
                              null);
                  }


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        } else if (string.toUpperCase().startsWith(BraceletInstructions.INSTRUCTIONS_HEAD + BraceletInstructions.INSTRUCTIONS_MOTION_RETURN)) {
            // TODO: 2018/10/11 运动数据
            String[] strs = BraceletInstructions.analysisData(string);
            if (strs.length >= 23) {
                String measureTime = BraceletInstructions.getTime(strs);
                int measureLen = Integer.valueOf(strs[7], 16);
                String[][] datas = BraceletInstructions.getMeasureData(strs, measureLen, 15);
                int bu = 0;
                double ka = 0;
                double ju = 0;
                double timeLen = 0;
                try {
                    if (datas != null) {

                        for (String[] strings : datas) {
                            timeLen = Integer.valueOf(strings[1], 16);
                            bu = Integer.valueOf(strings[6] + strings[5] + strings[4] + strings[3], 16);
                            ju = Integer.valueOf(strings[14] + strings[13] + strings[12] + strings[11], 16) / 1000.00;
                            ka = Integer.valueOf(strings[10] + strings[9] + strings[8] + strings[7], 16);
                            ju = ((int) (ju * 100)) / 100.0;
                            ka = ((int) (ka * 100)) / 100.0;
                            if(bu>0 )
                            {
                                currentStep = bu;
                                StepData stepData = new StepData(measureTime,bu,ka,ju,-1);
                                notifyCallback(null,
                                        stepData,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        -1,
                                        null,
                                        null);
                            }

                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }else if(string.toUpperCase().startsWith(BraceletInstructions.INSTRUCTIONS_HEAD+BraceletInstructions.INSTRUCTIONS_DZ800_BPOXYHEART_HISTORY))
        {
            // TODO: 2018/11/28 解析心率血压血氧历史数据
                handlerBPHeartOxyHistoryData(string);
        }
        else if(string.toUpperCase().startsWith(BraceletInstructions.INSTRUCTIONS_HEAD+BraceletInstructions.INSTRUCTIONS_DZ800_MOTION_HISTORY_SUCCESS))
        {
            // TODO: 2018/11/28 解析运动历史数据
            handlerMotionHistoryData(string);
        }
        else if(string.toUpperCase().startsWith(BraceletInstructions.INSTRUCTIONS_HEAD+BraceletInstructions.INSTRUCTIONS_BINDING_RETURN))
        {
            // TODO: 2018/11/28 解析绑定成功或者失败
            String[] strs = BraceletInstructions.analysisData(string);
            int state = Integer.parseInt(strs[4]);
            notifyCallback(null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    -1,
                    state == 0,
                    null);


        }else if(string.toUpperCase().startsWith(BraceletInstructions.INSTRUCTIONS_HEAD+BraceletInstructions.INSTRUCTIONS_LOGIN_RETURN))
        {
            // TODO: 2018/11/28 解析登录成功还是失败
            String[] strs = BraceletInstructions.analysisData(string);
            int state = Integer.parseInt(strs[4]);
            notifyCallback(null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    -1,
                    null,
                    state == 0);

        }else if(string.toUpperCase().startsWith(BraceletInstructions.INSTRUCTIONS_HEAD+BraceletInstructions.INSTRUCTIONS_ELECTRICITY_RETURN))
        {
            // TODO: 2018/11/28 解析电量数据
            String[] strs = BraceletInstructions.analysisData(string);
            int battery = Integer.valueOf(strs[4], 16);
            int state = Integer.valueOf(strs[5], 16);
            notifyCallback(null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    battery,
                    state,
                    null,
                    null);


        }else  if (string.toUpperCase().startsWith(BraceletInstructions.INSTRUCTIONS_HEAD+BraceletInstructions.INSTRUCTIONS_FIRMWARE_MSG)) {
//            byte[] b = new byte[6];
//            System.arraycopy(bytes, 4, b, 0, b.length);
//            setLog("固件版本号1:"+new String(bytes));

            byte[]  b = new byte[5];
            b[0] = bytes[5];
            b[1] = bytes[6];
            b[2] = bytes[7];
            b[3] = bytes[8];
            b[4] = bytes[9];
            String firmwareVersion = new String(b).toUpperCase();
//            setLog("固件版本号2:"+firmwareVersion);
            notifyCallback(firmwareVersion);

        }
//        else if(string.toUpperCase().startsWith(BraceletInstructions.INSTRUCTIONS_HEAD+BraceletInstructions.INSTRUCTIONS_DZ800_BPOXYHEART_HISTORY))
//        {
//
//        }
        
    }

    /**处理心率血压血氧历史数据
     * @param string 数据
     */
    private void handlerBPHeartOxyHistoryData(final String string)
    {
        setLog("处理心率血压血氧历史数据");
        mHandlerCallback.post(new Runnable() {
            @Override
            public void run() {
                try {
                    String[] data = BraceletInstructions.analysisData(string);//数据分割
                    int len = Integer.valueOf(data[4], 16);//数据的条数
                    if (len  <=  0) {
                        return;
                    }
                    String[][] datas = BraceletInstructions.getMeasureData(data, 4, len, 12);//数据细分为条数
                    ArrayList<HeartData> heartDatas = new ArrayList<>();
                    ArrayList<BloodPressure> bloodPressures = new ArrayList<>();
                    ArrayList<OxyData> oxyDatas = new ArrayList<>();

                    for (String[] strings : datas)//循环
                    {
                        String dateTime = BraceletInstructions.getTime(strings,  1, 2,0);//取得测量时间

                        int h = Integer.valueOf(strings[3], 16), m = Integer.valueOf(strings[4], 16);//取得时间
                        String time = (h < 10 ? "0" + h : h + "") + ":" + (m < 10 ? "0" + m : m + "");//时间优化显示
//                  12 0b 06 07 28   46 00 	4a 00 	7d 00	00
                        int heart = Integer.valueOf(strings[6] + strings[5], 16);//心率
                        int bpSystolic = Integer.valueOf(strings[10] + strings[9], 16);
                        ;//收缩压//s收缩压90-140，d舒张压为60-90
                        int bpDiastole = Integer.valueOf(strings[8] + strings[7], 16);//舒张压
                        int oxy = Integer.valueOf(strings[8] + strings[7], 16);//血氧
                        if (heart > 30) {
                            heartDatas.add(new HeartData(dateTime+" "+time,heart));
                         }
                        if (bpSystolic > 30 && bpDiastole > 30) {
                            bloodPressures.add(new BloodPressure(dateTime+" "+time,bpSystolic,bpDiastole));
                        }
                        if (oxy > 80) {
                            oxyDatas.add(new OxyData(dateTime+" "+time,oxy));

                        }



                    }
                    if(heartDatas.size()>0 || bloodPressures.size()>0 || oxyDatas.size()>0)
                    {
                        notifyCallback(null,
                                null,
                                null,
                                null,
                                null,
                                heartDatas,
                                oxyDatas,
                                bloodPressures,
                                null,
                                null,
                                -1,
                                null,
                                null);
                    }

                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**处理运动历史数据
     * @param string 数据
     */
    private void handlerMotionHistoryData(final String string)
    {
        setLog("处理运动历史数据");
        mHandlerCallback.post(new Runnable() {
            @Override
            public void run() {
                try {
                    String[] data = BraceletInstructions.analysisData(string);//数据分割
                    int len = Integer.valueOf(data[4], 16);//数据的条数
                    if (len <= 0) {
                        return;
                    }
                    String[][] datas = BraceletInstructions.getMeasureData(data, 4, len, 12);//数据细分为条数
                    ArrayList<StepData> stepDatas = new ArrayList<>();
                    for (String[] strings : datas)//循环
                    {
                        String dateTime = BraceletInstructions.getTime(strings, 2, 3, 0, 1);//取得测量时间
                        int step = Integer.valueOf(strings[7] + strings[6] + strings[5] + strings[4], 16);//步数
                        double kcal = Integer.valueOf(strings[9] + strings[8], 16);//卡路里
                        double km = Integer.valueOf(strings[11] + strings[10], 16) / 1000.0;//运动距离
                        km = Math.round(km * 100) * 0.01d;
                        stepDatas.add(new StepData(dateTime,step,kcal,km,-1));
                    }
                    if(stepDatas.size()>0)
                    {
                        notifyCallback(null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                stepDatas,
                                null,
                                -1,
                                null,
                                null);
                    }
                }catch (Exception e){e.printStackTrace();}
            }
        });
    }
    /**通知监听此数据的监听对象
     * @param heart 实时心率
     * @param step  实时运动
     * @param oxy   实时血氧
     * @param bloodPressure 实时血压
     * @param sleepData 实时睡眠
     * @param hearts    心率历史
     * @param oxys  血氧历史
     * @param bloodPressures    血压历史
     * @param steps 运动历史
     * @param electricQuantity  电量
     * @param state 电量状态
     * @param bindSuccess   绑定成功或失败
     * @param loginSuccess  登录成功或失败
     */
    private void  notifyCallback(
            HeartData heart,
            StepData step,
            OxyData oxy,
            BloodPressure bloodPressure,
            SleepData sleepData,
            ArrayList<HeartData> hearts,
            ArrayList<OxyData> oxys,
            ArrayList<BloodPressure> bloodPressures,
            ArrayList<StepData> steps,
            Integer electricQuantity,
            int state,
            final Boolean bindSuccess,
            final Boolean loginSuccess


            )
    {
        if(getOnNotifyAndReadCallback() == null && !(getOnNotifyAndReadCallback() instanceof BraceletDataCallback))
        {
            setLog("未找到监听数据解析的回调");
            return;
        }
        if(mBraceletDataCallback == null)
        {
            mBraceletDataCallback = (BraceletDataCallback) getOnNotifyAndReadCallback();
        }



        if(heart!=null){
            mBraceletDataCallback.onHeart( heart);
        }

        if(step!=null){
            mBraceletDataCallback.onStep( step);
         }

        if(oxy!=null){
            mBraceletDataCallback.onOXY( oxy);
        }

        if(bloodPressure!=null){
            mBraceletDataCallback.onBloodPressure( bloodPressure);
        }

        if(sleepData!=null){
            mBraceletDataCallback.onSleep( sleepData);
        }

        if(hearts != null){
            mBraceletDataCallback.onHeartOXYBPHistory( hearts, oxys,  bloodPressures);
         }

        if(steps!=null){
            mBraceletDataCallback.onStepHistory( steps) ;
        }


        if(electricQuantity!=null){
            mBraceletDataCallback.onElectricQuantity( electricQuantity,  state) ;
        }


        if(bindSuccess!=null){
            mBraceletDataCallback.onBindDeviceState(bindSuccess) ;
        }


        if(loginSuccess!=null){
            mBraceletDataCallback.onLoginDeviceState(loginSuccess) ;
        }


    }

    /**通知监听此数据的监听对象
     * @param version 手环软件版本信息
     */
    private void  notifyCallback(
          String version

    ) {

        if (getOnNotifyAndReadCallback() == null && !(getOnNotifyAndReadCallback() instanceof BraceletDataCallback)) {
            setLog("未找到监听数据解析的回调");
            return;
        }
        if (mBraceletDataCallback == null) {
            mBraceletDataCallback = (BraceletDataCallback) getOnNotifyAndReadCallback();
        }
        if(version != null)
        {
            mBraceletDataCallback.onVersion(version);
        }
    }

    @Override
    public void notifyConnectState(boolean isConnect) {
        this.isConnect = isConnect;
        if(isConnect)
        {

        }
    }
}
