package com.tangwenqiang.mytimer.views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tangwenqiang.mytimer.MainActivity;
import com.tangwenqiang.mytimer.R;
import com.tangwenqiang.mytimer.broadcastreceiver.AlarmReceiver;
import com.tangwenqiang.mytimer.datas.AlarmData;

import java.nio.Buffer;
import java.util.Calendar;

/**
 * Created by qwtangwenqiang on 2016/3/7.
 */
public class AlarmView extends LinearLayout {

    private Button btn_add_alarm;
    private ListView lv_alarm_list;
    private ArrayAdapter<AlarmData> adapter;
    private AlarmManager alarmManager;
    private static final String KEY_ALARM_LIST = "alarmList";   //  保存闹钟的键值

    public AlarmView(Context context) {
        super(context);
        //init(context);
        alarmManager = MainActivity.alarmManager;
    }

    public AlarmView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init(context);
        alarmManager = MainActivity.alarmManager;
    }

    public AlarmView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //init(context);
    }

    private void init(Context context) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        btn_add_alarm = (Button) findViewById(R.id.btn_add_alarm);
        lv_alarm_list = (ListView) findViewById(R.id.lv_alarm_list);

        adapter = new ArrayAdapter<AlarmData>(getContext(), android.R.layout.simple_list_item_1);
        lv_alarm_list.setAdapter(adapter);

        btn_add_alarm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlarm();
            }
        });

       lv_alarm_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
               new AlertDialog.Builder(getContext())
                       .setTitle("操作选项").
                       setItems(new CharSequence[]{"删除", "编辑"}, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       switch (which) {
                           case 0:
                               deleteAlarm(position);
                               break;
                       }
                   }
               }).show();

               return true;
           }
       });
        // 读取保存的闹钟
        readSaveAlarmList();
    }
    // 读取保存的闹钟
    private void readSaveAlarmList() {
        SharedPreferences sp = getContext().getSharedPreferences(AlarmView.class.getName(),
                Context.MODE_PRIVATE);
        String content = sp.getString(KEY_ALARM_LIST, null);
        if (content != null) {
            //以逗号为分开符，获得时间数组
            String []timesString = content.split(",");
            for (String s : timesString) {
                adapter.add(new AlarmData(Long.parseLong(s)));
            }
        }
    }

    private void deleteAlarm(int position) {
        AlarmData ad = adapter.getItem(position);
        adapter.remove(ad);
        saveAlarmList();
        // 取消闹钟
        alarmManager.cancel(PendingIntent.getBroadcast(getContext(),
                ad.getRequestCode(),
                new Intent(getContext(), AlarmReceiver.class), 0));
    }

    private void addAlarm() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                Calendar curCalendar = Calendar.getInstance();
                if (curCalendar.getTimeInMillis() >= calendar.getTimeInMillis()) {
                    calendar.setTimeInMillis(calendar.getTimeInMillis()+24*60*60*1000);
                }

                AlarmData alarmData = new AlarmData(calendar.getTimeInMillis());
                adapter.add(alarmData);
                //创建Intent对象，action为ELITOR_CLOCK，附加信息为字符串“你该打酱油了”
                Intent intent = new Intent("ELITOR_CLOCK");
                intent.putExtra("msg", "you have to get up!");
                //定义一个PendingIntent对象，PendingIntent.getBroadcast包含了sendBroadcast的动作。
                //也就是发送了action 为"ELITOR_CLOCK"的intent
                PendingIntent pi = PendingIntent.getBroadcast(getContext(), alarmData.getRequestCode(),
                        intent, 0);

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
                
                // 保存闹钟
                saveAlarmList();
            }

        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true){
            @Override
            protected void onStop() {
                //删除父级调用，避免onTimeSet执行两次
            }
        }.show();
    }

    private void saveAlarmList() {
        //使用SharedPreferences保存数据
        //实例化SharedPreferences对象（第一步）
        SharedPreferences mySp = getContext().getSharedPreferences(AlarmView.class.getName(),
                Context.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySp.edit();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < adapter.getCount(); i++) {
            //得到所有闹钟的时间，存入一个string，用逗号隔开
            sb.append(adapter.getItem(i).getTime()).append(",");
        }
        if (adapter.getCount() > 0) {
            // 去除最后的一个逗号
            String content = sb.toString().substring(0, sb.length() - 1);
            // 保存数据
            editor.putString(KEY_ALARM_LIST, content);

        } else {
            editor.putString(KEY_ALARM_LIST, null);
        }
        // 提交
        editor.commit();
    }

}
