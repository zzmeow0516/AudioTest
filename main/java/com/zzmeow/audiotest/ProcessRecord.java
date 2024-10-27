package com.zzmeow.audiotest;

import android.media.AudioRecord;
import android.util.Log;

public class ProcessRecord {

    private static final String TAG = "ProcessRecord";

    private static ProcessRecord mInstance;

    // 通过将构造函数设为私有，外部类无法直接创建该类的对象，只能通过 getInstance() 方法获取
    private ProcessRecord() {

    }

    public static ProcessRecord getInstance() {
        // 双重检查锁定，确保创建单例
        if (mInstance == null) {
            synchronized (ProcessRecord.class) {
                if (mInstance == null) {
                    mInstance = new ProcessRecord();
                }
            }
        }
        return mInstance;
    }

    public void startRecord() {
        Log.d(TAG, "startrecord");
    }

    public void stopRecord() {

    }



}
