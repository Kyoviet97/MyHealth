package com.health.myhealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.health.myhealth.model.UserModel;

import java.util.Calendar;
import java.util.List;

public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText edtUserName;
    private EditText edtPassWord;
    private Button btnLogin;
    private Calendar calendar;
    private String dateCurrent;
    private String dataHealth;
    private UserModel userModel;
    private List<UserModel.DateHealth> dateHealthList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        getDate();
        setOnClick();
    }

    private void init() {
        edtUserName = findViewById(R.id.edt_user_name);
        edtPassWord = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);
        calendar = Calendar.getInstance();
    }

    private void getDate() {
        String DAY = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String MONTH = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String YEAR = String.valueOf(calendar.get(Calendar.YEAR));
        dateCurrent = DAY + "/" + MONTH + "/" + YEAR;
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
            dataHealth = SharedPreferences.getDataString(this, "MY_DATA_HEALTH");
            if (dataHealth.equals("")) {
                Intent intentHealth = new Intent(this, NewMemberAcitivity.class);
                intentHealth.putExtra("dateCurrent", dateCurrent);
                startActivity(intentHealth);
            } else {
                checkDate();
            }
        }

    }

    private void checkDate() {
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                validateLogin();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
