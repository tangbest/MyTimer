package com.tangwenqiang.mytimer.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tangwenqiang.mytimer.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by qwtangwenqiang on 2016/3/7.
 */
public class TimeView extends LinearLayout {

    private TextView tv_show_time;

    public TimeView(Context context) {
        super(context);
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tv_show_time = (TextView) findViewById(R.id.tv_show_time);
        tv_show_time.setText("time");

        showTimeHandler.sendEmptyMessage(0);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if (visibility == View.VISIBLE) {
            showTimeHandler.sendEmptyMessage(0);
        } else {
            showTimeHandler.removeMessages(0);
        }
    }

    private android.os.Handler showTimeHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refressTime();

            if (getVisibility() == View.VISIBLE) {
                showTimeHandler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    };

    private void refressTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日\n HH:mm:ss");
        Date curDate = new Date();
        String str = format.format(curDate);
        tv_show_time.setText(str);

//        Calendar c = Calendar.getInstance();
//
//        tv_show_time.setText(String.format("%d:%d:%d",
//                c.get(Calendar.HOUR_OF_DAY),
//                c.get(Calendar.MINUTE),
//                c.get(Calendar.SECOND)));
    }
}
