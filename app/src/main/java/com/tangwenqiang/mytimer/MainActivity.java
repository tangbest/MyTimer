package com.tangwenqiang.mytimer;

import android.app.AlarmManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

import com.tangwenqiang.mytimer.views.AlarmView;
import com.tangwenqiang.mytimer.views.StopWatchView;
import com.tangwenqiang.mytimer.views.TimerView;

public class MainActivity extends AppCompatActivity {

    private TabHost tabHost;
    public static AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        setContentView(R.layout.activity_main);

        initTabHost();
    }

    private void initTabHost() {
        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();    //初始化TabHost容器
        tabHost.addTab(tabHost.newTabSpec("tabTime").setIndicator(getString(R.string.tab_time))
                .setContent(R.id.tabTime));
        tabHost.addTab(tabHost.newTabSpec("tabAlarm").setIndicator(getString(R.string.tab_alarm))
                .setContent(R.id.tabAlarm));
        tabHost.addTab(tabHost.newTabSpec("tabTimer").setIndicator(getString(R.string.tab_timer))
                .setContent(R.id.tabTimer));
        tabHost.addTab(tabHost.newTabSpec("tabStopWatch").setIndicator(getString(R.string.tab_stop_watch))
                .setContent(R.id.tabStopWatch));

        timerView = (TimerView) findViewById(R.id.tabTimer);
        sview = (StopWatchView) findViewById(R.id.tabStopWatch);
    }

    TimerView timerView;
    StopWatchView sview;
    @Override
    protected void onDestroy() {
        sview.onDestroy();
        timerView.onDestroy();
        super.onDestroy();
    }
}
