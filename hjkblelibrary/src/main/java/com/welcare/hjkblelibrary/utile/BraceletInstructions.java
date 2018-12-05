package com.welcare.hjkblelibrary.utile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;


import com.welcare.hjkblelibrary.entity.AlarmClockRemind;
import com.welcare.hjkblelibrary.utile.hex.DecoderException;
import com.welcare.hjkblelibrary.utile.hex.Hex;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by admin on 2018/11/29.
 * 手环指令API
 */
public class BraceletInstructions {

    private static final String TAG = "BraceletInstructions";
    /**
     * 包头-设备识别
     */
    public static final String INSTRUCTIONS_HEAD = "A9";
    /**
     * 时间设置	0x01
     **/
    public static final String INSTRUCTIONS_TIME = "01";
    /**
     * 闹钟设置	0x02
     */
    public static final String INSTRUCTIONS_ALARM_CLOCK = "02";
    /***设备绑定命令	0x32*/
    public static final String INSTRUCTIONS_BINDING = "32";
    /***设备绑定应答	0x33*/
    public static final String INSTRUCTIONS_BINDING_RETURN = "33";
    /***登入请求	0x34*/
    public static final String INSTRUCTIONS_LOGIN = "34";
    /***登入响应	0x35*/
    public static final String INSTRUCTIONS_LOGIN_RETURN = "35";
    /***解除绑定命令	0x0C*/
    public static final String INSTRUCTIONS_UNBUNDING = "0C";
    /***解除绑定回应	0x0D*/
    public static final String INSTRUCTIONS_UNBUNDING_RETURN = "0D";
    /***运动目标设置	0x03*/
    public static final String INSTRUCTIONS_MOVING_TARGET = "03";
    /***用户信息设置	0x04*/
    public static final String INSTRUCTIONS_USER_INFO = "04";
    /***防丢设置	0x05*/
    public static final String INSTRUCTIONS_ANTI_LOST = "05";
    /***	久坐设置	0x06*/
    public static final String INSTRUCTIONS_SEDENTARY = "06";
    /***自动睡眠设置	0x07*/
    public static final String INSTRUCTIONS_AUTO_SLEEP = "07";
    /***设备电量请求	0x08*/
    public static final String INSTRUCTIONS_ELECTRICITY = "08";
    /***设备电量返回	0x09*/
    public static final String INSTRUCTIONS_ELECTRICITY_RETURN = "09";
    /***系统用户设置	0x0A*/
    public static final String INSTRUCTIONS_SYSTEM_USER = "0A";
    /***天气推送	0x0B*/
    public static final String INSTRUCTIONS_WEATHER_PUSH = "0B";
    /***查找手环	0x0E*/
    public static final String INSTRUCTIONS_FIND_BRACELET = "0E";
    /***	远程控制	0x0F*/
    public static final String INSTRUCTIONS_REMOTE_CONTROL = "0F";
    /***来电提醒	0x10*/
    public static final String INSTRUCTIONS_CALL = "10";
    /***短信提醒	0x11*/
    public static final String INSTRUCTIONS_SHORT_MESSAGE = "11";
    /***QQ提醒	0x12*/
    public static final String INSTRUCTIONS_QQ = "12";
    /***微信提醒	0x13*/
    public static final String INSTRUCTIONS_WECHAT = "13";
    /**
     * 推送设置	0x14
     */
    public static final String INSTRUCTIONS_PUSH_SET = "14";
    /***勿扰模式	0x15*/
    public static final String INSTRUCTIONS_DO_NOT_DISTURD = "15";
    /***提醒模式	0x16*/
    public static final String INSTRUCTIONS_REMIND = "16";
    /***手势智控	0x17*/
    public static final String INSTRUCTIONS_GESTURE_CONTROL = "17";
    /***配置信息同步	0x18*/
    public static final String INSTRUCTIONS_CONFIG_MSG_SYNC = "18";
    /***推送消息	0x19*/
    public static final String INSTRUCTIONS_PUSH_MSG = "19";
    /***手机语言更新	0x1A*/
    public static final String INSTRUCTIONS_LANGUAGE_UPDATE = "1A";
    /***APP同步气压紫外线温度	0x1B*/
    public static final String INSTRUCTIONS_ENVIRONMENTEL_SYNC = "1B";
    /***拍照开关	0x1C*/
    public static final String INSTRUCTIONS_CAMERA_SWITCH = "1C";
    /***固件升级启动	0x1D*/
    public static final String INSTRUCTIONS_FIRMWARE_UP_STARTS = "1D";
    /***固件升级回应	0x1E*/
    public static final String INSTRUCTIONS_FIRMWARE_UP_RETURN = "1E";
    /***固件升级状态	0x1F*/
    public static final String INSTRUCTIONS_FIRMWARE_UP_STATE = "1F";
    /***运动数据请求	0x20*/
    public static final String INSTRUCTIONS_MOTION = "20";
    /***	运动数据返回	0x21*/
    public static final String INSTRUCTIONS_MOTION_RETURN = "21";
    /***睡眠数据请求	0x22*/
    public static final String INSTRUCTIONS_SLEEP = "22";
    /***睡眠数据返回	0x23*/
    public static final String INSTRUCTIONS_SLEEP_RETURN = "23";
    /***运动数据同步设置	0x24*/
    public static final String INSTRUCTIONS_MOTION_SYNC_SET = "24";
    /***历史数据同步指示	0x25*/
    public static final String INSTRUCTIONS_HOISTORY_MOTION_SYNC = "25";
    /***心率数据请求	0x26*/
    public static final String INSTRUCTIONS_HEART_RATE = "26";
    /***心率数据返回	0x27*/
    public static final String INSTRUCTIONS_HEART_RATE_RETURN = "27";
    /***气压数据请求	0x28*/
    public static final String INSTRUCTIONS_PRESSURE = "28";
    /***气压数据返回	0x29*/
    public static final String INSTRUCTIONS_PRESSURE_RETURN = "29";
    /***紫外线数据请求	0x2A*/
    public static final String INSTRUCTIONS_ULTRAVIOLET_RAYS = "2A";
    /***紫外线数据返回	0x2B*/
    public static final String INSTRUCTIONS_ULTRAVIOLET_RAYS_RETURN = "2B";
    /***自动测试心率	0x2C*/
    public static final String INSTRUCTIONS_AUTO_HEART_RATE = "2C";
    /***固件升级命令	0x30*/
    public static final String INSTRUCTIONS_FIRMWARE_UP = "30";
    /***固件版本信息	0x31*/
    public static final String INSTRUCTIONS_FIRMWARE_MSG = "31";
    /***实时同步数据	0x36*/
    public static final String INSTRUCTIONS_SYNC_DATA = "36";
    /***当天运动校准	0x37*/
    public static final String INSTRUCTIONS_DAY_MOTION = "37";
    /***断开蓝牙指示	0x38*/
    public static final String INSTRUCTIONS_DISCONNECT_BLE = "38";
    /***横竖显示 	0x39*/
    public static final String INSTRUCTIONS_ANYWAY_DISPLAY = "39";
    /***喝水提醒设置	0x3a*/
    public static final String INSTRUCTIONS_DRINK_WATER = "3A";
    /***屏幕测试	0xf0*/
    public static final String INSTRUCTIONS_SCREEN_TEST = "F0";
    /***马达测试	0xf1*/
    public static final String INSTRUCTIONS_MOTOR_TEST = "F1";
    /***计步器测试	0xf2*/
    public static final String INSTRUCTIONS_PEDOMETER_TEST = "F2";
    /***心率测试	0xf3*/
    public static final String INSTRUCTIONS_HEART_RATE_TEST = "F3";
    /***Flahs测试	0xf4*/
    public static final String INSTRUCTIONS_FLAHS_TEST = "F4";
    /***关机命令	0xf5*/
    public static final String INSTRUCTIONS_POWER_CLOSE = "F5";
    /***解绑命令	0xf6*/
    public static final String INSTRUCTIONS_UNBUNDING_SYSTEM = "F6";
    /***重启命令	0xf7*/
    public static final String INSTRUCTIONS_RESTART = "F7";
    /***请求睡眠汇总数据发送到厂商服务器	0x44*/
    public static final String INSTRUCTIONS_SEND_SLEEP_DATA = "44";

