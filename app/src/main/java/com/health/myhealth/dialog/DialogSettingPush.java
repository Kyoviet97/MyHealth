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

public class DialogSettingPush extends AppCompatDialog implements View.OnClickListener {
    private int timeDkVD;
    private String titleNotify;
    private String contentNotify;

    private TextView txtTimeVD;
    private Button btnApply;
    private Button btnCancel;
    private SeekBar seekBarTimeVd;
    private EditText edtTitleVD;
    private EditText edtContentVD;

    public DialogSettingPush(Context context) {
        super(context, R.style.Theme_dialog_setup);
        setContentView(R.layout.dialog_push);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.88);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.5);
        getWindow().setLayout(width, height);
        setCancelable(false);
        init();
        getData();
        setSeekBarChange();
    }

    private void init() {
        txtTimeVD = findViewById(R.id.txt_time_van_dong);
        btnApply = findViewById(R.id.btn_apply_push_demo);
        btnCancel = findViewById(R.id.btn_cancel_push_demo);
        seekBarTimeVd = findViewById(R.id.seekbar_van_dong);
        edtTitleVD = findViewById(R.id.edt_title_noty_vd);
        edtContentVD = findViewById(R.id.edt_content_noty_vd);
        btnApply.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    private void getData() {
        this.timeDkVD = SharedPreferences.getDataInt(getContext(), "DK_TIME_VD");
        this.titleNotify = SharedPreferences.getDataString(getContext(), "TITLE_VD");
        this.contentNotify = SharedPreferences.getDataString(getContext(), "CONTENT_VD");
        setTimeToTextView();
        seekBarTimeVd.setProgress(timeDkVD);
        edtTitleVD.setText(titleNotify);
        edtContentVD.setText(contentNotify);
    }

    private void setTimeToTextView() {
        txtTimeVD.setText("Nhắc sau khi nghỉ ngơi: " + timeDkVD + " phút");
    }

    private void setSeekBarChange() {
        seekBarTimeVd.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                timeDkVD = i;
                setTimeToTextView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void onDismiss() {
        if (isShowing()) {
            dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onDismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_apply_push_demo:
                if (edtTitleVD.getText().toString().equals("")) {
                    edtTitleVD.setError("Vui lòng điền tiêu đề thông báo");
                } else if (edtContentVD.getText().toString().equals("")) {
                    edtContentVD.setError("Vui lòng điền nội dung thông báo");
                } else {
                    SharedPreferences.setDataInt(getContext(), "DK_TIME_VD", timeDkVD);
                    SharedPreferences.setDataString(getContext(), "TITLE_VD", edtTitleVD.getText().toString());
                    SharedPreferences.setDataString(getContext(), "CONTENT_VD", edtContentVD.getText().toString());
                    onDismiss();
                }
                break;

            case R.id.btn_cancel_push_demo:
                onDismiss();
                break;
        }
    }
}
