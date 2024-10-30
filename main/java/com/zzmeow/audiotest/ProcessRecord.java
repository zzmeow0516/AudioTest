package com.zzmeow.audiotest;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Process;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;


public class ProcessRecord {

    private static final String TAG = "ProcessRecord";
    private static ProcessRecord mInstance;
    private boolean isRecording = false;
    // 定义一个AudioRecord类对象用来操作AudioRecord类中的方法（实例方法）。
    // 除非你想调用的方法是static修饰的静态方法，这种情况下可以直接通过类名调用，无需实例化。
    private AudioRecord mAudioRecord;
    private int recordBufferSize;
    private Thread mRecordThread;
    private DataOutputStream dataOutputStream;

    // 通过将构造函数设为私有，外部类无法直接创建该类的对象，只能通过 getInstance() 方法获取
    private ProcessRecord() {
        // 构造的时候直接确定好buffer大小，getMinBufferSize是静态方法可以直接调用
        recordBufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        Log.d(TAG, "ProcessRecord() recordBufferSize = " + recordBufferSize);
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
                //当创建 mRecordThread 对象时，将 recordRunnable 作为参数传入，线程启动后会自动调用 recordRunnable 中的 run() 方法
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
            //开始 录音 的逻辑
            try {
                // 需要定义一个数组用来存放录制的数据
                byte[] arrayData = new byte[recordBufferSize];
                // 定义一个int数据用来保存read的字节数
                int bytesRead;
                // 最高优先级
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);

                if (mAudioRecord.getState() != mAudioRecord.STATE_INITIALIZED) {
                    Log.e(TAG, "audiorecord state = STATE_INITIALIZED, stopRecord !");
                    stopRecord();
                    return;
                }

                mAudioRecord.startRecording();
                while (isRecording) {
                    bytesRead = mAudioRecord.read(arrayData, 0, recordBufferSize);
                    if (bytesRead >= 0 ) {
                        // 写数据
                        dataOutputStream.write(arrayData, 0, recordBufferSize);
                    } else {
                        Log.e(TAG, "error! bytesRead = " + bytesRead);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    private void setFilePath (String path) throws Exception {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        dataOutputStream = new DataOutputStream(new FileOutputStream(file, true));
    }

    public boolean startRecord(String path) {
        Log.d(TAG, "startRecord");
        if (isRecording) {
            Log.w(TAG, "startRecord, we are recording now!");
            return false;
        }
        try {
            setFilePath(path);
            createRecordThread();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void stopRecord() {
        if (!isRecording) {
            Log.e(TAG, "还没开始录音...");
            return;
        }
        try {
            destroyRecordThread();

            if (mAudioRecord != null) {
                if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                    mAudioRecord.stop();
                }
                if (mAudioRecord != null) {
                    mAudioRecord.release();     // 释放资源，无法继续使用
                    mAudioRecord = null;
                }
            }

            if (dataOutputStream != null) {
                dataOutputStream.flush();
                dataOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
