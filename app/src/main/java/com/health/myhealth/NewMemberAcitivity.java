package com.health.myhealth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.gson.Gson;
import com.health.myhealth.model.UserModel;
import java.util.ArrayList;
import java.util.List;

public class NewMemberAcitivity extends Activity implements View.OnClickListener {
    private EditText edtFullName;
    private EditText edtCanNang;
    private EditText edtDoTuoi;
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
        System.out.println("==================>>>> " + dateCurrent);
        init();
        setOnClick();
    }

    private void init() {
        edtFullName = findViewById(R.id.edt_full_name);
        edtDoTuoi = findViewById(R.id.edt_tuoi);
        edtCanNang = findViewById(R.id.edt_can_nang);
        btnHoanThanhTT = findViewById(R.id.btn_hoan_tat);
        userModel = new UserModel();
        dateHealthList = new ArrayList<>();
    }

    private void setOnClick() {
        btnHoanThanhTT.setOnClickListener(this);
    }

    private void validateThongTin() {
        String fullName = edtFullName.getText().toString();
        String canNang = edtCanNang.getText().toString();
        String doTuoi = edtDoTuoi.getText().toString();

        if (fullName.equals("")) {
            edtFullName.setError("Không được để trống");
        } else if (doTuoi.equals("")) {
            edtDoTuoi.setError("Không được để trống");
        } else if (canNang.equals("")) {
            edtCanNang.setError("Không được để trống");
        } else {

            userModel.setUserName(fullName);
            userModel.setAge(doTuoi);
            userModel.setWeight(canNang);
            dateHealthList.add(new UserModel.DateHealth(dateCurrent));
            userModel.getListDateHealth(dateHealthList);

            SharedPreferences.setDataString(this, "MY_DATA_HEALTH", new Gson().toJson(userModel));

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
