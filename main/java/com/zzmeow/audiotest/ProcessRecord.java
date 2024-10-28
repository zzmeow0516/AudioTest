package com.zzmeow.audiotest;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

public class ProcessRecord {

    private static final String TAG = "ProcessRecord";
    private static ProcessRecord mInstance;
    private boolean isRecording = false;
    // 定义一个AudioRecord类对象用来操作AudioRecord类中的方法（实例方法）。
    // 除非你想调用的方法是static修饰的静态方法，这种情况下可以直接通过类名调用，无需实例化。
    private AudioRecord mAudioRecord;
    private int recordBufferSize;
    private Thread mRecordThread;

    // 通过将构造函数设为私有，外部类无法直接创建该类的对象，只能通过 getInstance() 方法获取
    private ProcessRecord() {
        // 构造的时候直接确定好buffer大小，getMinBufferSize是静态方法可以直接调用
        recordBufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
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

    private void destroyRecordThread() {
        Log.d(TAG, "destroyRecordThread");
        try {
            isRecording = false;
            if (mRecordThread != null && Thread.State.RUNNABLE == mRecordThread.getState()) {
                mRecordThread.interrupt();
            }
            mRecordThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createRecordThread() {
        Log.d(TAG, "destroyRecordThread");
        destroyRecordThread();
        isRecording = true;
        try {
            if (mRecordThread == null) {
                //当你创建 mRecordThread 对象时，将 recordRunnable 作为参数传入，线程启动后会自动调用 recordRunnable 中的 run() 方法
                mRecordThread = new Thread(recordRunnable);
                mRecordThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            //开始录音的逻辑
        }
    };

    public boolean startRecord() {
        Log.d(TAG, "startRecord");
        if (isRecording) {
            Log.w(TAG, "startRecord, we are recording now!");
        }
        try {
            mAudioRecord.startRecording();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean stopRecord() {
        Log.d(TAG, "stopRecord");
        if (!isRecording) {
            Log.e(TAG, "not isRecording!");
            return false;
        }
        isRecording = false;
        try {
            mAudioRecord.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}