    /***请求手环主动间隔15s发运动汇总数据到厂商服务器	0x48*/
    public static final String INSTRUCTIONS_SEND_SLEEP_DATA_TIME = "48";
    /***连接微信成功回应	0xC0*/
    public static final String INSTRUCTIONS_CONNECT_WECHAT = "C0";
    /***发送运动历史数据到微信	0xA8*/
    public static final String INSTRUCTIONS_SEND_HISTORY_MOTION_WECHAT = "A8";

    /***发送金币数据到手环	0xB0*/
    public static final String INSTRUCTIONS_SEND_GOLD_COIN = "B0";
    /***DZ800运动历史数据请求	0xB1*/
    public static final String INSTRUCTIONS_DZ800_MOTION_HISTORY_REQUEST = "B1";
    /***DZ800运动历史的数据	0xB2*/
    public static final String INSTRUCTIONS_DZ800_MOTION_HISTORY = "B2";
    /***DZ800运动历史数据接收成功 0xB3*/
    public static final String INSTRUCTIONS_DZ800_MOTION_HISTORY_SUCCESS = "B3";
    /***DZ800 心率血压血氧历史请求	0xB4*/
    public static final String INSTRUCTIONS_DZ800_BPOXYHEART_HISTORY_REQUEST = "B4";
    /***DZ800 心率血压血氧历史数据	0xB5*/
    public static final String INSTRUCTIONS_DZ800_BPOXYHEART_HISTORY = "B5";
    /***DZ800 心率血压血氧历史数据接收成功	0xB6*/
    public static final String INSTRUCTIONS_DZ800_BPOXYHEART_HISTORY_SUCCESS = "B6";
    /***DZ800 HBC模式设置	0xB7*/
    public static final String INSTRUCTIONS_DZ800_HBC = "B7";


    /***实时同步数据-关闭*/
    public static final String REAL_TIME_SYNC_CLOSE = "00";
    /***实时同步数据-运动*/
    public static final String REAL_TIME_SYNC_MOTION = "01";
    /***实时同步数据-睡眠*/
    public static final String REAL_TIME_SYNC_SLEEP = "02";
    /***实时同步数据-心率*/
    public static final String REAL_TIME_SYNC_HEART_RATE = "03";
    /***实时同步数据-气压*/
    public static final String REAL_TIME_SYNC_PRESSURE = "04";


    /**
     * 获取绑定运动手环的命令
     *
     * @param id  自定义ID
     * @param mac 设备mac
     * @return
     */
    public static String getBindingInstructions(String id, String mac) {
        return getBindingAndLoginBracelet(id, mac, INSTRUCTIONS_BINDING);
    }

    /**
     * 获取登入运动手环的命令
     *
     * @param id  自定义ID
     * @param mac 设备mac
     * @return
     */
    public static String getLoginInstructions(String id, String mac) {
        return getBindingAndLoginBracelet(id, mac, INSTRUCTIONS_LOGIN);
    }

    /**
     * 获取绑定/登入运动手环的命令
     *
     * @param id  自定义ID
     * @param mac 设备mac
     * @return
     */
    private static String getBindingAndLoginBracelet(String id, String mac, String instructions) {
        StringBuffer str = new StringBuffer();
        str.append(INSTRUCTIONS_HEAD);
        str.append(instructions);
        str.append("00");
        str.append("0C");
        if (id != null) {
            if (id.length() >= 12) {
                if (id.length() == 12) {
                    str.append(id);
                } else {
                    str.append(id.substring(0, 12));
                }
            } else {
                int len = id.length();
                for (int i = len; i < 12; i++) {
                    str.append("0");
                }
                str.append(id);
            }

        } else {
            str.append("000000000000");
        }
        if (mac != null) {
            if (mac.indexOf(":") > -1) {
                String[] m = mac.split(":");
                if (m.length > 5) {
                    str.append(m[0] + m[1] + m[2] + m[3] + m[4]);
                } else {
                    str.append("0000000000");
                }
            } else {
                str.append("0000000000");
            }

        } else {
            str.append("0000000000");
        }
        str.append("AD");
        str.append(calculationCRC(str.toString()));

        LogUtile.e(TAG, "校对一下:" + proofreadingCRC(str.toString()));
        return str.toString();
    }

    /**
     * 获取同步手环时间命令
     *
     * @return 同步时间命令
     */
    public static String getTimeSynsInstructions() {
        StringBuffer str = new StringBuffer(INSTRUCTIONS_HEAD);
        str.append(INSTRUCTIONS_TIME);
        str.append("00");
        str.append("06");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] times = sdf.format(new Date()).split(" ");
        String[] date = times[0].split("-");
        String[] time = times[1].split(":");


