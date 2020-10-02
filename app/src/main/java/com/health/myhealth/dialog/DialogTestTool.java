package com.health.myhealth.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.google.gson.Gson;
import com.health.myhealth.R;
import com.health.myhealth.model.UserModel;
import com.health.myhealth.utils.SharedPreferences;
import com.health.myhealth.utils.Utils;

//Thông tin lịch sử hoạt động theo từ ngày
public class DialogTestTool extends AppCompatDialog implements View.OnClickListener{
    public OnClickItemDialog onClickItemDialog;
    public DialogTestTool(Context context, OnClickItemDialog onClickItemDialog) {
        super(context, R.style.Theme_dialog_history);
        setContentView(R.layout.main_dialog_test);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.6);
        getWindow().setLayout(width, height);
        setCancelable(true);
        this.onClickItemDialog = onClickItemDialog;
        init();
    }

    private void init() {
        findViewById(R.id.test_steep).setOnClickListener(this);
        findViewById(R.id.test_bike).setOnClickListener(this);
        findViewById(R.id.test_run).setOnClickListener(this);
        findViewById(R.id.test_sleep).setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.test_steep:
                onClickItemDialog.onClickItem(0);
                dismiss();
                break;

            case R.id.test_run:
                onClickItemDialog.onClickItem(1);
                dismiss();
                break;

            case R.id.test_bike:
                onClickItemDialog.onClickItem(2);
                dismiss();
                break;

            case R.id.test_sleep:
                onClickItemDialog.onClickItem(3);
                dismiss();
                break;
        }
    }

    public interface OnClickItemDialog{
        void onClickItem(int idItem);
    }
}
