package com.health.myhealth;

import java.util.Calendar;

public class Utils {
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
}
