package com.welcare.hjkblelibrary.entity;

/**
 * Created by admin on 2018/11/27.
 * 心率
 */

public class HeartData {
    /**
     * 测量时间 格式 yyyy-MM-dd HH:mm
     */
   public String time;
    /**
     * 心率
     */
    public int heart;

    public HeartData(String time, int heart) {
        this.time = time;
        this.heart = heart;
    }

    public HeartData() {
    }
}
