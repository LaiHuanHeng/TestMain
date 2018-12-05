package com.welcare.hjkblelibrary.entity;

/**
 * Created by admin on 2018/11/27.
 * 睡眠
 */

public class SleepData {
    /**
     * 测量时间 格式 yyyy-MM-dd
     */
    public String time;
    /**
     * 深睡 单位:分钟
     */
    public int deepSleep;
    /**
     * 浅睡 单位:分钟
     */
    public int lightSleep;
    /**
     * 未睡 单位:分钟
     */
    public int notAsleep;

    public SleepData(String time, int deepSleep, int lightSleep, int notAsleep) {
        this.time = time;
        this.deepSleep = deepSleep;
        this.lightSleep = lightSleep;
        this.notAsleep = notAsleep;
    }

    public SleepData() {
    }
}
