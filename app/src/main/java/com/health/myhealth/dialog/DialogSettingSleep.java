package com.health.myhealth.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;
import com.health.myhealth.R;
import com.health.myhealth.utils.SharedPreferences;

public class DialogSettingSleep extends AppCompatDialog implements View.OnClickListener {
    private int startSleep;
    private int stopSleep;
    private int dkTimeSleep;
    private String conttentSleep;
    private TextView txtTimeSleep;
    private TextView txtTimeNghiNgoi;
    private Button btnApplyDialog;
    private Button btnCancelDialog;
    private SeekBar seekBarStart;
    private SeekBar seekBarStop;
    private SeekBar seekBarDkSleep;
    private EditText edtContent;

    public DialogSettingSleep(Context context) {
        super(context, R.style.Theme_dialog_setup);
        setContentView(R.layout.dialog_sleep_demo);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.88);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.7);
        getWindow().setLayout(width, height);
        setCancelable(false);
        init();
        getData();
        seekbarChange();
    }


    private void init() {
        txtTimeSleep = findViewById(R.id.txt_time_sleep);
        txtTimeNghiNgoi = findViewById(R.id.txt_time_nghi_ngoi);
        seekBarStart = findViewById(R.id.seekbar_start_sleep);
        seekBarStop = findViewById(R.id.seekbar_stop_sleep);
        seekBarDkSleep = findViewById(R.id.seekbar_nghi_ngoi);
        btnApplyDialog = findViewById(R.id.btn_apply_setting_dialog);
        btnCancelDialog = findViewById(R.id.btn_cancel_seting);
        edtContent = findViewById(R.id.edt_content_notify);
        btnApplyDialog.setOnClickListener(this);
        btnCancelDialog.setOnClickListener(this);

    }

    private void getData() {
        this.startSleep = SharedPreferences.getDataInt(getContext(), "START_H_SLEEP");
        this.stopSleep = SharedPreferences.getDataInt(getContext(), "STOP_H_SLEEP");
        this.dkTimeSleep = SharedPreferences.getDataInt(getContext(), "DK_TIME_SLEEP");
        txtTimeNghiNgoi.setText("Bắt đầu sau " + dkTimeSleep +" phút không hoạt động");
        this.conttentSleep = SharedPreferences.getDataString(getContext(), "CONTENT_SLEEP");
        seekBarStart.setProgress(startSleep);
        seekBarStop.setProgress(stopSleep);
        seekBarDkSleep.setProgress(dkTimeSleep);
        edtContent.setText(conttentSleep);
        setTimeToTextView();
    }

    private void seekbarChange(){
        seekBarStart.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                startSleep = i;
                setTimeToTextView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarStop.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                stopSleep = i;
                setTimeToTextView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarDkSleep.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                dkTimeSleep = i;
                txtTimeNghiNgoi.setText("Bắt đầu sau " + i +" phút không hoạt động");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setTimeToTextView(){
        txtTimeSleep.setText("Bắt đầu từ: " + startSleep + " giờ" + "\nKết thúc vào lúc: " + stopSleep + " giờ");
    }

    private void onDismiss() {
        if (isShowing()) {
            dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_apply_setting_dialog:
                if (edtContent.getText().toString().equals("")){
                    edtContent.setError("Vui lòng điền nội dung thông báo");
                    return;
                }
                SharedPreferences.setDataInt(getContext(), "START_H_SLEEP", startSleep);
                SharedPreferences.setDataInt(getContext(), "STOP_H_SLEEP", stopSleep);
                SharedPreferences.setDataInt(getContext(), "DK_TIME_SLEEP", dkTimeSleep);
                SharedPreferences.setDataString(getContext(), "CONTENT_SLEEP", edtContent.getText().toString());
                onDismiss();
                break;

            case R.id.btn_cancel_seting:
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