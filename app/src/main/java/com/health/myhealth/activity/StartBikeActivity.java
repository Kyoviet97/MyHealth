package com.health.myhealth.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.health.myhealth.R;
import com.health.myhealth.model.UserModel;
import com.health.myhealth.utils.CLocation;
import com.health.myhealth.utils.Conts;
import com.health.myhealth.utils.IBaseGpsListener;
import com.health.myhealth.utils.SharedPreferences;
import com.health.myhealth.utils.Utils;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

public class StartBikeActivity extends AppCompatActivity implements IBaseGpsListener {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 99;
    private TextView txtCurrentSpeed;
    private Double speedBike;
    private ArrayList<Double> arrayListSpeed;
    private Button btnStopBike;
    private int timeRun = 0;
    private Handler handler;
    private TextView txtTimeBike;
    private TextView txtKmBike;
    private Boolean isRunBike = true;
    private float canNang;
    private TextView txtCaloBike;
    private TextView textViewKQ;

    private Boolean isTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_start_bike);
        Intent intent = getIntent();
        isTest = intent.getBooleanExtra("TEST", false);
        Conts.isBike = true;

        init();

        if (ActivityCompat.checkSelfPermission(StartBikeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(StartBikeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(StartBikeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
            return;
        } else {
            setLocationManager();
        }

    }

    private void init() {
        txtCurrentSpeed = findViewById(R.id.txtCurrentSpeed);
        txtTimeBike = findViewById(R.id.txt_time_bike);
        txtCaloBike = findViewById(R.id.txt_calo_bike);
        btnStopBike = findViewById(R.id.btn_stop_bike);
        textViewKQ = findViewById(R.id.txt_speed_test);
        txtKmBike = findViewById(R.id.txt_km_bike);

        getDataBody();
        arrayListSpeed = new ArrayList();
        handler = new Handler();
        startRunBike();
        setOnClick();
        testMethod();
    }

    private void testMethod(){
        if (isTest){
            speedBike = 3.5;
            txtCurrentSpeed.setText(speedBike + "\nM/s");
            textViewKQ.setText("Chế độ Test với tốc độ: "  + speedBike + " m/s ");
            textViewKQ.setTextColor(Color.RED);

        }
    }

    private void getDataBody() {
        String strDataProfile = SharedPreferences.getDataString(this, "MY_DATA_HEALTH");
        UserModel userModel = new Gson().fromJson(strDataProfile, UserModel.class);
        this.canNang = Float.parseFloat(userModel.getWeight());
    }

    private void setOnClick() {
        btnStopBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRunBike();
            }
        });
    }

    private void stopRunBike() {
        isRunBike = false;
        txtCurrentSpeed.setText(0.0 + "\nM/s");
        txtTimeBike.setText("00:00");

        if (getAverageSpeed() == 0.0) {
            textViewKQ.setText("Không phát hiện vận tốc di chuyển, hãy chắc chắn bạn đạp xe ngoài trời và dịch vụ GPS hoạt động");
            btnStopBike.setVisibility(View.GONE);
            return;
        }

        textViewKQ.setText("Tốc độ trung bình: " + getAverageSpeed() + " M/s" + "\n"
                + "Thời gian đạp xe: " + timeRunBike() + " phút" + "\n"
                + "Quãng đường đi chuyển: " + getDistance() + " Km");

        getData(new DataHeath() {
            @Override
            public void dataHealth(int step, int run, long bikeTime, double bikeCalo, double bikeKm, long sleep) {
                String dateCurrent = Utils.getDateCurrent();
                double totalBikeCalo = bikeCalo + getCaloBike(getAverageSpeed());
                double totalBikeKm = bikeKm + getDistanceDb();
                long totalTime = bikeTime + timeRun;

                SharedPreferences.setDataString(StartBikeActivity.this, dateCurrent, new Gson().toJson(new UserModel.DataHealth(step, run, sleep, totalTime, totalBikeCalo, totalBikeKm)));

            }
        });
        txtTimeBike.setText(Utils.showTimeSleepMinute(timeRun));
        txtKmBike.setText(getDistance());
        txtCaloBike.setText(String.valueOf(getCaloBike(getAverageSpeed())));
        btnStopBike.setVisibility(View.GONE);
    }

    private void getData(DataHeath dataHeathCallback) {
        String dateCurrent = Utils.getDateCurrent();
        String strData = SharedPreferences.getDataString(this, dateCurrent);
        if (!strData.equals("")) {
            UserModel.DataHealth dataHealth = new Gson().fromJson(strData, UserModel.DataHealth.class);
            if (dataHealth != null) {
                int STEP = dataHealth.getStep();
                int RUN = dataHealth.getRun();
                long SLEEP = dataHealth.getSleep();
                long BIKE_TIME = dataHealth.getTimeBike();
                double BIKE_CALO = dataHealth.getCaloBike();
                double BIKE_KM = dataHealth.getKmBike();

                dataHeathCallback.dataHealth(STEP, RUN, BIKE_TIME, BIKE_CALO, BIKE_KM, SLEEP);

            }
        } else {
            UserModel.DataHealth newData = new UserModel.DataHealth(0, 0, 0, 0, 0.0, 0.0);
            SharedPreferences.setDataString(this, dateCurrent, new Gson().toJson(newData));
            getData(dataHeathCallback);
        }
    }

    private String getDistance() {
        Double distance = ((getAverageSpeed() * 60) * timeRunBike() * 0.001);
        return String.valueOf(Math.round(distance * 100.0) / 100.0);
    }

    private Double getDistanceDb() {
        Double distance = ((getAverageSpeed() * 60) * timeRunBike() * 0.001);
        return Math.round(distance * 100.0) / 100.0;
    }

    private Double timeRunBike() {
        Double time = timeRun / 60D;
        return Math.round(time * 100.0) / 100.0;
    }

    private Double getAverageSpeed() {
        if (isTest){
            return speedBike;
        }
        Double sum = 0.0;
        if (!arrayListSpeed.isEmpty()) {
            for (Double mark : arrayListSpeed) {
                sum += mark;
            }
            Double sunAverage = sum.doubleValue() / arrayListSpeed.size();
            return Math.round(sunAverage * 100.0) / 100.0;
        }
        return sum;
    }


    private Double getCaloBike(Double speed) {
        Double calo;
        Double heSo;

        if (speed > 5.3) {
            heSo = 7.0;
        } else if (speed > 4.2) {
            heSo = 5.8;
        } else if (speed > 2.4) {
            heSo = 3.5;
        } else {
            heSo = 1.0;
        }

        calo = ((heSo * canNang * 3.5) / 200) * (timeRun / 60D);
        return Math.round(calo * 100.0) / 100.0;
    }

    @SuppressLint("MissingPermission")
    private void setLocationManager() {
        if (isTest){
            return;
        }
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        this.updateSpeed(null);
    }

    private void startRunBike() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isRunBike) {
                    handler.removeCallbacks(this);
                } else {
                    timeRun++;
                    txtKmBike.setText(getDistance());
                    txtCaloBike.setText(getCaloBike(getAverageSpeed()) + "");
                    handler.postDelayed(this, 1000);
                    txtTimeBike.setText(getTimeBike(timeRun));
                }

            }
        }, 0);
    }

    public void finish() {
        super.finish();
    }

    private String getTimeBike(int time) {
        String strTime = "00:00";

        String phut = String.valueOf((time / 60) % 60);
        if (phut.length() == 1) {
            phut = "0" + phut;
        }

        String giay = String.valueOf(time % 60);
        if (giay.length() == 1) {
            giay = "0" + giay;
        }

        strTime = phut + ":" + giay;

        return strTime;
    }

    private void updateSpeed(CLocation location) {
        float nCurrentSpeed = 0;
        if (location != null) {
            location.setUseMetricunits(true);
            nCurrentSpeed = location.getSpeed();
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.CHINA, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(" ", "");


        try {
            if (speedBike != Double.parseDouble(strCurrentSpeed)) {
                arrayListSpeed.add(speedBike);
            }
        } catch (Exception e) {

        }

        speedBike = Double.parseDouble(strCurrentSpeed);

        txtCurrentSpeed.setText(strCurrentSpeed + "\nM/s");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            CLocation myLocation = new CLocation(location, true);
            if (isRunBike && !isTest) {
                this.updateSpeed(myLocation);
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {


    }

    @Override
    public void onProviderEnabled(String provider) {


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {


    }

    @Override
    public void onGpsStatusChanged(int event) {


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLocationManager();
                } else {
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private interface DataHeath {
        void dataHealth(int step, int run, long bikeTime, double bikeCalo, double bikeKm, long sleep);
    }

    @Override
    public void onBackPressed() {
        if (isRunBike) {
            stopRunBike();
        } else {
            Intent previousScreen = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(previousScreen);
            finish();
        }
    }
}