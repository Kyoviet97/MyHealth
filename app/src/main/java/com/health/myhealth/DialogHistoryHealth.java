package com.health.myhealth;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

public class DialogHistoryHealth extends AppCompatDialog {
    private TextView txtHistoryDate;
    public DialogHistoryHealth(Context context) {
        super(context, R.style.Theme_dialog_history);
        setContentView(R.layout.dialog_history_main);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.5);
        getWindow().setLayout(width, height);
        setCancelable(true);
        init();
    }

    private void init() {
        txtHistoryDate = findViewById(R.id.txt_date_history);
    }

    public void onShowDialog(String date){
        if (!isShowing()){
            txtHistoryDate.setText(date);
            show();
        }
    }
}
