package com.adups.fota.nv;

import android.util.Log;

public class NvWriter {  
    private static final String TAG = "NvWriter-TAG";  
    private static NvWriter sInstance = null;  
  
    /*FLAG RESULT*/  
    public static final char PASS = 'P';  
    public static final char FAIL = 'F';  
    public static final char NA = ' ';  
  
    static {  
      System.loadLibrary("nvwriter_jni");  
    }  
    private native String native_readflag_NV();  
    private native void native_writeflag_NV(int index,char result);  
  
    public static NvWriter getInstance() {  
      if (sInstance == null) {  
        sInstance = new NvWriter();  
      }  
      return sInstance;  
    }  
  
    public String readFlagNV() {  
      String mFlagNv = native_readflag_NV();  
      Log.i(TAG,"readFlagNV: mFlagNv = " + mFlagNv);  
      return mFlagNv;  
    }  
  
    public void writeFlagNV(int index,char result) {  
      native_writeflag_NV(index,result);  
    }  
  
    public char getFlag(int index) {  
      String mFlagNv = readFlagNV();  
      char flag = NA;  
      if (mFlagNv != null && mFlagNv.length() >= index) {  
        flag = mFlagNv.charAt(index);  
      }  
      Log.i(TAG,index + ": flag = " + flag);  
      return flag;  
    }  
} 