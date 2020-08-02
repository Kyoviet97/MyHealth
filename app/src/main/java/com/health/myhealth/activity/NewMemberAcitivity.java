package com.health.myhealth.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.gson.Gson;
import com.health.myhealth.R;
import com.health.myhealth.utils.SharedPreferences;
import com.health.myhealth.model.UserModel;
import java.util.ArrayList;
import java.util.List;

public class NewMemberAcitivity extends Activity implements View.OnClickListener {
    private EditText edtFullName;
    private EditText edtCanNang;
    private EditText edtDoTuoi;
    private EditText edtChieuCao;
    private Button btnHoanThanhTT;
    private String dateCurrent;
    private UserModel userModel;
    private List<UserModel.DateHealth> dateHealthList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_member_acitivity);
        Intent intent = getIntent();
        dateCurrent = intent.getStringExtra("dateCurrent");
        init();
        setOnClick();
    }

    private void init() {
        edtFullName = findViewById(R.id.edt_full_name);
        edtDoTuoi = findViewById(R.id.edt_tuoi);
        edtCanNang = findViewById(R.id.edt_can_nang);
        edtChieuCao = findViewById(R.id.edt_chieu_cao);
        btnHoanThanhTT = findViewById(R.id.btn_hoan_tat);
        userModel = new UserModel();
        dateHealthList = new ArrayList<>();
    }

    private void setOnClick() {
        btnHoanThanhTT.setOnClickListener(this);
    }

    private void validateThongTin() {
        //Kiểm tra thông tin mà người dùng nhập vào
        String fullName = edtFullName.getText().toString();
        String canNang = edtCanNang.getText().toString();
        String doTuoi = edtDoTuoi.getText().toString();
        String chieuCao = edtChieuCao.getText().toString();

        if (fullName.equals("")) {
            edtFullName.setError("Không được để trống");
        } else if (doTuoi.equals("")) {
            edtDoTuoi.setError("Không được để trống");
        } else if(Integer.parseInt(doTuoi) < 3){
            edtDoTuoi.setError("Tuổi không hợp lệ");
        }else if (chieuCao.equals("")) {
            edtChieuCao.setError("Không được để trống");
        } else if(Double.parseDouble(chieuCao) < 50){
            edtChieuCao.setError("Chiều cao không được nhỏ hơn 50cm");
        }else if (canNang.equals("")) {
            edtCanNang.setError("Không được để trống");
        } else if(Double.parseDouble(canNang) < 10){
            edtCanNang.setError("Cân nặng không được nhỏ hơn 10kg");
        } else {
            userModel.setUserName(fullName);
            userModel.setAge(doTuoi);
            userModel.setWeight(canNang);
            userModel.setHeight(chieuCao);
            dateHealthList.add(new UserModel.DateHealth(dateCurrent));
            userModel.getListDateHealth(dateHealthList);

            SharedPreferences.setDataString(this, "MY_DATA_HEALTH", new Gson().toJson(userModel));
            SharedPreferences.setDataInt(this, "CHECK_LOGIN", 1);
            SharedPreferences.setDataInt(this, "START_H_SLEEP", 22);
            SharedPreferences.setDataInt(this, "STOP_H_SLEEP", 6);
            SharedPreferences.setDataInt(this, "DK_TIME_SLEEP", 10);
            SharedPreferences.setDataString(this, "CONTENT_SLEEP", "Đã đến giờ đi ngủ");

            SharedPreferences.setDataInt(this, "DK_TIME_VD", 60);
            SharedPreferences.setDataString(this, "TITLE_VD", "Thông báo");
            SharedPreferences.setDataString(this, "CONTENT_VD", "Hãy đứng dạy và vận động cơ thể");
            startActivity(new Intent(this, HealthActivity.class));
            finish();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_hoan_tat:
                validateThongTin();
                break;
        }
    }
}