        str.append(hex10_16(date[0].substring(2)) + hex10_16(date[1]) + hex10_16(date[2]));
        str.append(hex10_16(time[0]) + hex10_16(time[1]) + hex10_16(time[2]));

//		str.append(time[0]+time[1]+time[2]);
//		str.append("171215");
//		str.append("010203");
//		str.append("00");
        str.append(calculationCRC(str.toString()));
        proofreadingCRC(str.toString());
        return str.toString();
    }

    /**
     * 获取用户信息设置命令 目前不支持此项
     *
     * @return
     */
    public static String getUserInfoInstructions(int sex, int age, int height, int weight) {
        StringBuffer str = new StringBuffer(INSTRUCTIONS_HEAD);
        str.append(INSTRUCTIONS_USER_INFO);
        str.append("00");
        str.append("08");
        str.append(hex10_16(sex + ""));
        str.append(hex10_16(age + ""));
        str.append(hex10_16(height + ""));
        str.append(hex10_16(weight + ""));
        str.append("27100000");

        str.append(calculationCRC(str.toString()));
        proofreadingCRC(str.toString());
        return str.toString();
    }

    /**
     * 获取用户信息设置命令 目前不支持此项
     *
     * @return
     */
    public static String getDZUserInfoInstructions(int sex, int age, int height, int weight) {
        StringBuffer str = new StringBuffer(INSTRUCTIONS_HEAD);
        str.append(INSTRUCTIONS_USER_INFO);
        str.append("00");
        str.append("04");
        str.append(hex10_16(sex + ""));
        str.append(hex10_16(age + ""));
        str.append(hex10_16(height + ""));
        str.append(hex10_16(weight + ""));
        str.append(calculationCRC(str.toString()));
        proofreadingCRC(str.toString());
        return str.toString();
    }

    /**
     * 获取解除绑定命令
     *
     * @return
     */
    public static String getUnbundingInstructions() {
        return getZeroLenthInstructions(INSTRUCTIONS_UNBUNDING);
    }

    /**
     * 获取电量命令
     *
     * @return
     */
    public static String getElectricityInstructions() {
        return getZeroLenthInstructions(INSTRUCTIONS_ELECTRICITY);
    }

    /**
     * 查找手环命令
     *
     * @return
     */
    public static String getFindBraceletInstructions() {
        return getZeroLenthInstructions(INSTRUCTIONS_FIND_BRACELET);
    }

    /**
     * 获取运动数据命令
     *
     * @return
     */
    public static String getMotionInstructions() {
        return getZeroLenthInstructions(INSTRUCTIONS_MOTION);
    }

    /**
     * 接收成功或者失败运动数据命令
     *
     * @param state 成功/失败
     * @return
     */
    public static String getMotionStateInstructions(boolean state) {
        String s = "00";
        if (!state) {
            s = "01";
        }
        return getOneLenthInstructions(INSTRUCTIONS_MOTION, s);
    }

    /**
     * 运动数据同步设置命令
     *
     * @param state 开/关
     * @return
     */
    public static String getSetMotionSyncInstructions(boolean state) {
        String s = "01";
        if (!state) {
            s = "00";
        }
        return getOneLenthInstructions(INSTRUCTIONS_MOTION_SYNC_SET, s);
    }


    /**
     * 睡眠数据接收命令
     *
     * @return
     */
    public static String getSleepInstructions() {
        return getZeroLenthInstructions(INSTRUCTIONS_SLEEP);
    }

    /**
     * 睡眠数据接收成功或者失败命令
     *
     * @param state 成功/失败
     * @return
     */
    public static String getSleepInstructions(boolean state) {
        String s = "00";
        if (!state) {
            s = "01";
        }
        return getOneLenthInstructions(INSTRUCTIONS_SLEEP, s);
    }

    /**
     * 心率数据接收命令
     *
     * @return
     */
    public static String getHeartRateInstructions() {
        return getZeroLenthInstructions(INSTRUCTIONS_HEART_RATE);
    }

    /**
     * 心率数据接收成功或者失败命令
     *
     * @param state 成功/失败
     * @return
     */
    public static String getHeartRateInstructions(boolean state) {
        String s = "00";
        if (!state) {
            s = "01";
        }
        return getOneLenthInstructions(INSTRUCTIONS_HEART_RATE, s);
    }

    /**
     * 气压数据接收命令 目前不支持此项
     *
     * @return
     */
    public static String getPressureInstructions() {
        return getZeroLenthInstructions(INSTRUCTIONS_PRESSURE);
    }

    /**
     * 气压数据接收成功或者失败命令 目前不支持此项
     *
     * @param state
     * @return
     */
    public static String getPressureInstructions(boolean state) {
        String s = "00";
        if (!state) {
            s = "01";
        }
        return getOneLenthInstructions(INSTRUCTIONS_PRESSURE, s);
    }

    /**
     * 紫外线数据接收命令 目前不支持此项
     *
     * @return
     */
    public static String getUltravioletRaysInstructions() {
        return getZeroLenthInstructions(INSTRUCTIONS_ULTRAVIOLET_RAYS);
    }

    /**
     * 紫外线数据接收成功或失败命令 目前不支持此项
     *
     * @param state 成功/失败
     * @return
     */
    public static String getUltravioletRaysInstructions(boolean state) {
        String s = "00";
        if (!state) {
            s = "01";
        }
        return getOneLenthInstructions(INSTRUCTIONS_ULTRAVIOLET_RAYS, s);
    }

    /**
     * 开启/关闭实时同步数据 DZ系列不支持
     *
     * @param type 同步的数据类型
     * @return
     */
    public static String getRealTimeSyncInstructions(String type) {
        String s = REAL_TIME_SYNC_CLOSE;
        if (type.equals(REAL_TIME_SYNC_HEART_RATE) || type.equals(REAL_TIME_SYNC_MOTION)
                || type.equals(REAL_TIME_SYNC_PRESSURE) || type.equals(REAL_TIME_SYNC_SLEEP)) {
            s = type;
        }
        return getOneLenthInstructions(INSTRUCTIONS_SYNC_DATA, s);
    }

    /**
     * 断开蓝牙指令 DZ系列不支持
     *
     * @return
     */
    public static String getDisconnectBleInstructions() {
        return getZeroLenthInstructions(INSTRUCTIONS_DISCONNECT_BLE);
    }

    /**
     * 横竖屏显示指令 DZ系列不支持
     *
     * @param state true 横  false 竖
     * @return
     */
    public static String getAnywayDisolayInstructions(boolean state) {
        String s = "00";
        if (!state) {
            s = "01";
        }
        return getOneLenthInstructions(INSTRUCTIONS_ANYWAY_DISPLAY, s);
    }

    /**
     * 开关睡眠指令
     *
     * @param state true 开 false 关
     * @return
     */
    public static String getAutoSleepInstructions(boolean state) {
        String s = "01";
        if (!state) {
            s = "00";
        }
        return getOneLenthInstructions(INSTRUCTIONS_AUTO_SLEEP, s);
    }

    /**
     * 开关HBC模式指令 只支持DZ800
     *
     * @param state true 开 false 关
     * @return
     */
    public static String getDZ800HBCInstructions(boolean state) {
        String s = "01";
        if (!state) {
            s = "00";
        }
        return getOneLenthInstructions(INSTRUCTIONS_DZ800_HBC, s);
    }

    /**
     * 屏幕测试 DZ系列不支持
     *
     * @param state 开/关
     * @return
     */
    public static String getScreenTestInstructions(boolean state) {
        String s = "01";
        if (!state) {
            s = "00";
        }
        return getOneLenthInstructions(INSTRUCTIONS_SCREEN_TEST, s);
    }

    /**
     * 马达测试 DZ系列不支持
     *
     * @param state 开/关
     * @return
     */
    public static String getMotorTestInstructions(boolean state) {
        String s = "01";
        if (!state) {
            s = "00";
        }
        return getOneLenthInstructions(INSTRUCTIONS_MOTOR_TEST, s);
    }

    /**
     * 计步器测试 DZ系列不支持
     *
     * @param state 开/关
     * @return
     */
    public static String getPedometerTestInstructions(boolean state) {
        String s = "01";
        if (!state) {
            s = "00";
        }
        return getOneLenthInstructions(INSTRUCTIONS_PEDOMETER_TEST, s);
    }

    /**
     * 心率测试 DZ系列不支持
     *
     * @param state 开/关
     * @return
     */
    public static String getHeartRateTestInstructions(boolean state) {
        String s = "01";
        if (!state) {
            s = "00";
        }
        return getOneLenthInstructions(INSTRUCTIONS_HEART_RATE_TEST, s);
    }

    /**
     * 关机测试 DZ系列不支持
     *
     * @return
     */
    public static String getPowerCloseInstructions() {
        return getZeroLenthInstructions(INSTRUCTIONS_POWER_CLOSE);
    }

    /**
     * 强制解绑测试 DZ系列不支持
     *
     * @return
     */
    public static String getUnbundingTestInstructions() {
        return getZeroLenthInstructions(INSTRUCTIONS_UNBUNDING_SYSTEM);
    }

    /**
     * 重启测试 DZ系列不支持
     *
     * @return
     */
    public static String getRestartInstructions() {
        return getZeroLenthInstructions(INSTRUCTIONS_RESTART);
    }

    /**
     * 设置语言  目前不支持此项
     *
     * @param state 中文/非中文
     **/
    public static String getLanguageInstructions(boolean state) {
        String s = "00";
        if (!state) {
            s = "01";
        }
        return getOneLenthInstructions(INSTRUCTIONS_LANGUAGE_UPDATE, s);
    }

    /**
     * 运动数据同步设置
     *
     * @param state 同步/不同步
     **/
    public static String getMotionSynsSetInstructions(boolean state) {
        String s = "01";
        if (!state) {
            s = "00";
        }
        return getOneLenthInstructions(INSTRUCTIONS_MOTION_SYNC_SET, s);
    }

    /**
     * 拍照开关
     *
     * @param state 拍照开/拍照关
     **/
    public static String getMotionCameraSwitchInstructions(boolean state) {
        String s = "01";
        if (!state) {
            s = "00";
        }
        return getOneLenthInstructions(INSTRUCTIONS_CAMERA_SWITCH, s);
    }
