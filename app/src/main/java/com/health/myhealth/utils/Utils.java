package com.health.myhealth.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.Calendar;

public class Utils {
    //Lấy thông tin ngày tháng năm hiện tại
    public static String getDateCurrent(){
        Calendar calendar = Calendar.getInstance();
        String DAY = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String MONTH = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String YEAR = String.valueOf(calendar.get(Calendar.YEAR));

        if (DAY.length() == 1){
            DAY = "0" + DAY;
        }

        if (MONTH.length() == 1){
            MONTH = "0" + MONTH;
        }

        return DAY + "/" + MONTH + "/" + YEAR;
    }

    public static float harrisBenedictRmr(int gender, float weightKg, float age, float heightCm) {
        if (gender == 0) {
            return 655.0955f + (1.8496f * heightCm) + (9.5634f * weightKg) - (4.6756f * age);
        } else {
            return 66.4730f + (5.0033f * heightCm) + (13.7516f * weightKg) - (6.7550f * age);
        }
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public static boolean checkTimeSleep(Context context) {
        int start = SharedPreferences.getDataInt(context, "START_H_SLEEP");
        int stop = SharedPreferences.getDataInt(context, "STOP_H_SLEEP");
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        boolean dkSleep = false;

        if (start > stop){
            if (hour >= start || hour < stop) {
                dkSleep = true;
            }
        }else {
            if (hour >= start && hour < stop){
                dkSleep = true;
            }
        }

        return dkSleep;

    }

    public static String showTimeSleep(long sleep) {
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

    public static String showTimeSleep2(long sleep) {
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

        return srtHSleep + ":" + srtMSleep;
    }

    public static String showTimeSleepMinute(long sleep) {
        Long minute = (sleep / 60);
        return minute.toString();
    }

    public static String showTimeSleep3(long sleep) {
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

        return srtHSleep + "giờ " + srtMSleep + "phút";
    }

    public static String showTimeSleep4(long sleep) {
        long HSleep = (sleep / 60 / 60) % 60;
        long MSleep = (sleep / 60) % 60;
        long SSleep = sleep % 60;

        String srtHSleep = String.valueOf(HSleep);
        String srtMSleep = String.valueOf(MSleep);
        String srtSSleep = String.valueOf(SSleep);

        if (srtHSleep.length() == 1) {
            srtHSleep = "0" + srtHSleep;
        }

        if (srtMSleep.length() == 1) {
            srtMSleep = "0" + srtMSleep;
        }

        if (srtSSleep.length() == 1) {
            srtSSleep = "0" + srtSSleep;
        }


        return srtHSleep + ":" + srtMSleep + ":" + srtSSleep;
    }

//    public static void pushNotify(Context context, String title, String content){
//        NotificationHelper notificationHelper = new NotificationHelper(context);
//        notificationHelper.createNotification(title, content);
//    }


    //Công thức tính calo tiêu thụ
    public static double getCalo(double chieuCao, double canNang, int tuoi, boolean isRun){
        double calo = 0;
        int vanToc = 5;
        if (isRun){
            vanToc = 10;
        }
        double chieuCaoX2 =  (chieuCao/100) * (chieuCao/100);
        double bmi = (canNang/chieuCaoX2);
        calo = .04 * (bmi + (vanToc * 0.621371192)) / tuoi;
        return calo;
    }

}
