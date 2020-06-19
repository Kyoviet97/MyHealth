package com.health.myhealth.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.google.gson.Gson;
import com.health.myhealth.utils.ListenerEventSensor;
import com.health.myhealth.activity.LoginActivity;
import com.health.myhealth.R;
import com.health.myhealth.utils.SensorManager;
import com.health.myhealth.utils.SharedPreferences;
import com.health.myhealth.utils.Utils;
import com.health.myhealth.model.UserModel;

import java.util.Calendar;

public class Service extends android.app.Service implements ListenerEventSensor {
    private static final int TIME_IS_SLEEP = 600000;
    private SensorManager sensorManager;

    private UserModel.DataHealth dataHealth;
    private Gson gson;
    private Handler handler;

    private boolean isRunSenserCountSleep = false;
    private boolean isSenser = false;
    private boolean isFirstRun = false;

    private int timeCountNoSenser = 0;
    private int STEP;
    private int RUN;
    private long SLEEP;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("===============>> onBind");
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("===============>> onCreate");
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void init() {
        isFirstRun = true;
        gson = new Gson();
        getData();
        sensorManager = new SensorManager(Service.this, this);
    }

    private void notication(String step, String sleep) {
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "service_my_health";

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Count Step",
                    NotificationManager.IMPORTANCE_NONE);

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, LoginActivity.class), 0);


            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_favorite)  // the status icon
                    .setWhen(System.currentTimeMillis())  // the time stamp
                    .setContentTitle(step)  // the label of the entry
                    .setContentText(sleep)  // the contents of the entry
                    .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                    .build();

            startForeground(2, notification);
        }

    }


    private boolean checkTimeSleep() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 22 || hour <= 6) {
            return true;
        } else {
            return false;
        }
    }

    private void countTimeStartSleep() {
        isRunSenserCountSleep = true;
        if (handler == null) {
            handler = new Handler();
        }
        handler.postAtTime(new Runnable() {
            @Override
            public void run() {
                if (isSenser) {
                    handler.removeCallbacks(this);
                    timeCountNoSenser = 0;
                    isSenser = false;
                } else {
                    timeCountNoSenser = timeCountNoSenser + 1;
                    System.out.println("====================>>TimeCountNoSenser: " + timeCountNoSenser);
                    System.out.println(timeCountNoSenser);
                    if (timeCountNoSenser >= TIME_IS_SLEEP) {
                        isRunSenserCountSleep = false;
                        //BAT DAU NGU O DAY
                    }
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isSenser = true;
        timeCountNoSenser = 0;

    }

    private void checkTimeCountSleep() {
        if (checkTimeSleep()) {
            if (timeCountNoSenser == 0 && !isRunSenserCountSleep) {
                countTimeStartSleep();
            } else {
                timeCountNoSenser = 0;
            }
        } else {
            isSenser = true;
        }
    }

    private void getData(){
        String strData = SharedPreferences.getDataString(Service.this, Utils.getDateCurrent());
        System.out.println("================>>>> " + strData);
        dataHealth = gson.fromJson(strData, UserModel.DataHealth.class);

        this.SLEEP = dataHealth.getSleep();
        this.STEP = dataHealth.getStep();
        this.RUN = dataHealth.getBike();
    }


    @Override
    public void eventSensor(int step, int run, int sleep, double calo, double quangDuong) {
        getData();
        checkTimeCountSleep();
        if (step % 30 == 0 || isFirstRun) {
            isFirstRun = false;
            String dataHoatDong = "Hoạt động: " + step + " bước (" + Math.round(calo * 100.0) / 100.0 + "kcal, " + Math.round(quangDuong * 100.0) / 100.0 + "km)";
            System.out.println("===============>>>> " + dataHoatDong);
            notication(dataHoatDong, Utils.showTimeSleep(SLEEP));
        }
    }
}
