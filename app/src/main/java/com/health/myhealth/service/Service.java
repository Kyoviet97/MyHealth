package com.health.myhealth.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.health.myhealth.LoginActivity;
import com.health.myhealth.R;
import com.health.myhealth.SharedPreferences;
import com.health.myhealth.model.UserModel;

import java.util.Calendar;

public class Service extends android.app.Service implements SensorEventListener {
    private static final int TIME_IS_SLEEP = 600000;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean isSensorPresent = false;
    private Calendar calendar;
    private UserModel.DataHealth dataHealth;
    private Gson gson;
    private String dateCurrent = "";
    private Handler handler;
    private int timeCountNoSenser = 0;
    private boolean isSenser = false;

    private int STEP = 0;
    private long SLEEP = 0;

    private long sleepStart = 0;
    private long sleepEnd = 0;

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

        gson = new Gson();
        getData();
        notication(reviewHealthToday(STEP), showTimeSleep(SLEEP));
        init();

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

    private String reviewHealthToday(int step) {
        double cal = (step * 0.05);
        double km = (step * 0.0008);

        return "Hoạt động: " + step + " bước (" + Math.round(cal * 100.0) / 100.0 + "kcal, " + Math.round(km * 100.0) / 100.0 + "km)";
    }

    private String showTimeSleep(long sleep) {
        long HSleep = (sleep / 60 / 60) % 60;
        long MSleep = (sleep / 60) % 60;

        String srtHSleep = String.valueOf(HSleep);
        String srtMSleep = String.valueOf(MSleep);


        if (srtHSleep.length() == 1) {
            srtHSleep = "0" + srtHSleep;
        }


        if (srtMSleep.length() == 1) {
            srtMSleep = "0" + srtMSleep;
        }

        return "Nghỉ ngơi: " + srtHSleep + "giờ " + srtMSleep + "phút";
    }

    private String getDateCurrent() {
        calendar = Calendar.getInstance();
        String DAY = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String MONTH = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String YEAR = String.valueOf(calendar.get(Calendar.YEAR));
        return DAY + "/" + MONTH + "/" + YEAR;
    }

    private void getData() {
        dateCurrent = getDateCurrent();
        dataHealth = gson.fromJson(SharedPreferences.getDataString(Service.this, dateCurrent), UserModel.DataHealth.class);
        if (dataHealth != null) {
            STEP = dataHealth.getStep();
            SLEEP = dataHealth.getSleep();

            System.out.println("=====================>>>>SLEEP: " + SLEEP);
        } else {
            STEP = 0;
            SLEEP = 0;
        }
    }

    private void init() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        checkSensorStep();
    }

    private void checkSensorStep() {
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

            isSensorPresent = true;
        } else {
            isSensorPresent = false;
        }

        if (isSensorPresent) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorChanged();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void sensorChanged() {
        if (!dateCurrent.equals(getDateCurrent())) {
            getData();
        }

        if (checkTimeSleep()) {
            if (timeCountNoSenser == 0) {
                countTimeStartSleep();
            } else {
                timeCountNoSenser = 0;
            }
        } else {
            isSenser = true;
        }

        setTimeSleep();

        SharedPreferences.setDataString(Service.this, dateCurrent, gson.toJson(new UserModel.DataHealth(STEP, 0, 0, SLEEP)));
        if (STEP % 30 == 0) {
            notication(reviewHealthToday(STEP), showTimeSleep(SLEEP));
        }
        STEP++;
    }

    private void setTimeSleep() {
        if (sleepStart != 0) {
            sleepEnd = System.currentTimeMillis();
            long countGiayNgu = ((sleepEnd - sleepStart) / 1000);
            SLEEP = SLEEP + countGiayNgu;
            sleepStart = 0;
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
                    System.out.println(timeCountNoSenser);
                    if (timeCountNoSenser >= TIME_IS_SLEEP) {
                        isSenser = true;
                        sleepStart = System.currentTimeMillis();
                        //hoan thanh
                    }
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        setTimeSleep();
        SharedPreferences.setDataString(Service.this, dateCurrent, gson.toJson(new UserModel.DataHealth(STEP, 0, 0, SLEEP)));

        isSenser = true;
        timeCountNoSenser = 0;
        if (isSensorPresent) {
            mSensorManager.unregisterListener(this);
        }
    }
}
