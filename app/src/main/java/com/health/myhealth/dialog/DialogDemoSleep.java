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
import com.health.myhealth.utils.Conts;
import com.health.myhealth.utils.SharedPreferences;
import com.health.myhealth.utils.Utils;

public class DialogDemoSleep extends AppCompatDialog implements View.OnClickListener {
    private TextView txtCountSleep;
    private TextView txtTotalSleep;
    private Button btnStart;
    private Button btnStop;
    private UserModel.DataHealth newData;
    private boolean isStopCount = false;
    private Long soGiayNgu;
    private Handler handlerCountStart;
    private Handler handlerCount;

    public DialogDemoSleep(Context context) {
        super(context, R.style.Theme_dialog_setup);
        setContentView(R.layout.dialog_sleep_demo);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.88);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.5);
        getWindow().setLayout(width, height);
        setCancelable(false);
        Conts.timeCountSleepDemo = 10;
        init();
        getData();
    }


    private void init() {
        txtCountSleep = findViewById(R.id.txt_count_time_sleep);
        txtTotalSleep = findViewById(R.id.txt_total_sleep);
        btnStart = findViewById(R.id.btn_start_sleep_demo);
        btnStop = findViewById(R.id.btn_stop_sleep_demo);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    private void getData() {
        String dateCurrent = Utils.getDateCurrent();
        String strData = SharedPreferences.getDataString(getContext(), dateCurrent);
        newData = new Gson().fromJson(strData, UserModel.DataHealth.class);
        this.soGiayNgu = newData.getSleep();
        txtTotalSleep.setText("Tổng giờ ngủ: " + Utils.showTimeSleep4(soGiayNgu));
        System.out.println("==================>>>> " + new Gson().toJson(newData));
    }

    private void startCountSleep() {
        txtTotalSleep.setText("Tổng giờ ngủ: " + Utils.showTimeSleep4(soGiayNgu));
        if (handlerCount == null) {
            handlerCount = new Handler();
        }
        handlerCount.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isStopCount || Conts.timeCountSleepDemo != 0) {
                    handlerCount.removeCallbacks(this);
                    if (isShowing()){
                        startCount();
                    }
                } else {
                    String dateCurrent = Utils.getDateCurrent();
                    soGiayNgu = soGiayNgu + 2;
                    UserModel.DataHealth data = new UserModel.DataHealth(newData.getStep(), newData.getBike(), soGiayNgu);
                    SharedPreferences.setDataString(getContext(), dateCurrent, new Gson().toJson(data));
                    txtTotalSleep.setText("Tổng giờ ngủ: " + Utils.showTimeSleep4(soGiayNgu));
                    handlerCount.postDelayed(this, 2000);
                }
            }
        }, 0);
    }

    private void startCount() {
        if (handlerCountStart == null) {
            handlerCountStart = new Handler();
        }
        handlerCountStart.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Conts.timeCountSleepDemo == 0) {
                    handlerCountStart.removeCallbacks(this);
                    txtCountSleep.setText("Bắt đầu tính giờ ngủ");
                    startCountSleep();
                } else {
                    Conts.timeCountSleepDemo--;
                    txtCountSleep.setText("Giờ ngủ sẽ tính sau: " + Conts.timeCountSleepDemo + " giây");
                    handlerCountStart.postDelayed(this, 1000);
                }
            }
        }, 0);
    }

    private void onDismiss() {
        isStopCount = true;
        Conts.timeCountSleepDemo = 0;
        if (isShowing()) {
            dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_sleep_demo:
                btnStart.setEnabled(false);
                startCount();
                break;

            case R.id.btn_stop_sleep_demo:
                onDismiss();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onDismiss();
    }

}