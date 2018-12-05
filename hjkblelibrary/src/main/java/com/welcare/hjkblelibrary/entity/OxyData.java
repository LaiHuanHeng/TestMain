package com.welcare.hjkblelibrary.entity;

/**
 * Created by admin on 2018/11/27.
 * 血氧
 */

public class OxyData {


    /**
     * 测量时间 格式 yyyy-MM-dd HH:mm
     */

    public String time;
    /**
     * 血氧 单位为%
     */
    public int oxy;
    public OxyData(String time, int oxy) {
        this.time = time;
        this.oxy = oxy;
    }

    public OxyData() {
    }
}
