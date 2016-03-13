package com.tangwenqiang.mytimer.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tangwenqiang.mytimer.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by qwtangwenqiang on 2016/3/9.
 */
public class StopWatchView extends LinearLayout {
    private TextView tv_hour, tv_minute, tv_second, tv_ms;
    private Button btn_begin, btn_pause, btn_recorder, btn_reset, btn_resume;
    private ListView lv_list;
    private ArrayAdapter<String> adapter;
    public StopWatchView(Context context) {
        super(context);
    }

    public StopWatchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StopWatchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        init();
    }

    private void init() {
        lv_list = (ListView) findViewById(R.id.lv_list);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        lv_list.setAdapter(adapter);

        tv_hour = (TextView) findViewById(R.id.tv_hour);
        tv_minute = (TextView) findViewById(R.id.tv_minute);
        tv_second = (TextView) findViewById(R.id.tv_second);
        tv_ms = (TextView) findViewById(R.id.tv_ms);


        btn_begin = (Button) findViewById(R.id.btn_begin);
        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_resume = (Button) findViewById(R.id.btn_resume);
        btn_recorder = (Button) findViewById(R.id.btn_recorder);
        btn_reset = (Button) findViewById(R.id.btn_reset);

        btn_begin.setOnClickListener(listener_begin);
        btn_recorder.setOnClickListener(listener_recorder);
        btn_pause.setOnClickListener(listener_pause);
        btn_reset.setOnClickListener(listener_reset);
        btn_resume.setOnClickListener(listener_resume);
    }

    OnClickListener listener_begin = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startWatch();
        }
    };

    OnClickListener listener_recorder = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showRecorder();
        }
    };

    OnClickListener listener_pause = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }
        }
    };

    OnClickListener listener_reset = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (timerTask != null) {
                allTime = 0;
                timerTask.cancel();
                timerTask = null;
            }
            tv_ms.setText("0");
            tv_second.setText("0");
            tv_minute.setText("0");
            tv_hour.setText("0");
            adapter.clear();
        }
    };

    OnClickListener listener_resume = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startWatch();
        }
    };

    private void showRecorder() {
        StringBuffer sb = new StringBuffer();
        sb.append(tv_hour.getText().toString())
                .append(":")
                .append(tv_minute.getText().toString())
                .append(":")
                .append(tv_second.getText().toString())
                .append(":")
                .append(tv_ms.getText().toString());
        adapter.add(sb.toString());
    }

    private Timer timer = new Timer();
    private TimerTask timerTask = null;
    private int allTime;
    private void startWatch() {
        allTime = 0;
        int hour = 0, min = 0, sec = 0, ms = 0;
        if (!TextUtils.isEmpty(tv_hour.getText())) {
            hour = Integer.parseInt(tv_hour.getText().toString());
        }
        if (!TextUtils.isEmpty(tv_minute.getText())) {
            min = Integer.parseInt(tv_minute.getText().toString());
        }
        if (!TextUtils.isEmpty(tv_second.getText())) {
            sec = Integer.parseInt(tv_second.getText().toString());
        }
        if (!TextUtils.isEmpty(tv_ms.getText())) {
            ms = Integer.parseInt(tv_ms.getText().toString());
        }

        allTime = hour * 60 * 60 * 1000 + min * 60 * 1000 + sec * 1000 + ms;
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    allTime++;
                    handler.sendEmptyMessage(BENGIN_WATCH);
                }
            };
            timer.schedule(timerTask, 0, 1);
        }
    }

    private final int BENGIN_WATCH = 1;
    private final int PAUSE_WATCH = 1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == BENGIN_WATCH) {
                int hour = allTime/60/60/1000;
                int min = (allTime/60/1000)%60;
                int sec = (allTime/1000)%60;
                int ms = allTime % 1000;

                tv_hour.setText(hour+"");
                tv_minute.setText(min+"");
                tv_second.setText(sec+"");
                tv_ms.setText(ms+"");
            }

        }
    };

    public void onDestroy() {
        timer.cancel();
    }

}
