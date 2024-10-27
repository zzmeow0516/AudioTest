package com.zzmeow.audiotest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    //将button id 映射到对应操作上
    // Runnable是一个接口，需要实现run方法
    private final Map<Integer, Runnable> buttonActions = new HashMap<>();

    private ProcessRecord myAudioRecord;
    private ProcessPlayback myAudioTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 获取单例
        myAudioRecord = ProcessRecord.getInstance();
        myAudioTrack = ProcessPlayback.getInstance();

        // find button view by id
        Button button_startRecord = findViewById(R.id.button_start_record);
        Button button_stopRecord = findViewById(R.id.button_stop_record);
        Button button_startPlay =  findViewById(R.id.button_start_play);
        Button button_stopPlay = findViewById(R.id.button_stop_play);

        // 通过this直接将当前类MainActivity作为button的监听器，同时监听多个button.
        button_startRecord.setOnClickListener(this);
        button_stopRecord.setOnClickListener(this);
        button_startPlay.setOnClickListener(this);
        button_stopPlay.setOnClickListener(this);

        // 建立映射关系
        buttonActions.put(R.id.button_start_record, new Runnable() {
            @Override
            public void run() {
                myAudioRecord.startRecord();
                Log.d(TAG, "Recording started");
            }
        });

        buttonActions.put(R.id.button_stop_record, new Runnable() {
            @Override
            public void run() {
                myAudioRecord.stopRecord();
                Log.d(TAG, "Recording stopped");
            }
        });

        buttonActions.put(R.id.button_start_play, new Runnable() {
            @Override
            public void run() {
                myAudioTrack.startTrack();
                Log.d(TAG, "Playback started");
            }
        });

        buttonActions.put(R.id.button_stop_play, new Runnable() {
            @Override
            public void run() {
                myAudioTrack.stopTrack();
                Log.d(TAG, "Playback stopped");
            }
        });
    }

    // 直接在当前类中监听多个view, 可以implements View.OnClickListener, 但这必须override onClick()
    // 点击button时候，系统会调用onClick, 传递的参数就是view
    @Override
    public void onClick(View v) {
        // gradle 8.0及以上版本，需要避免在switch中使用资源ID, 因为资源ID不一定是Final的, 而switch-case中要求一定是final常量.
        /*@
        switch (v.getId()) {
            default:
                break;
        }
        @*/
        Runnable action = buttonActions.get(v.getId());
        if (action != null) {
            Log.d(TAG, "ready to run !");
            action.run();
        }
    }
}
