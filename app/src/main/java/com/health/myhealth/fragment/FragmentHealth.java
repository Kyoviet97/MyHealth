package com.health.myhealth.fragment;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.health.myhealth.R;
import com.health.myhealth.SharedPreferences;
import com.health.myhealth.StepDetector;
import com.health.myhealth.StepListener;
import com.health.myhealth.Utils;
import com.health.myhealth.model.UserModel;

import java.util.Calendar;

import static android.content.Context.SENSOR_SERVICE;

public class FragmentHealth extends Fragment implements SensorEventListener, StepListener {
    private View rootView;

    private TextView txtSensor;
    private TextView reviewHealthToday;
    private Calendar calendar;
    private UserModel.DataHealth dataHealth;
    private Gson gson;
    private String dateCurrent = "";

    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accelSensor;
    private int numSteps;
    private int numBike;
    private long numSleep;

    private float chieuCao;
    private float canNang;
    private int soTuoi;

    private double BMR;

    private int x = 0;
    private long stepOne;
    private long stepTow;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        getData();
        this.BMR = 66.5 + (13.75 * canNang) + (5.003 * chieuCao) - ((6.755 * soTuoi));
        System.out.println("=====================>>>> bmr" + BMR);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_health, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setSensorStep();
        reviewHealthStep(numSteps);
    }

    private void init() {
        txtSensor = rootView.findViewById(R.id.txt_sensor);
        reviewHealthToday = rootView.findViewById(R.id.txt_review_health_today);
        txtSensor.setText(numSteps + "");
    }

    private void getData() {

        String strDataProfile = SharedPreferences.getDataString(getActivity(), "MY_DATA_HEALTH");
        UserModel userModel = gson.fromJson(strDataProfile, UserModel.class);
        this.soTuoi = Integer.parseInt(userModel.getAge());
        this.canNang = Float.parseFloat(userModel.getWeight());
        this.chieuCao = Float.parseFloat(userModel.getHeight());

        dateCurrent = Utils.getDateCurrent();
        String strData = SharedPreferences.getDataString(getActivity(), dateCurrent);
        if (!strData.equals("")){
            dataHealth = gson.fromJson(strData, UserModel.DataHealth.class);
            if (dataHealth != null){
                numSteps = dataHealth.getStep();
                numSleep = dataHealth.getSleep();
                numBike = dataHealth.getBike();
            }else {
                numSteps = 0;
                numSleep = 0;
                numBike = 0;
            }
        }else {
            numSteps = 0;
            numSleep = 0;
            numBike = 0;
        }

    }


    private void setSensorStep() {
        sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        sensorManager.registerListener(FragmentHealth.this, accelSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        if (!dateCurrent.equals(Utils.getDateCurrent())){
            getData();
        }
        txtSensor.setText("" + numSteps);

        numSteps++;

        if (x == 0){
            x = 1;
            stepOne = System.currentTimeMillis();
        }else {
            x = 0;
            stepTow = System.currentTimeMillis();
            if ((stepTow - stepOne) < 500){
                System.out.println("=======================>>> CHẠY");
                numBike++;
                reviewHealthBike(numSteps);
                SharedPreferences.setDataString(getActivity(), dateCurrent, gson.toJson(new UserModel.DataHealth(numSteps, numBike, numSleep)));

            }else {
                System.out.println("=======================>>> ĐI BỘ");
                reviewHealthBike(numSteps);
                SharedPreferences.setDataString(getActivity(), dateCurrent, gson.toJson(new UserModel.DataHealth(numSteps, numBike, numSleep)));

            }
        }

    }


    private void reviewHealthStep(int step){
        double cal = (step * 0.05);
        double km = (step * 0.0008);
        reviewHealthToday.setText( step + " bước (" + Math.round(cal * 100.0) / 100.0 +  "kcal, " + Math.round(km * 100.0) / 100.0 +  "km)");
    }

    private void reviewHealthBike(int step){
        double cal = (step * 0.08);
        double km = (step * 0.001);
        reviewHealthToday.setText( step + " bước (" + Math.round(cal * 100.0) / 100.0 +  "kcal, " + Math.round(km * 100.0) / 100.0 +  "km)");
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

}
