package com.tangwenqiang.mytimer.views;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.tangwenqiang.mytimer.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by qwtangwenqiang on 2016/3/9.
 */
public class TimerView extends LinearLayout {
    private EditText et_hour, et_minute, et_second;
    private Button btn_begin, btn_pause, btn_resume, btn_reset;

    public TimerView(Context context) {
        super(context);
    }

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        et_hour = (EditText) findViewById(R.id.et_hour);
        et_minute = (EditText) findViewById(R.id.et_minute);
        et_second = (EditText) findViewById(R.id.et_second);

        btn_begin = (Button) findViewById(R.id.btn_begin);
        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_resume = (Button) findViewById(R.id.btn_resume);
        btn_reset = (Button) findViewById(R.id.btn_reset);

        btn_begin.setEnabled(false);
        btn_pause.setVisibility(View.GONE);
        btn_resume.setVisibility(View.GONE);
        btn_reset.setVisibility(View.GONE);

        et_hour.addTextChangedListener(tw_hour);
        et_minute.addTextChangedListener(tw_minute);
        et_second.addTextChangedListener(tw_second);

        btn_begin.setOnClickListener(listener_start);
        btn_pause.setOnClickListener(listener_pause);
        btn_resume.setOnClickListener(listener_resume);
        btn_reset.setOnClickListener(listener_reset);
    }

    private static int allSeconds;
    private Timer timer = new Timer();
    //开启另一个线程，用时间调度来计时，不用handler，handler在主线程运行，影响性能
    private TimerTask timerTask = null;
    OnClickListener listener_start = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startTimer();
            btn_begin.setVisibility(View.GONE);
            btn_pause.setVisibility(View.VISIBLE);

        }
    };

    OnClickListener listener_pause = new OnClickListener() {
        @Override
        public void onClick(View v) {
            pauseTimer();
            btn_pause.setVisibility(View.GONE);
            btn_resume.setVisibility(VISIBLE);
            btn_reset.setVisibility(VISIBLE);
        }
    };

    OnClickListener listener_resume = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startTimer();
            btn_resume.setVisibility(View.GONE);
            btn_reset.setVisibility(View.GONE);
            btn_pause.setVisibility(VISIBLE);
        }
    };

    OnClickListener listener_reset = new OnClickListener() {
        @Override
        public void onClick(View v) {
            stopTimer();
            et_hour.setText("0");
            et_minute.setText("0");
            et_second.setText("0");

            btn_resume.setVisibility(View.GONE);
            btn_reset.setVisibility(View.GONE);
            btn_begin.setVisibility(VISIBLE);
        }
    };

    private void pauseTimer() {
        stopTimer();
    }

    private static final int MSG_WHAT_TIME_TICK = 1;
    private static  final int MSG_WHAT_TIME_IS_UP = 2;
    private void startTimer() {
        allSeconds = 0;
        int hour = 0, min = 0, sec = 0;
        if (!TextUtils.isEmpty(et_hour.getText())) {
            hour = Integer.parseInt(et_hour.getText().toString());
        }
        if (!TextUtils.isEmpty(et_minute.getText())) {
            min = Integer.parseInt(et_minute.getText().toString());
        }
        if (!TextUtils.isEmpty(et_second.getText())) {
            sec = Integer.parseInt(et_second.getText().toString());
        }
        //获取计时的时间
        allSeconds = hour * 60 * 60 + min * 60 + sec;
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    //每执行一次，减少一秒
                    allSeconds--;
                    //用handler来刷新界面
                    handler.sendEmptyMessage(MSG_WHAT_TIME_TICK);

                    if (allSeconds <= 0) {
                        handler.sendEmptyMessage(MSG_WHAT_TIME_IS_UP);
                        stopTimer();
                    }
                }
            };
            //启动timerTask，延迟1000ms，每1000ms执行一次
            timer.schedule(timerTask, 1000, 1000);
        }
    }

    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_WHAT_TIME_TICK) {
                int hour = allSeconds/60/60;
                int min = (allSeconds/60)%60;
                int sec = allSeconds%60;

                et_hour.setText(hour+"");
                et_minute.setText(min+"");
                et_second.setText(sec+"");
            } else if (msg.what == MSG_WHAT_TIME_IS_UP) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Time is up")
                        .setMessage("Time is up")
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        }
    };

    TextWatcher tw_hour = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) {
                int value = Integer.parseInt(s.toString());
                if (value > 59) {
                    et_hour.setText("59");
                } else if (value < 0) {
                    et_hour.setText("0");
                }
            }
            checkToEnableStart();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    TextWatcher tw_minute = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) {
                int value = Integer.parseInt(s.toString());
                if (value > 59) {
                    et_minute.setText("59");
                } else if (value < 0) {
                    et_minute.setText("0");
                }
            }
            checkToEnableStart();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    TextWatcher tw_second = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) {
                int value = Integer.parseInt(s.toString());
                if (value > 59) {
                    et_second.setText("59");
                } else if (value < 0) {
                    et_second.setText("0");
                }
            }
            checkToEnableStart();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void checkToEnableStart() {
        btn_begin.setEnabled((!TextUtils.isEmpty(et_hour.getText())&&Integer.parseInt(et_hour.getText().toString())>0)
                || (!TextUtils.isEmpty(et_minute.getText())&&Integer.parseInt(et_minute.getText().toString())>0)
                || (!TextUtils.isEmpty(et_second.getText())&&Integer.parseInt(et_second.getText().toString())>0)
        );
    }

    public void onDestroy() {
        timer.cancel();
    }
}
