package com.zzmeow.audiotest;

import android.media.AudioTrack;

public class ProcessPlayback {

    private static final String TAG = "ProcessPlayback";

    private static ProcessPlayback mInstance;

    // 通过将构造函数设为私有，外部类无法直接创建该类的对象，只能通过 getInstance() 方法获取
    private ProcessPlayback() { }

    public static ProcessPlayback getInstance() {
        if (mInstance == null) {
            synchronized (ProcessPlayback.class) {
                if (mInstance == null) {
                    mInstance = new ProcessPlayback();
                }
            }
        }
        return mInstance;
    }

    public void startTrack() {

    }

    public void stopTrack() {

    }
}
