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
import com.health.myhealth.model.UserModel;
import com.health.myhealth.utils.ListenerEventSensor;
import com.health.myhealth.activity.LoginActivity;
import com.health.myhealth.R;
import com.health.myhealth.utils.SensorManager;
import com.health.myhealth.utils.SharedPreferences;
import com.health.myhealth.utils.Utils;
//Dịch vụ chạy ngầm
public class ServiceCountStep extends android.app.Service implements ListenerEventSensor {
    private SensorManager sensorManager;
    private boolean isFirstRun = false;

    private int timeCountNoSenser = 0;
    private static final int TIME_IS_SLEEP = 3;
    private Handler handlerCountTime;
    private boolean stopHandlerCountTime = false;
    private boolean isHandlerCountTimeRun = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void init() {
        isFirstRun = true;
        //Khởi tạo bộ quản lý chuyển động
        sensorManager = new SensorManager(ServiceCountStep.this, this);
    }

    //Thông báo lên thanh statusBar của thiết bị các chỉ số
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


    //Thông báo các hoạt động khác (Báo người dùng nên vận động nếu nghỉ ngơi quá lâu)
    //title là tiêu đề của thông bóa, content là nội dung thông báo
    private void showNotication(String title, String content) {
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
                    .setContentTitle(title)  // the label of the entry
                    .setContentText(content)  // the contents of the entry
                    .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                    .build();

            startForeground(2, notification);
        }

    }

    //Hàm này sẽ đếm thời gian tính giờ ngủ. Khởi động khi app bắt đầu chạy ngầm
    //Điều kiện tính thời gian ngủ: Sau 10 phút không có hoạt động và trong thời gian từ 22h đến 6h
    //Nếu thỏa mãn điều kiện ngủ thì cứ sau 5 phút sẽ cộng vào thời gian ngủ

    private void startCountTime() {
        timeCountNoSenser = 0;
        if (handlerCountTime == null) {
            handlerCountTime = new Handler();
        }
        if (!isHandlerCountTimeRun) {
            handlerCountTime.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (stopHandlerCountTime) {
                        handlerCountTime.removeCallbacks(this);
                        stopHandlerCountTime = false;
                        isHandlerCountTimeRun = false;
                    } else {
                        isHandlerCountTimeRun = true;
                        timeCountNoSenser = timeCountNoSenser + 1;
                        //Nếu không hoạt động trong vòng 10 phút và trong thời gian ngủ sẽ tính là ngủ
                        if (timeCountNoSenser >= TIME_IS_SLEEP && Utils.checkTimeSleep()) {
                            getData();
                        }
                        //Nếu trong vòng 1h không hoạt động và ngài thời gian ngủ sẽ nhắc người dùng vận động (Thay đổi nội dung nhắc nhở nếu muốn)
                        if ((timeCountNoSenser % 12 == 0) && !Utils.checkTimeSleep()) {
                            showNotication("Bạn đã nghỉ ngơi trong một thời gian dài", "Hãy đứng lên và vận động cơ thể");
                        }
                        // 300000 mili giây = 5 phút
                        handlerCountTime.postDelayed(this, 300000);
                    }
                }
            }, 0);
        }
    }


    private void getData() {
        //Lấy các dữ liệu mới nhất trong bộ nhớ thiết bị
        String dateCurrent = Utils.getDateCurrent();
        String strData = SharedPreferences.getDataString(this, dateCurrent);
        if (!strData.equals("")) {
            UserModel.DataHealth dataHealth = new Gson().fromJson(strData, UserModel.DataHealth.class);
            UserModel.DataHealth dataHealthUpdate = new UserModel.DataHealth(dataHealth.getStep(), dataHealth.getBike(), (dataHealth.getSleep() + 300));
            SharedPreferences.setDataString(this, dateCurrent, new Gson().toJson(dataHealthUpdate));
        } else {
            isFirstRun = true;
            UserModel.DataHealth newData = new UserModel.DataHealth(0, 0, 0);
            SharedPreferences.setDataString(this, dateCurrent, new Gson().toJson(newData));
            getData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener();
        stopHandlerCountTime = true;
    }

    @Override
    public void eventSensor(int step, int run, long sleep, double calo, double quangDuong) {
        //Lắng nghe các dữ liệu gửi về từ bộ quản lý hoạt động
        if (step % 30 == 0 || isFirstRun) {
            isFirstRun = false;
            String dataHoatDong = "Hoạt động: " + step + " bước (" + Math.round(calo * 100.0) / 100.0 + "kcal, " + Math.round(quangDuong * 100.0) / 100.0 + "km)";
            notication(dataHoatDong, Utils.showTimeSleep(sleep));
        }
        startCountTime();
    }
}
