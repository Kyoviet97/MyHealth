package com.health.myhealth.utils;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.health.myhealth.service.ServiceAlarm;

public class AlarmManager {
    public static android.app.AlarmManager alarmMnager;
    public static PendingIntent pendingIntent;

    @SuppressLint("ServiceCast")
    public static void setUp(Context context, int s) {
        alarmMnager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ServiceAlarm.class);
        intent.setAction("COUNT_HEALTH_1023");
        pendingIntent = PendingIntent.getBroadcast(context,0, intent, 0);
        long alarmTimeAtUTC = System.currentTimeMillis() + s * 1000L;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMnager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, alarmTimeAtUTC, pendingIntent);
        }else {
            alarmMnager.setExact(android.app.AlarmManager.RTC_WAKEUP, alarmTimeAtUTC, pendingIntent);
        }
    }

    public static void stopAlarm(){
        try {
            alarmMnager.cancel(pendingIntent);
        }catch (Exception e){
            System.out.println("==================????? " + e.getMessage());
        }
    }
}
