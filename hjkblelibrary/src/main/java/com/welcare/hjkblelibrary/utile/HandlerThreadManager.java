package com.welcare.hjkblelibrary.utile;

import android.os.HandlerThread;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by admin on 2018/11/26.
 * 线程管理器
 */

public class HandlerThreadManager {

    private static HandlerThreadManager instance;
    private HashMap<String, HandlerThread> handlerThreadHashMap = new HashMap<>();

    public static HandlerThreadManager getInstance() {
        if (instance == null) {
            synchronized (HandlerThreadManager.class) {
                if (instance == null) {
                    instance = new HandlerThreadManager();
                }
            }
        }
        return instance;
    }

    public void createHandler(String key) {
        if (handlerThreadHashMap.containsKey(key)) {
            return;
        }
        HandlerThread handlerThread = new HandlerThread(key);
        handlerThread.start();
        handlerThreadHashMap.put(key, handlerThread);
    }

    public HandlerThread getHanderThread(String key) {
        if (!handlerThreadHashMap.containsKey(key)) {
            createHandler(key);
        }
        return handlerThreadHashMap.get(key);
    }

    public void clear() {
        Set<String> keys = handlerThreadHashMap.keySet();
        for (String k : keys) {
            handlerThreadHashMap.get(k).quit();
        }
        handlerThreadHashMap.clear();
    }

}
