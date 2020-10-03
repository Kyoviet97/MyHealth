package com.health.myhealth.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.google.gson.Gson;
import com.health.myhealth.R;
import com.health.myhealth.model.UserModel;
import com.health.myhealth.utils.SharedPreferences;
import com.health.myhealth.utils.Utils;

public class DialogSetTime extends AppCompatDialog implements View.OnClickListener{
    private int timeNotifiSleep = 5;
    private int timeStartSleep = 5;
    private SeekBar seekBarTime;
    private TextView txtShowTime;

    private TextView txtTimeStart;
    private SeekBar startTimeSb;

    public OnClickItemDialog onClickItemDialog;
    public DialogSetTime(Context context, OnClickItemDialog onClickItemDialog) {
        super(context, R.style.Theme_dialog_history);
        setContentView(R.layout.dialog_set_time);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.6);
        getWindow().setLayout(width, height);
        setCancelable(true);
        this.onClickItemDialog = onClickItemDialog;
        init();
        setUpSeekBar();
    }


    private void setUpSeekBar(){
        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i < 5){
                    timeNotifiSleep = 5;
                    seekBarTime.setProgress(5);
                }else {
                    timeNotifiSleep = i;
                }
                txtShowTime.setText("Nhắc nhở nghỉ ngơi sau: " + timeNotifiSleep + "s");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        startTimeSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i < 5){
                    timeStartSleep = 5;
                    startTimeSb.setProgress(5);
                }else {
                    timeStartSleep = i;
                }
                txtTimeStart.setText("Bắt đầu tính thời gian ngủ sau: " + timeStartSleep + "s nếu thiết bị không hoạt động");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    };

    private void init() {
        seekBarTime = findViewById(R.id.seekbar_time_sleep_test);
        seekBarTime.setProgress(5);
        txtShowTime = findViewById(R.id.time_sleep_test);

        txtTimeStart = findViewById(R.id.time_sleep_start);
        startTimeSb = findViewById(R.id.seekbar_time_start_sleep);
        startTimeSb.setProgress(5);

        findViewById(R.id.start_test_notifi_sleep).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_test_notifi_sleep:
                onClickItemDialog.onClickItem(timeNotifiSleep, timeStartSleep);
                dismiss();
                break;
        }
    }

    public interface OnClickItemDialog{
        void onClickItem(int time, int timeStart);
    }
}
