package com.welcare.hjkblelibrary.entity;

/**
 * Created by admin on 2018/11/27.
 * 血压
 */

public class BloodPressure {
    /**
     * 测量时间 格式 yyyy-MM-dd HH:mm
     */
    public String time;
    /**
     * 收缩压
     */
    public int systolic;
    /**
     * 舒张压
     */
    public int diastolic;

    public BloodPressure(String time, int systolic, int diastolic) {
        this.time = time;
        this.systolic = systolic;
        this.diastolic = diastolic;
    }

    public BloodPressure() {
    }
}