//	INSTRUCTIONS_AUTO_HEART_RATE

    /**
     * 获取设置自动测量心率命令 目前不支持此项
     *
     * @param open     开启或者关闭
     * @param dates    开始时间和结束时间 默认为当前时间和当前时间后一分钟
     * @param interval 自动测量时间间隔 分钟
     * @return自动测量心率命令
     */
    public static String getSetAutoHeartRateInstructions(boolean open, int interval, Date... dates) {
        StringBuffer str = new StringBuffer(INSTRUCTIONS_HEAD);
        str.append(INSTRUCTIONS_AUTO_HEART_RATE);
        str.append("0006");
        if (open) {
            str.append("01");
        } else {
            str.append("00");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (dates != null && dates.length >= 2) {
            String[] times = sdf.format(dates[0]).split(" ");
            String[] time = times[1].split(":");
            str.append(hex10_16(time[0]) + hex10_16(time[1]));
            String[] times2 = sdf.format(dates[1]).split(" ");
            String[] time2 = times2[1].split(":");
            str.append(hex10_16(time2[0]) + hex10_16(time2[1]));
        } else {
            String[] times = sdf.format(new Date()).split(" ");
            String[] time = times[1].split(":");
            int i = Integer.parseInt(time[1]) + 2;
            str.append(hex10_16(time[0]) + hex10_16(i + "") + hex10_16(time[0]) + hex10_16((i + 1) + ""));
        }
        if (interval % 10 != 0) {
            interval = 10;
        }
        str.append("" + interval);
        str.append(calculationCRC(str.toString()));
        proofreadingCRC(str.toString());
        return str.toString();
    }

    /**
     * 获取升级固件命令  目前不支持此项
     *
     * @param datas 命令
     * @return
     */
    public static String getFirmwareUpStatrtsInstructions(byte[] datas) {
        if (datas == null) {
            return "";
        }
        StringBuffer str = new StringBuffer(INSTRUCTIONS_HEAD);
        str.append(INSTRUCTIONS_FIRMWARE_UP_STARTS);
        str.append("00");
        str.append(hex10_16(datas.length + ""));
        String s = calculationCRC(str.toString(), datas);
        LogUtile.i(TAG, s + " ");
        str.append(new String(Hex.encodeHex(datas)));
        str.append(s);

        proofreadingCRC(str.toString());
        return str.toString();
    }


    /**
     * 获取取消升级固件命令  目前不支持此项
     *
     * @return
     */
    public static String getFirmwareUpCancelInstructions() {
        return getOneLenthInstructions(INSTRUCTIONS_FIRMWARE_UP_STATE, "01");
    }

    /**
     * 获取取消升级固件命令  目前不支持此项
     *
     * @return
     */
    public static String getFirmwareMsgInstructions() {
        return getZeroLenthInstructions(INSTRUCTIONS_FIRMWARE_MSG);
    }


    /**
     * 获取接收到短信然后震动的指令
     *
     * @param open 开/关
     * @return
     */
    public static String getSMSRemindInstructions(boolean open) {
        String s = "00";
        if (open) {
            s = "01";
        }
        return getOneLenthInstructions(INSTRUCTIONS_SHORT_MESSAGE, s);
    }

    /**
     * 获取接收到来电然后震动的指令
     *
     * @param open 开/关
     * @return
     */
    public static String getCallInstructions(boolean open) {
        String s = "00";
        if (open) {
            s = "01";
        }
        return getOneLenthInstructions(INSTRUCTIONS_CALL, s);
    }

    /**
     * 获取防丢设置指令
     *
     * @param alertType  警报类型 1: 连接断开  2: 超出3米  3: 超出5米
     * @param alertLevel 0: 不警报	1: 低档警报		2: 中档警报		3: 高档警报
     * @return
     */
    public static String getAntiLostInstructions(String alertType, String alertLevel) {
        return getNLenthInstructions(INSTRUCTIONS_ANTI_LOST, alertType, alertLevel);
    }


    /**
     * 获取设置久坐提醒命令 默认为开始时间为9点至晚上20点
     *
     * @return
     */
    public static String getSedentaryInstructions(boolean open) {
//		Byte4	Byte5	Byte6	Byte7	Byte8	Byte9	Byte10	Byte11	Byte12
//		长度：8	久坐
//		使能	开始时间	结束时间	重复	久坐时间	久坐阀值
//		重复说明	Bit0--Bit6分别表示周一到周日的重复设置，所有位都为0时，
//		表示只提醒一次
//		0 ：表示不重复
//		1 ：表示重复
        String s = "01";
        if (!open) {
            s = "00";
        }

        return getNLenthInstructions(INSTRUCTIONS_SEDENTARY, s, "09", "14", "7F", "78", "00", "01", "01");
    }

    /**
     * 获取设置久坐提醒命令
     *
     * @param open      是否开启
     * @param startHour 开启时间
     * @param endHour   结束时间
     * @param repeats   重复日期 true为开false为关.全false为不重复 0 星期一 1 星期二 2 星期三 3 星期四 4 星期五 5 星期六 6 星期天   只有一个长度则重复
     * @param time      久坐阀值 30-300分钟
     * @return
     */
    public static String getSedentaryInstructions(boolean open, int startHour, int endHour, boolean[] repeats,int time) {
        if(startHour < 0)
        {
            startHour = 0;
        }
        if(startHour>23)
        {
            startHour = 23;
        }
        if(endHour < 0)
        {
            endHour = 0;
        }
        if(endHour>23)
        {
            endHour = 23;
        }
        if(time<30 || time>300)
        {
            time = 60;
        }
        String[] times = getByteDataSort(time,2);

//		Byte4	Byte5	Byte6	Byte7	Byte8	Byte9	Byte10	Byte11	Byte12
//		长度：8	久坐
//		使能	开始时间	结束时间	重复	久坐时间	久坐阀值
//		重复说明	Bit0--Bit6分别表示周一到周日的重复设置，所有位都为0时，
//		表示只提醒一次
//		0 ：表示不重复
//		1 ：表示重复
        String s = "01";
        if (!open) {
            s = "00";
        }
        String sH =hex10_16(startHour+"");
        String eH =hex10_16(endHour+"");
        StringBuffer repeat = new StringBuffer("0");
        if(repeats.length!=1) {
            boolean[] rs = new boolean[7];
            if (repeats.length == 7) {
                rs = repeats;
            } else {
                if (repeats.length < 7) {
                    System.arraycopy(repeats, 0, rs, 0, repeats.length);
                } else {
                    System.arraycopy(repeats, 0, rs, 0, rs.length);
                }
            }
            for (int i = rs.length - 1; i > -1; i--) {
                if (rs[i]) {
                    repeat.append("1");
                } else {
                    repeat.append("0");
                }
            }
        }else
         {
             repeat.append("0000001");
         }
        return getNLenthInstructions(INSTRUCTIONS_SEDENTARY, s, sH, eH, bitTo16(repeat.toString()), times[0], times[1], "01", "01");
    }

    /**
     * 获取设置左手抬手亮屏指令
     *
     * @return
     */
    public static String getLeftGestureControlInstructions(boolean gestureControlOpen, boolean writhingOpen) {
//		Byte4	Byte5
//		长度：1	手势亮屏状态
//		Bit0:  0-左手  1-右手
//		Bit1:  0-抬手亮屏关闭   1-抬手亮屏开启
//		Bit2:  0-翻腕亮屏关闭   1-翻腕亮屏开启
        String s = "00";
        if (!gestureControlOpen && writhingOpen) {
            s = "04";
        }
        if (gestureControlOpen) {
            s = bitTo16("00000" + (writhingOpen ? "1" : "0") + "10");
        }

        return getOneLenthInstructions(INSTRUCTIONS_GESTURE_CONTROL, s);
    }

    /**
     * 获取设置左手翻腕亮屏指令
     *
     * @return
     */
    public static String getLeftWrithingBrightScreenInstructions(boolean writhingOpen, boolean gestureControlOpen) {
//		Byte4	Byte5
//		长度：1	手势亮屏状态
//		Bit0:  0-左手  1-右手
//		Bit1:  0-抬手亮屏关闭   1-抬手亮屏开启
//		Bit2:  0-翻腕亮屏关闭   1-翻腕亮屏开启
        String s = "00";
        if (!writhingOpen && gestureControlOpen) {
            s = "02";
        }
        if (writhingOpen) {
            s = bitTo16("000001" + (gestureControlOpen ? "1" : "0") + "0");
        }

        return getOneLenthInstructions(INSTRUCTIONS_GESTURE_CONTROL, s);
    }

    /**
     * 获取设置右手抬手亮屏指令
     *
     * @return
     */
    public static String getRightGestureControlInstructions(boolean gestureControlOpen, boolean writhingOpen) {
//		Byte4	Byte5
//		长度：1	手势亮屏状态
//		Bit0:  0-左手  1-右手
//		Bit1:  0-抬手亮屏关闭   1-抬手亮屏开启
//		Bit2:  0-翻腕亮屏关闭   1-翻腕亮屏开启
        String s = "00";
        if (!gestureControlOpen && writhingOpen) {
            s = "04";
        }
        if (gestureControlOpen) {
            s = bitTo16("00000" + (writhingOpen ? "1" : "0") + "11");
        }

        return getOneLenthInstructions(INSTRUCTIONS_GESTURE_CONTROL, s);
    }

    /**
     * 获取设置右手翻腕亮屏指令
     *
     * @return
     */
    public static String getRightWrithingBrightScreenInstructions(boolean writhingOpen, boolean gestureControlOpen) {
//		Byte4	Byte5
//		长度：1	手势亮屏状态
//		Bit0:  0-左手  1-右手
//		Bit1:  0-抬手亮屏关闭   1-抬手亮屏开启
//		Bit2:  0-翻腕亮屏关闭   1-翻腕亮屏开启
        String s = "00";
        if (!writhingOpen && gestureControlOpen) {
            s = "02";
        }
        if (writhingOpen) {
            s = bitTo16("000001" + (gestureControlOpen ? "1" : "0") + "1");
        }

        return getOneLenthInstructions(INSTRUCTIONS_GESTURE_CONTROL, s);
    }

    /**
     * 获取设置勿扰模式 当前的时间默认为00:00 - 23:59
     *
     * @return
     */
    public static String getDoNotDisturdInstructions(boolean open) {
//		Byte4	Byte5	Byte6	Byte7	Byte8	Byte9
//		长度：5	勿扰使能
//		1: 开启
//		0: 关闭
// 	开始时间
//		(时)	开始时间
//				(分)	结束时间
//			(时)	结束时间
//			(分)

        String s = "01";
        if (!open) {
            s = "00";
        }

        return getNLenthInstructions(INSTRUCTIONS_DO_NOT_DISTURD, s, "00", "00", "17", "3B");
    }

    /**
     * 获取设置勿扰模式 当前的时间默认为
     *
     * @param open        是否开启
     * @param startHour   开始小时
     * @param startMinute 开始分钟
     * @param endHour     结束小时
     * @param endMinute   结束分钟
     * @return
     */
    public static String getDoNotDisturdInstructions(boolean open, int startHour, int startMinute, int endHour, int endMinute) {
//		Byte4	Byte5	Byte6	Byte7	Byte8	Byte9
//		长度：5	勿扰使能
//		1: 开启
//		0: 关闭
// 	开始时间
//		(时)	开始时间
//				(分)	结束时间
//			(时)	结束时间
//			(分)

        String s = "01";
        if (!open) {
            s = "00";
        }

        return getNLenthInstructions(INSTRUCTIONS_DO_NOT_DISTURD, s, hex10_16(startHour+""),hex10_16( startMinute+""), hex10_16(endHour+""), hex10_16(endMinute+""));
    }

    /**
     * 获取发送金币数据信息
     * 此命令DZ800有效
     *
     * @return
     */
    public static String getGoldCoinInstructions(int goldCoin, int step, double kcal, double km) {
        if (goldCoin > 999999999) {

            return getNLenthInstructions(INSTRUCTIONS_SEND_GOLD_COIN, "FF", "FF", "FF", "FF");
        }
        String[] sendGoldCoin16 = getByteDataSort(goldCoin, 4);
        String[] sendStep16 = getByteDataSort(step, 4);
        String[] sendKcal16 = getByteDataSort((int) (kcal), 2);
        String[] sendKm16 = getByteDataSort((int) (km * 1000), 4);

        String[] sendData = new String[sendGoldCoin16.length + sendStep16.length + sendKcal16.length + sendKm16.length];
        System.arraycopy(sendGoldCoin16, 0, sendData, 0, sendGoldCoin16.length);
        System.arraycopy(sendStep16, 0, sendData, 4, sendStep16.length);
        System.arraycopy(sendKcal16, 0, sendData, 8, sendKcal16.length);
        System.arraycopy(sendKm16, 0, sendData, 10, sendKm16.length);
//		for(int i = 0;i<goldCoin16.length;i++)
//		{
//			sendGoldCoin16[i+1] = goldCoin16[i];
////			j++;
//		}
        return getNLenthInstructions(INSTRUCTIONS_SEND_GOLD_COIN, sendData);
    }

    /**将数据解析为指定长度的数据并且按照从左到右排序
     * @param data 数据
     * @param len   需要的长度
     * @return
     */
    public static String[] getByteDataSort(int data, int len) {
        if (len <= 0) {

            return new String[]{"00"};
        }
        String[] reByteData = new String[len];
        for (int i = 0; i < reByteData.length; i++) {
            reByteData[i] = "00";
        }

        String[] byteData16 = analysisData(hex10_16(data + ""));
        ;
        int j = 0;
        for (int i = byteData16.length - 1; i > -1; i--) {
            reByteData[j] = byteData16[i];
            j++;
        }
        return reByteData;
    }

    /**
     * 获取设置闹钟指令 闹钟数量最大为3个
     *
     * @return
     */
    public static String getAlarmClockInstructions(List<AlarmClockRemind> alarmClockReminds) {
//		Byte4	Byte5	Byte6	Byte7	...	ByteM-2	ByteM-1	ByteM
//		长度：3 * N	小时	分钟	重复	...	小时	分钟	重复
//		闹钟数量	闹钟1	...	闹钟N
//		重复说明	Bit0--Bit6分别表示周一到周日的重复设置，所有位都为0时，表示只有当天有效
//		0 ：表示不重复
//		1 ：表示重复
        if (alarmClockReminds.size() <= 0) {
            return getNLenthInstructions(INSTRUCTIONS_ALARM_CLOCK, "00", "00", "00");
        }
        String[] datas = new String[alarmClockReminds.size() * 3];
        int len = alarmClockReminds.size();
        if (len >= 3) {
            len = 3;
        }

        for (int i = 0; i < len; i++) {
            AlarmClockRemind acr = alarmClockReminds.get(i);
            datas[i * 3 + 0] = hex10_16(acr.getHour() + "");
            datas[i * 3 + 1] = hex10_16(acr.getMinute() + "");
            if (acr.isRepeat()) {
                datas[i * 3 + 2] = "7F";
            } else {
                StringBuffer bits = new StringBuffer("0");
                if (acr.getCycles()[0]) {
                    bits.append("1");
                } else {
                    bits.append("0");
                }
                for (int j = acr.getCycles().length - 1; j > 0; j--) {
                    if (acr.getCycles()[j]) {
                        bits.append("1");
                    } else {
                        bits.append("0");
                    }
                }


                datas[i * 3 + 2] = bitTo16(bits.toString());
            }

        }


        return getNLenthInstructions(INSTRUCTIONS_ALARM_CLOCK, datas);
    }


    /**
     * 获取发送图片命令
     *
     * @param context      上下文
     * @param instructions 指令
     * @param name         发送的名字
     * @param phone        发送的电话
     * @return
     */
    public static String getSendBitmapInstructions(Context context, String instructions, String name, String phone) {
        byte[] datas = FormatBMP(createBitmap(context, name));

        if (datas == null) {
            return "";
        }
        if (phone == null) {
            phone = "";
        }
        StringBuffer phones = new StringBuffer();

        for (int i = 0; i < 11; i++) {
            if (i < phone.length()) {
                phones.append("0" + phone.charAt(i));
            } else {
                phones.append("FF");
            }

        }
        byte[] phoneByte = new byte[11];
        try {
            phoneByte = Hex.decodeHex(phones.toString().toCharArray());
        } catch (DecoderException e) {
            e.printStackTrace();
        }

        StringBuffer str = new StringBuffer(INSTRUCTIONS_HEAD);
        str.append(instructions);//指令
//		str.append("00");
        String len = hex10_16(datas.length + phoneByte.length + 1 + "");
        if (len.length() == 2) {
            str.append("00");
            str.append(len);
        } else {
            str.append(len);
        }


        str.append(hex10_16(phoneByte.length + ""));
        str.append(phones.toString());
//		str.append(hex10_16(datas.length+""));
        String s = "";
        if (datas == null || datas.length == 0) {

            s = calculationCRC(str.toString());
        } else {
            s = calculationCRC(str.toString(), datas);
        }

        str.append(new String(Hex.encodeHex(datas)));
        str.append(s);
//		proofreadingCRC(str.toString());
        return str.toString();
    }

    /**
     * DZ800运动历史数据请求
     *
     * @return
     */
    public static String getDZ800MotionHistoryRequestInstructions() {
        return getZeroLenthInstructions(INSTRUCTIONS_DZ800_MOTION_HISTORY_REQUEST);
    }

    /**
     * DZ800运动历史数据接收成功
     *
     * @return
     */
    public static String getDZ800MotionHistorySuccessInstructions() {
        return getZeroLenthInstructions(INSTRUCTIONS_DZ800_MOTION_HISTORY_SUCCESS);
    }

    /**
     * 心率血压血氧历史请求
     *
     * @return
     */
    public static String getDZ800BPOXYHEARTHistoryRequestInstructions() {
        return getZeroLenthInstructions(INSTRUCTIONS_DZ800_BPOXYHEART_HISTORY_REQUEST);
    }

    /**
     * 心率血压血氧历史数据接收成功
     *
     * @return
     */
    public static String getDZ800BPOXYHEARTHistorySuccessInstructions() {
        return getZeroLenthInstructions(INSTRUCTIONS_DZ800_BPOXYHEART_HISTORY_SUCCESS);
    }


    /**
     * 获取0长度的命令
     *
     * @param instructions 命令
     * @return
     */
    private static String getZeroLenthInstructions(String instructions) {
        StringBuffer str = new StringBuffer(INSTRUCTIONS_HEAD);
        str.append(instructions);
        str.append("00");
        str.append("00");
        str.append(calculationCRC(str.toString()));
        proofreadingCRC(str.toString());
        return str.toString();
    }

    /**
     * 获取1长度的命令
     *
     * @param instructions 命令
     * @return
     */
    private static String getOneLenthInstructions(String instructions, String data) {
        StringBuffer str = new StringBuffer(INSTRUCTIONS_HEAD);
        str.append(instructions);
        str.append("00");
        str.append("01");
        str.append(data);
        str.append(calculationCRC(str.toString()));
        proofreadingCRC(str.toString());
        return str.toString();
    }


    /**
     * 获取N长度的命令
     *
     * @param instructions 命令
     * @return
     */
    private static String getNLenthInstructions(String instructions, String... data) {
        if (data == null || data.length < 0) {
            return "";
        }
        StringBuffer str = new StringBuffer(INSTRUCTIONS_HEAD);
        str.append(instructions);
        str.append("00");
        str.append(hex10_16(data.length + ""));
        for (String s : data) {
            str.append(s);
        }

        str.append(calculationCRC(str.toString()));
        proofreadingCRC(str.toString());
        return str.toString();
    }

    /**
     * 计算校验码
     *
     * @param val
     * @return
     */
    public static String calculationCRC(String val) {
        byte[] data;
        String crc = "";
        try {
            data = Hex.decodeHex(val.toCharArray());


            if (data != null) {
                byte bb = data[0];
                for (int i = 1; i < data.length; i++) {
                    bb += data[i];
                }
                int s = (bb & 0xFF);

                crc = Integer.toHexString(s) + "";
            }
            if (crc.length() < 2) {
                crc = "0" + crc;
            }
        } catch (DecoderException e) {
            e.printStackTrace();
        }

        return crc;
    }

    /**
     * 计算校验码
     *
     * @param val
     * @return
     */
    public static String calculationCRC(String val, byte[] datas) {
        byte[] data1;
        byte[] data;
        String crc = "";
        if (datas == null || datas.length <= 0) {
            return "";
        }
        try {
            data1 = Hex.decodeHex(val.toCharArray());
            data = new byte[data1.length + datas.length];

            System.arraycopy(data1, 0, data, 0, data1.length);

            System.arraycopy(datas, 0, data, data1.length, datas.length);
//			int j = 0;
//			for(byte b:data)
//			{
//				j++;
//				LogUtile.i(TAG, "byte"+j+":"+b);
//			}
//			LogUtile.i(TAG, "data1:'"+data1[data1.length-1]+"data_data1:"+data[data1.length-1]+"\ndatas:"
//					+datas[datas.length-1]+"data_datas:"+data[data.length-1]);
            if (data != null) {
                byte bb = data[0];
                for (int i = 1; i < data.length; i++) {
                    bb += data[i];
                }
                int s = (bb & 0xFF);

                crc = Integer.toHexString(s) + "";
            }
            if (crc.length() < 2) {
                crc = "0" + crc;
            }
        } catch (DecoderException e) {
            e.printStackTrace();
        }

        return crc;
    }

    /**
     * 校对校验码
     *
     * @param val
     * @return
     */
    public static boolean proofreadingCRC(String val) {

        if (val == null || val.length() <= 2) {
            LogUtile.i(TAG, "校验的数据:" + val + "");
            return false;
        }

        String s = val.substring(0, val.length() - 2);
//		LogUtile.i(TAG,"可能报错的地方:"+s+"");
        char[] c = s.toCharArray();
        String crc = val.substring(val.length() - 2);
        byte[] data;
        String temp = "";
        try {
            data = Hex.decodeHex(c);
            if (data != null) {
                byte bb = data[0];
                for (int i = 1; i < data.length; i++) {
                    bb += data[i];
                }
                int num = (bb & 0xFF);

                temp = Integer.toHexString(num) + "";
                if (temp.length() < 2) {
                    temp = "0" + temp;
                }
            }


            LogUtile.e(TAG, "校对一下:" + val
//					+"\ncrc:"+temp+"\n"+crc+"\n进程ID"+android.os.Process.myPid()
//					+"\n线程ID"+android.os.Process.myTid()+"\n用户ID"+android.os.Process.myUid()
            );
//			System.out.println("crc:"+crc+"\ttemp:"+temp);
            if (crc.equals(temp)) {
                return true;
            }
        } catch (DecoderException e) {
            e.printStackTrace();
//			System.out.println(e.getMessage()+"");
        }
        return false;
    }

    /**
     * 10转16
     *
     * @param val
     * @return
     */
    public static String hex10_16(String val) {
        String s = "00";
        try {
            s = Integer.toHexString(Integer.parseInt(val));
            if (s.length() % 2 != 0) {
                s = "0" + s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtile.i(TAG, "进制转换:" + s);

        return s;
    }

    /**
     * 解析命令为字符数组
     *
     * @param str
     * @return
     */
    public static String[] analysisData(String str) {
        if (str == null || str.length() % 2 != 0) {
            return null;
        }
        String[] strs = new String[str.length() / 2];
        for (int i = 0; i < strs.length; i++) {

            strs[i] = str.substring(i * 2, i * 2 + 2);
//			LogUtile.i(TAG, "解析的字符串:序号:"+i+"\t数据:"+strs[i]);

        }


        return strs;
    }

    /**
     * 获取数据的测量时间
     *
     * @param strs
     * @return
     */
    public static String getTime(String[] strs) {
        try {
            String y = "20" + "" + Integer.valueOf(strs[4], 16);
            int m1 = Integer.valueOf(strs[5], 16);
            int d1 = Integer.valueOf(strs[6], 16);
            String m2 = m1 < 10 ? "0" + m1 : m1 + "";
            String d2 = d1 < 10 ? "0" + d1 : d1 + "";
            return y + "-" + m2 + "-" + d2;

        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 获取数据的测量时间
     *
     * @param strs
     * @return
     */
    public static String getTime(String[] strs, int monthIndex, int dayIndex, int... yearIndex) {
        try {
            String y = "2018";
            if (yearIndex != null && yearIndex.length == 1) {
                y = "20" + Integer.valueOf(strs[yearIndex[0]], 16) + "";
            }
            if (yearIndex != null && yearIndex.length >= 2) {
                y = Integer.valueOf(strs[yearIndex[1]] + strs[yearIndex[0]], 16) + "";
            }

            int m1 = Integer.valueOf(strs[monthIndex], 16);
            int d1 = Integer.valueOf(strs[dayIndex], 16);
            String m2 = m1 < 10 ? "0" + m1 : m1 + "";
            String d2 = d1 < 10 ? "0" + d1 : d1 + "";
            return y + "-" + m2 + "-" + d2;

        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 是否是当天
     *
     * @param str
     * @return
     */
    public static boolean isSameDay(String[] str) {
        if (str.length < 7) {
            LogUtile.e(TAG, "数据不对");
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dt = sdf.format(new Date());
        String[] date = dt.split("-");
        int y = Integer.parseInt(date[0].substring(2));
        int m = Integer.parseInt(date[1]);
        int d = Integer.parseInt(date[2]);
        int y1 = Integer.valueOf(str[4], 16);
        int m1 = Integer.valueOf(str[5], 16);
        int d1 = Integer.valueOf(str[6], 16);
        LogUtile.e(TAG, "今天:" + y + "-" + m + "-" + d + "       数据日期:" + y1 + "-" + m1 + "-" + d1);
        if (y1 != y) {

            LogUtile.e(TAG, "不是今年");
            return false;
        }
        if (m1 != m) {
            LogUtile.e(TAG, "不是这个月");
            return false;
        }
        if (d1 != d) {
            LogUtile.e(TAG, "不是今天");
            return false;
        }

        return true;
    }

    /**
     * 获取测量的数据
     *
     * @param strs
     * @param dataNum
     * @param len
     * @return
     */
    public static String[][] getMeasureData(String[] strs, int dataNum, int len) {
        if (!(strs.length >= 8 + dataNum * len + 1)) {
            return null;
        }
        String[][] datas = new String[dataNum][];
        try {
            for (int i = 0; i < datas.length; i++) {

                String[] data = new String[len];

                for (int j = 0; j < data.length; j++) {
                    data[j] = strs[7 + i * len + (j + 1)];
                }
                datas[i] = data;

            }
        } catch (Exception e) {
            e.printStackTrace();
            datas = null;
        }
        return datas;
    }

    /**
     * 获取测量的数据
     *
     * @param strs       处理的数据
     * @param startIndex 从哪里开始的index
     * @param dataNum    //数据数量
     * @param len        //数据的长度
     * @return
     */
    public static String[][] getMeasureData(String[] strs, int startIndex, int dataNum, int len) {
        if (!(strs.length >= (startIndex + 1) + dataNum * len + 1)) {
            return null;
        }
        String[][] datas = new String[dataNum][];
        try {
            for (int i = 0; i < datas.length; i++) {

                String[] data = new String[len];

                for (int j = 0; j < data.length; j++) {
                    data[j] = strs[startIndex + i * len + (j + 1)];
                }
                datas[i] = data;

            }
        } catch (Exception e) {
            e.printStackTrace();
            datas = null;
        }
        return datas;
    }

    /**
     * bite 转 16进制
     *
     * @param bit
     * @return
     */
    public static String bitTo16(String bit) {
        int re, len;
        if (null == bit) {
            return 0 + "0";
        }
        len = bit.length();
        if (len != 4 && len != 8) {
            return 0 + "0";
        }
        if (len == 8) {// 8 bit处理
            if (bit.charAt(0) == '0') {// 正数
                re = Integer.parseInt(bit, 2);
            } else {// 负数
                re = Integer.parseInt(bit, 2) - 256;
            }
        } else {//4 bit处理
            re = Integer.parseInt(bit, 2);
        }


        return hex10_16(re + "");
    }


    private static Bitmap createBitmap(Context context, String text) {
        if (text == null || text.length() <= 0) {
            return null;
        }
        if (text.length() > 6) {
            text = text.substring(0, 6);
        }
        int textSize = 16;//字体大小
        int textColor = Color.rgb(51, 51, 51);//字体颜色
        Paint textPaint = new Paint();//画笔
        textPaint.setColor(textColor);//设置颜色
        textPaint.setTextSize(textSize);//设置字体大小
        int textW = (int) textPaint.measureText(text, 0, text.length());//获取字体宽度
        Rect rect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), rect);
        int textH = rect.height();//获取字体高度
        int bW = textW; //bitmap 宽度
        int bH = textH + 2;//bitmap 高度
        Bitmap bitmap = Bitmap.createBitmap(bW, bH, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);//画布
        canvas.drawColor(Color.WHITE);//设置画布背景颜色
        canvas.drawText(text, 0, bH - 3, textPaint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bitmap;
    }

    /**
     * 将bitmap转换成bmp格式图片
     *
     * @param bitmap 要转换的bitmap
     *               <p>
     *               param fos bmp文件输出流
     */
    public static byte[] FormatBMP(Bitmap bitmap) {

        if (bitmap != null) {
            int w = bitmap.getWidth(), h = bitmap.getHeight();
            int[] pixels = new int[w * h];
            bitmap.getPixels(pixels, 0, w, 0, 0, w, h);//取得BITMAP的所有像素点

            byte[] rgb = addBMP_RGB_888(pixels, w, h);

            ByteBuffer byteBuffer = ByteBuffer.allocate(rgb.length + 2);
            byteBuffer.put((byte) w);
            byteBuffer.put((byte) h);
            byteBuffer.put(rgb);
            return byteBuffer.array();

        }
        return new byte[]{0, 0};
    }

    private static byte[] addBMP_RGB_888(int[] b, int w, int h) {
        int len = w * h;

        byte[] tmp = new byte[3];
        int index = 0, bitindex = 1;
        int bufflen = 0;
        if (w * h % 8 != 0)//将8字节变成1个字节,不足补0
        {
            bufflen = w * h / 8 + 1;
        } else {
            bufflen = w * h / 8;
        }
        if (bufflen % 4 != 0)//BMP图像数据大小，必须是4的倍数，图像数据大小不是4的倍数时用0填充补足
        {
            bufflen = bufflen + bufflen % 4;
        }
        byte[] buffer = new byte[bufflen];
        for (int i = 0; i < len; i += w) {
            // DIB文件格式最后一行为第一行，每行按从左到右顺序
            int end = i + w - 1, start = i;
            Log.i("图像处理", "处理结果:I:" + i + "\t W:" + w + " \tEND:" + end + "\tSTART:" + start);
            for (int j = start; j <= end; j++) {

                tmp[0] = (byte) (b[j] >> 0);
                tmp[1] = (byte) (b[j] >> 8);
                tmp[2] = (byte) (b[j] >> 16);

                String hex = "";
                for (int g = 0; g < tmp.length; g++) {
                    String temp = Integer.toHexString(tmp[g] & 0xFF);
                    if (temp.length() == 1) {
                        temp = "0" + temp;
                    }
                    hex = hex + temp;
                }

                if (bitindex > 8) {
                    index += 1;
                    bitindex = 1;
                }

                if (!hex.equals("ffffff")) {
                    buffer[index] = (byte) (buffer[index] | (0x01 << 8 - bitindex));
                }
                bitindex++;
                Log.i("TTT", "===buffer_index=" + index + "======buffer=" + buffer[index]);
            }
        }
        return buffer;
    }
}
