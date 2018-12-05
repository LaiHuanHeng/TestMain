package com.welcare.hjkblelibrary.entity;

/**
 * Created by admin on 2018/11/27.
 * 计步,运动数据
 */

public class StepData {
    /**
     * 测量时间 格式 yyyy-MM-dd
     */
    public String time;
    /**
     * 运动步数
     */
    public int step;
    /**
     * 卡路里数据 单位大卡
     */
    public double kcal;
    /**
     * 运动距离 单位千里
     */
    public double km;
    /**
     * 活跃时间 部分手环不存在
     */
    public double activeTime;

    public StepData(String time, int step, double kcal, double km, double activeTime) {
        this.time = time;
        this.step = step;
        this.kcal = kcal;
        this.km = km;
        this.activeTime = activeTime;
    }

    public StepData() {
    }
}
