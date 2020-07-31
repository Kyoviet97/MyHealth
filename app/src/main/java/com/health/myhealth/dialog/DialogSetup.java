package com.health.myhealth.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.google.gson.Gson;
import com.health.myhealth.R;
import com.health.myhealth.model.UserModel;
import com.health.myhealth.utils.SharedPreferences;
import com.health.myhealth.utils.Utils;

public class DialogSetup extends AppCompatDialog implements View.OnClickListener{
    private TextView txtCountSleep;
    private TextView txtTotalSleep;
    private Button btnStart;
    private UserModel.DataHealth newData;
    private boolean isStopCount = false;
    private Long soGiayNgu;
    private int timeCount;

    private Handler handlerCount;
    public DialogSetup(Context context) {
        super(context, R.style.Theme_dialog_setup);
        setContentView(R.layout.dialog_setup);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.88);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.5);
        getWindow().setLayout(width, height);
        timeCount = 10;
        init();
        getData();
    }

    private void init() {
        txtCountSleep = findViewById(R.id.txt_count_time_sleep);
        txtTotalSleep = findViewById(R.id.txt_total_sleep);
        btnStart = findViewById(R.id.btn_start_sleep_demo);

        handlerCount = new Handler();

        btnStart.setOnClickListener(this);
    }

    private void getData(){
        String dateCurrent = Utils.getDateCurrent();
        String strData = SharedPreferences.getDataString(getContext(), dateCurrent);
        newData = new Gson().fromJson(strData, UserModel.DataHealth.class);
        this.soGiayNgu = newData.getSleep();
    }

    private void startCountSleep(){
        txtTotalSleep.setText(Utils.showTimeSleep4(soGiayNgu));
        final Handler handlerCount = new Handler();
        handlerCount.postDelayed(new Runnable() {
            @Override
            public void run() {
                String dateCurrent = Utils.getDateCurrent();
                UserModel.DataHealth data = new UserModel.DataHealth(newData.getStep(), newData.getBike(), (newData.getSleep() + 5));
                SharedPreferences.setDataString(getContext(), dateCurrent, new Gson().toJson(data));
                soGiayNgu = soGiayNgu + 5;
                txtTotalSleep.setText(Utils.showTimeSleep4(soGiayNgu));
                handlerCount.postDelayed(this, 5000);
            }
        }, 0);
    }

    @Override
    public void onClick(View view) {
        handlerCount.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("================>>>> " + timeCount);
                if (isStopCount){
                    handlerCount.removeCallbacks(this);
                    txtCountSleep.setText("Bắt đầu tính giờ ngủ");
//                    startCountSleep();
                }else {
                    timeCount --;
                    if (timeCount == 0){
                        isStopCount = true;
                    }
                    txtCountSleep.setText("Giờ ngủ sẽ tính sau: " + timeCount + " giây");
                }
                handlerCount.postDelayed(this, 1000);
            }
        }, 0);
    }
}