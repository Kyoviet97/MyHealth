package com.health.myhealth.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.health.myhealth.utils.AlarmManager;
import com.health.myhealth.utils.SharedPreferences;
import com.health.myhealth.utils.Utils;

public class ServiceAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == "COUNT_HEALTH_1023") {
            if (!Utils.isMyServiceRunning(ServiceCountStep.class, context) && SharedPreferences.getDataInt(context, "CHECK_LOGIN") == 1){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(new Intent(context, ServiceCountStep.class));
                    return;
                }
                context.startService(new Intent(context, ServiceCountStep.class));
            }else {

            }
            AlarmManager.setUp(context, 60);
        }
    }
}

