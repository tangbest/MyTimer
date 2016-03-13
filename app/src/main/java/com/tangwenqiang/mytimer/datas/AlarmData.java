package com.tangwenqiang.mytimer.datas;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by qwtangwenqiang on 2016/3/7.
 */
public class AlarmData {
    private Calendar date;
    private long time;
    private String timeLabel;

    public AlarmData(long time) {
        this.time = time;
        date = Calendar.getInstance();
        date.setTimeInMillis(time);


        timeLabel = String.format(
                "%d月%d日 : %d时%d分", date.get(Calendar.MONTH)+1, date.get(Calendar.DAY_OF_MONTH),
                date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE));

    }

    public long getTime() {
        return time;
    }

    // 每个闹钟有不同的请求码
    public int getRequestCode() {
        return (int) (getTime()/1000/60);
    }
    @Override
    public String toString() {
        return getTimeLabel();  // 适配器返回的数据
    }

    private String getTimeLabel() {
        return timeLabel;
    }
}
