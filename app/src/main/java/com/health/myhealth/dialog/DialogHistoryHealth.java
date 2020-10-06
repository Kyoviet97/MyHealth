package com.health.myhealth.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.google.gson.Gson;
import com.health.myhealth.R;
import com.health.myhealth.model.UserModel;
import com.health.myhealth.utils.SharedPreferences;
import com.health.myhealth.utils.Utils;

//Thông tin lịch sử hoạt động theo từ ngày
public class DialogHistoryHealth extends AppCompatDialog {
    private UserModel.DataHealth dataHealth;
    private Context context;

    private TextView txtHistoryDate;
    private TextView txtStep;
    private TextView txtCalo;
    private TextView txtLong;
    private TextView txtSleep;

    private TextView txtRun;
    private TextView txtTimeBike;

    private String dateHistory = "";

    private int STEP;
    private int RUN;
    private long SLEEP;

    private Long BIKETIME;
    private double BIKECALO;
    private double BIKEKM;

    private float chieuCao;
    private float canNang;
    private int soTuoi;

    public DialogHistoryHealth(Context context) {
        super(context, R.style.Theme_dialog_history);
        setContentView(R.layout.dialog_history_main);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.8);
        getWindow().setLayout(width, height);
        setCancelable(true);
        this.context = context;
        getProfile();
        init();
    }

    private void init() {
        txtHistoryDate = findViewById(R.id.txt_date_history);
        txtStep = findViewById(R.id.txt_step);
        txtCalo = findViewById(R.id.txt_calo);
        txtLong = findViewById(R.id.txt_long);
        txtSleep = findViewById(R.id.txt_sleep);

        txtRun = findViewById(R.id.txt_run_step_history);
        txtTimeBike = findViewById(R.id.txt_time_bike_history);
    }

    private void getProfile(){
        String strDataProfile = SharedPreferences.getDataString(context, "MY_DATA_HEALTH");
        UserModel userModel = new Gson().fromJson(strDataProfile, UserModel.class);
        this.soTuoi = Integer.parseInt(userModel.getAge());
        this.canNang = Float.parseFloat(userModel.getWeight());
        this.chieuCao = Float.parseFloat(userModel.getHeight());
    }

    private void getData() {
        String strDataHealth = SharedPreferences.getDataString(context, dateHistory);
        dataHealth = new Gson().fromJson(strDataHealth, UserModel.DataHealth.class);

        STEP = dataHealth.getStep();
        SLEEP = dataHealth.getSleep();
        RUN = dataHealth.getRun();

        BIKETIME = dataHealth.getTimeBike();
        BIKECALO = dataHealth.getCaloBike();
        BIKEKM = dataHealth.getKmBike();

        double caloDiBo = (STEP - RUN) * Utils.getCalo(chieuCao, canNang, soTuoi, false);
        double caloChay = RUN * Utils.getCalo(chieuCao, canNang, soTuoi, true);

        double kmDiBo = (STEP - RUN) * 0.00075;
        double kmChay = RUN * 0.00085;

        txtStep.setText((STEP - RUN) + " bước");
        txtRun.setText(RUN + " bước");
        txtTimeBike.setText(Utils.showTimeSleepMinute(BIKETIME) + " phút");

        txtSleep.setText(Utils.showTimeSleepMinute(SLEEP) + " phút");
        txtCalo.setText(Math.round((caloChay + caloDiBo + BIKECALO) * 100.0) / 100.0 + " calo");
        txtLong.setText(Math.round((kmChay + kmDiBo + BIKEKM) * 100.0) / 100.0 + " km");
    }

    public void onShowDialog(String date){
        this.dateHistory = date;
        if (!isShowing()){
            txtHistoryDate.setText(date);
            getData();
            show();
        }
    }
}
