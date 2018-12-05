package com.welcare.hjkblelibrary.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by admin on 2018/11/28.
 * 闹钟
 */

public class AlarmClockRemind implements Serializable {
    /**
     * 设置闹钟时间戳
     */
    private long time;//闹钟时间
    /**
     * 闹钟小时
     */
    private int hour ;//闹钟小时
    /**
     * 一周几天 0星期日,1 星期一,2 星期二,3 星期三,4 星期四,5 星期五,6 星期六
     */
    private int minute;//闹钟分钟
    private boolean[] cycles = new boolean[7];//一周几天 0星期日,1 星期一,2 星期二,3 星期三,4 星期四,5 星期五,6 星期六
    /**
     * 是否每天重复 此字段为预留字段
     */
    private boolean repeat = false;//是否每天重复
    /**
     * 是否开启闹钟,此选项为false 则此闹钟无效
     */
    private boolean open = false;//是否开启

    /**获取设置闹钟时间戳
     * @return
     */
    public long getTime() {
        return time;
    }

    /**设置设置闹钟时间戳
     * @param time
     */
    public AlarmClockRemind setTime(long time) {
        this.time = time;
        return this;
    }

    /**获取一周那天开启闹钟,都是false为当天一次闹钟
     * @return
     */
    public boolean[] getCycles() {
        if(cycles == null)
        {
            cycles = new boolean[7];
        }
        return cycles;
    }

    /**设置一周那天开启闹钟,都是false为当天一次闹钟
     * @param cycles
     */
    public AlarmClockRemind setCycles(boolean[] cycles) {
        if(cycles != null && cycles.length != 7 )
        {
            cycles = new boolean[7];
            int i = 0;
            for(boolean b:cycles)
            {
                cycles[i] = b;
                i++;
                if(i == 7)
                {
                    break;
                }
            }
        }
        this.cycles = cycles;
        return this;
    }

    /**是否重复 预留字段
     * @return
     */
    public boolean isRepeat() {
        return repeat;
    }

    /**设置重复开关
     * @param repeat
     */
    public AlarmClockRemind setRepeat(boolean repeat) {
        this.repeat = repeat;
        return this;
    }

    /**获取闹钟的小时
     * @return
     */
    public int getHour() {
        return hour;
    }

    /**设置闹钟的小时
     * @param hour
     */
    public AlarmClockRemind setHour(int hour) {
        this.hour = hour;
        return this;
    }

    /**获取闹钟的分钟
     * @return
     */
    public int getMinute() {
        return minute;
    }

    /**设置闹钟的分钟
     * @param minute
     */
    public AlarmClockRemind setMinute(int minute) {
        this.minute = minute;
        return this;
    }

    /**是否开启闹钟
     * @return
     */
    public boolean isOpen() {
        return open;
    }

    /**设置闹钟开关
     * @param open
     */
    public void setOpen(boolean open) {
        this.open = open;
    }

    /**克隆一份闹钟对象进行修改不涉及到原对象
     * @return
     */
    public Object deepClone()
    {
        try {
            //将对象写到流里
            ByteArrayOutputStream bo=new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(this);
            //从流里读出来
            ByteArrayInputStream bi=new ByteArrayInputStream(bo.toByteArray());
            ObjectInputStream oi=new ObjectInputStream(bi);
            return oi.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}