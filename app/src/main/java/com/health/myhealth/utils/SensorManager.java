package com.health.myhealth.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.google.gson.Gson;
import com.health.myhealth.model.UserModel;

import static android.content.Context.SENSOR_SERVICE;
public class SensorManager implements SensorEventListener, StepListener {
    private static SensorManager sensorManager;
    private Context context;

    private StepDetector simpleStepDetector;
    private android.hardware.SensorManager mSensorManager;
    private Sensor accelSensor;
    private Gson gson;
    private UserModel.DataHealth dataHealth;

    private boolean isPauseCountStep = false;

    private int STEP;
    private int RUN;
    private long SLEEP;

    private long BIKETIME;
    private Double BIKECALO;
    private Double BIKEKM;

    private String dateCurrent;
    private float chieuCao;
    private float canNang;
    private int soTuoi;

    private long timeNsOld;

    private ListenerEventSensor listenerEventSensor;

    public SensorManager(Context context, ListenerEventSensor listenerEventSensor) {
        this.context = context;
        this.listenerEventSensor = listenerEventSensor;
        isPauseCountStep = false;
        init();
    }

    private void init() {
        STEP = 0;
        RUN = 0;
        SLEEP = 0;

        BIKETIME = 0;

        gson = new Gson();
        getData();
        tongHopKetQua();
        mSensorManager = (android.hardware.SensorManager) context.getSystemService(SENSOR_SERVICE);
        accelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);
        mSensorManager.registerListener(SensorManager.this, accelSensor, android.hardware.SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void unregisterListener() {
        mSensorManager.unregisterListener(this);
    }

    public void setPauseCountStep(boolean isPause) {
        isPauseCountStep = isPause;
    }

    private void getData() {
        dateCurrent = Utils.getDateCurrent();

        String strDataProfile = SharedPreferences.getDataString(context, "MY_DATA_HEALTH");
        UserModel userModel = gson.fromJson(strDataProfile, UserModel.class);
        this.soTuoi = Integer.parseInt(userModel.getAge());
        this.canNang = Float.parseFloat(userModel.getWeight());
        this.chieuCao = Float.parseFloat(userModel.getHeight());

        String strData = SharedPreferences.getDataString(context, dateCurrent);
        if (!strData.equals("")) {
            dataHealth = gson.fromJson(strData, UserModel.DataHealth.class);
            if (dataHealth != null) {
                STEP = dataHealth.getStep();
                RUN = dataHealth.getRun();
                SLEEP = dataHealth.getSleep();

                BIKETIME = dataHealth.getTimeBike();
                BIKECALO = dataHealth.getCaloBike();
                BIKEKM = dataHealth.getKmBike();
            }
        } else {
            UserModel.DataHealth newData = new UserModel.DataHealth(0, 0,0, 0, 0.0, 0.0);
            SharedPreferences.setDataString(context, dateCurrent, new Gson().toJson(newData));
            getData();
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void step(long timeNs) {
        if (Conts.isBike){
            return;
        }
        if (!isPauseCountStep) {
            getData();
            STEP++;
            if ((timeNs - timeNsOld) < 400000000) {
                RUN++;
                SharedPreferences.setDataString(context, dateCurrent, gson.toJson(new UserModel.DataHealth(STEP, RUN, SLEEP, BIKETIME, BIKECALO, BIKEKM)));

            } else {
                SharedPreferences.setDataString(context, dateCurrent, gson.toJson(new UserModel.DataHealth(STEP, RUN, SLEEP, BIKETIME, BIKECALO, BIKEKM)));
            }
            timeNsOld = timeNs;
            tongHopKetQua();
        }
    }

    private void tongHopKetQua() {
        //Tổng hợp các dữ liệu và gửi về các đối tượng đang lắng nghe
        double caloDiBo = (STEP - RUN) * Utils.getCalo(chieuCao, canNang, soTuoi, false);
        double caloChay = RUN * Utils.getCalo(chieuCao, canNang, soTuoi, true);
        double kmDiBo = (STEP - RUN) * 0.00075;
        double kmChay = RUN * 0.00085;
        listenerEventSensor.eventSensor(STEP, RUN, SLEEP, (caloDiBo + caloChay + BIKECALO), (kmDiBo + kmChay + BIKEKM), BIKETIME);
    }
}
