package com.health.myhealth.fragment;

import android.content.Context;
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
import com.health.myhealth.model.UserModel;

import java.util.Calendar;

public class FragmentHealth extends Fragment implements SensorEventListener {
    private View rootView;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean isSensorPresent = false;
    private TextView txtSensor;
    private TextView reviewHealthToday;
    private Calendar calendar;
    private UserModel.DataHealth dataHealth;
    private Gson gson;
    private int step = 0;
    private String dateCurrent = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        getData();
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
        checkSensorStep();
    }

    private void init() {
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        txtSensor = rootView.findViewById(R.id.txt_sensor);
        reviewHealthToday = rootView.findViewById(R.id.txt_review_health_today);
    }

    private void getData() {
        dateCurrent = getDateCurrent();
        dataHealth = gson.fromJson(SharedPreferences.getDataString(getActivity(), dateCurrent), UserModel.DataHealth.class);
        if (dataHealth != null){
            step = dataHealth.getStep();
        }else {
            step = 0;
        }
    }

    private String getDateCurrent(){
        calendar = Calendar.getInstance();
        String DAY = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String MONTH = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String YEAR = String.valueOf(calendar.get(Calendar.YEAR));
        return DAY + "/" + MONTH + "/" + YEAR;
    }

    private void checkSensorStep() {
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        } else {
            isSensorPresent = false;
        }

        if (isSensorPresent) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!dateCurrent.equals(getDateCurrent())){
            getData();
        }
        txtSensor.setText("" + step);
        reviewHealthToday(step);
        SharedPreferences.setDataString(getActivity(), dateCurrent, gson.toJson(new UserModel.DataHealth(step, 0, 0, 0)));
        step++;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void reviewHealthToday(int step){
        double cal = (step * 0.05);
        double km = (step * 0.0008);
        reviewHealthToday.setText( step + " bước (" + Math.round(cal * 100.0) / 100.0 +  "kcal, " + Math.round(km * 100.0) / 100.0 +  "km)");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isSensorPresent) {
            mSensorManager.unregisterListener(this);
        }
    }

}
