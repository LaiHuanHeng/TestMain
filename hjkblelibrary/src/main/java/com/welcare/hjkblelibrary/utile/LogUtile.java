package com.welcare.hjkblelibrary.utile;

import android.util.Log;

/**
 * Created by admin on 2018/11/26.
 * 日志管理
 */

public class LogUtile {
    private static boolean isOpen = false;

    public static boolean isOpen() {
        return isOpen;
    }
    public static void setOpen(boolean open) {
        isOpen = open;
    }



    public static int v(String tag, String msg) {
        if(!isOpenLog())
        {
            return  -1;
        }
        return Log.v(tag,msg);// VERBOSE, tag, msg);
    }

    public static int v(String tag, String msg, Throwable tr) {
        if(!isOpenLog())
        {
            return  -1;
        }
        return Log.v(tag,msg,tr);//VERBOSE, tag, msg, tr);
    }
  public static int d(String tag, String msg) {
      if(!isOpenLog())
      {
          return  -1;
      }
        return Log.d(tag,msg);// DEBUG, tag, msg);
    }

    public static int d(String tag, String msg, Throwable tr) {
        if(!isOpenLog())
        {
            return  -1;
        }
        return Log.d(tag,msg,tr);//DEBUG, tag, msg, tr);
    }

    public static int i(String tag, String msg) {
        if(!isOpenLog())
        {
            return  -1;
        }
        return Log.d(tag,msg);// INFO, tag, msg);
    }

     public static int i(String tag, String msg, Throwable tr) {
         if(!isOpenLog())
         {
             return  -1;
         }
        return Log.i(tag,msg,tr);//INFO, tag, msg, tr);
    }

    public static int w(String tag, String msg) {
        if(!isOpenLog())
        {
            return  -1;
        }
        return Log.w(tag,msg);// WARN, tag, msg);
    }

    public static int w(String tag, String msg, Throwable tr) {
        if(!isOpenLog())
        {
            return  -1;
        }
        return Log.w(tag,msg,tr);//WARN, tag, msg, tr);
    }


    public static int w(String tag, Throwable tr) {
        if(!isOpenLog())
        {
            return  -1;
        }
        return Log.w(tag,tr);//WARN, tag, "", tr);
    }

    public static int e(String tag, String msg) {
        if(!isOpenLog())
        {
            return  -1;
        }
        return Log.e(tag,msg);// ERROR, tag, msg);
    }

    public static int e(String tag, String msg, Throwable tr) {
        if(!isOpenLog())
        {
            return  -1;
        }
        return Log.e(tag,msg,tr);//ERROR, tag, msg, tr);
    }

    public static int wtf(String tag, String msg) {
        if(!isOpenLog())
        {
            return  -1;
        }
        return Log.wtf( tag, msg);
    }

    public static int wtf(String tag, Throwable tr) {
        if(!isOpenLog())
        {
            return  -1;
        }
        return Log.wtf(tag, tr.getMessage(), tr);
    }

    public static int wtf(String tag, String msg, Throwable tr) {
        if(!isOpenLog())
        {
            return  -1;
        }
        return Log.wtf( tag, msg, tr);
    }


    public static String getStackTraceString(Throwable tr) {
      if(!isOpenLog())
      {
          return "";
      }
        return Log.getStackTraceString(tr);
    }

   
    public static int println(int priority, String tag, String msg) {
        if(!isOpenLog())
        {
            return  -1;
        }
        return Log.println(priority,tag,msg);// priority, tag, msg);
    }


    private static boolean isOpenLog()
    {
        return isOpen();
    }
}
