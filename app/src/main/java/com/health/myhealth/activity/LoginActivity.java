package com.health.myhealth.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.health.myhealth.R;
import com.health.myhealth.utils.SharedPreferences;
import com.health.myhealth.utils.Utils;
import com.health.myhealth.model.UserModel;
import com.health.myhealth.service.ScreenReceiver;
import com.health.myhealth.service.Service;

import java.util.List;

public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText edtUserName;
    private EditText edtPassWord;
    private Button btnLogin;
    private String dateCurrent;
    private String dataHealth;
    private UserModel userModel;
    private List<UserModel.DateHealth> dateHealthList;
    private BroadcastReceiver mReceiver = null;
    private boolean isOffScreen = true;

    private LinearLayout mainLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        getDate();
        setOnClick();
        checkScreenOff();
        checkLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkServiceRun();
        isOffScreen = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
            if (isOffScreen && ScreenReceiver.wasScreenOn){
                System.out.println("====================>>>> SCREEN TURNED OFF");
                getData();
        }
    }

    private void checkLogin(){
        int isLogin = SharedPreferences.getDataInt(this, "CHECK_LOGIN");
        if (isLogin == 1){
            getDate();
            checkDate();
        }else {
            mainLogin.setVisibility(View.VISIBLE);
        }
    }

    private void getData(){
        dataHealth = SharedPreferences.getDataString(this, "MY_DATA_HEALTH");
    }

    private void checkScreenOff(){
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }

    private void checkServiceRun() {
        if (isMyServiceRunning(Service.class)){
            stopService(new Intent(this, Service.class));
        }
    }

    private void init() {
        mainLogin = findViewById(R.id.main_login);
        edtUserName = findViewById(R.id.edt_user_name);
        edtPassWord = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);
    }

    private void getDate() {
        dateCurrent = Utils.getDateCurrent();
    }

    private void setOnClick() {
        btnLogin.setOnClickListener(this);
    }

    private void validateLogin() {
        String userName = edtUserName.getText().toString();
        String passWord = edtPassWord.getText().toString();

        if (userName.equals("")) {
            edtUserName.setError("Không được để trống");
        } else if (passWord.equals("")) {
            edtPassWord.setError("Không được để trống");
        } else if (userName.equals("admin") && passWord.equals("123")) {
            isOffScreen = false;
            getData();
            if (dataHealth.equals("")) {
                isOffScreen = false;
                Intent intentHealth = new Intent(this, NewMemberAcitivity.class);
                intentHealth.putExtra("dateCurrent", dateCurrent);
                startActivity(intentHealth);
            } else {
                SharedPreferences.setDataInt(this, "CHECK_LOGIN", 1);
                checkDate();
            }
        }

    }

    private void checkDate() {
        getData();
        userModel = new Gson().fromJson(dataHealth, UserModel.class);
        dateHealthList = userModel.getListDateHealth();

        for (int i = 0; i <= dateHealthList.size(); i++) {
            if (i < dateHealthList.size()) {
                System.out.println("===============>>>> " + i + "/" + dateHealthList.size());
                if (dateHealthList.get(i).getDate().equals(dateCurrent)) {
                    goToHealthMain();
                    return;
                }
            } else {
                System.out.println("================>>>> max");
                dateHealthList.add(new UserModel.DateHealth(dateCurrent));
                userModel.getListDateHealth(dateHealthList);
                SharedPreferences.setDataString(this, "MY_DATA_HEALTH", new Gson().toJson(userModel));
                goToHealthMain();
                return;
            }
        }
    }

    private void goToHealthMain() {
        Intent intentHealth = new Intent(this, HealthActivity.class);
        startActivity(intentHealth);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                validateLogin();
                break;
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("=====================>>> LOGIN onDestroy");

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }
}