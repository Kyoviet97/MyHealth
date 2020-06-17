package com.health.myhealth;

import android.content.Context;

public class SharedPreferences {
    public static void setDataString(Context context, String key, String dataString) {
        android.content.SharedPreferences sharedPref = context.getSharedPreferences("com.health.myhealth", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, dataString);
        editor.apply();
    }

    public static String getDataString(Context context, String key) {
        android.content.SharedPreferences sharedPref = context.getSharedPreferences("com.health.myhealth", Context.MODE_PRIVATE);
        String dataString = sharedPref.getString(key, "");
        return dataString;
    }


    public static void setDataInt(Context context, String key, int dataInt) {
        android.content.SharedPreferences sharedPref = context.getSharedPreferences("com.health.myhealth", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, dataInt);
        editor.apply();
    }

    public static int getDataInt(Context context, String key) {
        android.content.SharedPreferences sharedPref = context.getSharedPreferences("com.health.myhealth", Context.MODE_PRIVATE);
        int dataInt = sharedPref.getInt(key, 0);
        return dataInt;
    }

    public static void setDataLong(Context context, String key, long dataLong) {
        android.content.SharedPreferences sharedPref = context.getSharedPreferences("com.health.myhealth", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key, dataLong);
        editor.apply();
    }

    public static long getDataLong(Context context, String key) {
        android.content.SharedPreferences sharedPref = context.getSharedPreferences("com.health.myhealth", Context.MODE_PRIVATE);
        long dataLong = sharedPref.getLong(key, 0);
        return dataLong;
    }

    public static void clearAll(Context context) {
        android.content.SharedPreferences sharedPref = context.getSharedPreferences("com.health.myhealth", Context.MODE_PRIVATE);
        sharedPref.edit().clear().commit();
    }


}
