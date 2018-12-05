package com.welcare.hjkblelibrary.entity;

/**
 * Created by admin on 2018/11/26.
 * 设备类型
 */

public enum DeviceType {
    DZ800,QS80,X4,Y2;

    /**获取设备类型
     * @param bleName 设备名称
     * @return
     */
   public static DeviceType getType(String bleName){
        if("DZ800".equalsIgnoreCase(bleName))
        {
            return DZ800;
        }else if("QS80".equalsIgnoreCase(bleName))
        {
            return QS80;
        }else if("X4".equalsIgnoreCase(bleName))
        {
            return X4;
        }else if("Y2".equalsIgnoreCase(bleName))
        {
            return Y2;
        }
       return null;
   }
}
