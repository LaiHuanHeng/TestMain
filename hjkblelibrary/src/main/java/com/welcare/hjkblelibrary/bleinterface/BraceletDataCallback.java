package com.welcare.hjkblelibrary.bleinterface;

import com.vise.baseble.callback.IBleCallback;
import com.welcare.hjkblelibrary.entity.BloodPressure;
import com.welcare.hjkblelibrary.entity.HeartData;
import com.welcare.hjkblelibrary.entity.OxyData;
import com.welcare.hjkblelibrary.entity.SleepData;
import com.welcare.hjkblelibrary.entity.StepData;

import java.util.ArrayList;

/**
 * Created by admin on 2018/11/27.
 * 设备数据回调接口
 */

public interface BraceletDataCallback extends IBleCallback {
    /**绑定设备
     * @param  success true 成功 false 失败
     */
    void onBindDeviceState(boolean success);

    /**登录设备
     * @param success true 成功 false 失败
     */
    void onLoginDeviceState(boolean success);

    /**心率数据监听
     * @param heart 心率
     */
     void onHeart(HeartData heart);

    /**运动数据监听
     * @param step 运动数据
      */
     void onStep(StepData step);

    /**血氧数据监听
     * @param oxy 血氧数据
     */
     void onOXY(OxyData oxy);

    /**血压数据监听
     * @param bloodPressure 血压数据
     */
     void onBloodPressure(BloodPressure bloodPressure);

    /**睡眠数据监听
     * @param sleepData
     */
     void onSleep(SleepData sleepData);

    /**心率血氧血压历史数据
     * @param hearts 心率历史数据集合
     * @param oxys  血氧历史数据集合
     * @param bloodPressures 血压历史数据集合
     */
     void onHeartOXYBPHistory(ArrayList<HeartData> hearts,ArrayList<OxyData> oxys,ArrayList<BloodPressure> bloodPressures);

    /**运动历史数据
     * @param steps 运动数据集合

     */
    void onStepHistory(ArrayList<StepData> steps);

    /**手环电量返回 DZ800需要主动获取,QS80 10分钟自动上传一次
     * @param electricQuantity 当前电量 带我%
     * @param state 充电状态 0: 未充电 1: 充电完成 2: 正在充电

     */
    void onElectricQuantity(int electricQuantity ,int state);



    /**手环软件版本号
     * @param version
     */
    void onVersion(String version);


}
