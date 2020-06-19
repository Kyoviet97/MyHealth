package com.health.myhealth.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.google.gson.Gson;
import com.health.myhealth.model.UserModel;

import static android.content.Context.SENSOR_SERVICE;
public class SensorManager implements SensorEventListener, StepListener {
    private Context context;

    private StepDetector simpleStepDetector;
    private android.hardware.SensorManager mSensorManager;
    private Sensor accelSensor;
    private Gson gson;
    private UserModel.DataHealth dataHealth;

    private int STEP;
    private int RUN;
    private long SLEEP;

    private String dateCurrent;
    private float chieuCao;
    private float canNang;
    private int soTuoi;

    private int x = 0;
    private long stepOne;
    private long stepTow;

    private ListenerEventSensor listenerEventSensor;

    public SensorManager(Context context, ListenerEventSensor listenerEventSensor) {
        this.context = context;
        this.listenerEventSensor = listenerEventSensor;
        init();
    }

    private void init() {
        gson = new Gson();
        getDateCurrent();
        getData();
        tongHopKetQua();

        mSensorManager = (android.hardware.SensorManager) context.getSystemService(SENSOR_SERVICE);
        accelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);
        mSensorManager.registerListener(SensorManager.this, accelSensor, android.hardware.SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void getDateCurrent(){
        dateCurrent = Utils.getDateCurrent();
    }

    public void unregisterListener(){
        mSensorManager.unregisterListener(this);
    }

    private void getData() {
        dateCurrent = Utils.getDateCurrent();

        String strDataProfile = SharedPreferences.getDataString(context, "MY_DATA_HEALTH");
        UserModel userModel = gson.fromJson(strDataProfile, UserModel.class);
        this.soTuoi = Integer.parseInt(userModel.getAge());
        this.canNang = Float.parseFloat(userModel.getWeight());
        this.chieuCao = Float.parseFloat(userModel.getHeight());

        String strData = SharedPreferences.getDataString(context, dateCurrent);
        if (!strData.equals("")){
            dataHealth = gson.fromJson(strData, UserModel.DataHealth.class);
            if (dataHealth != null){
                STEP = dataHealth.getStep();
                SLEEP = dataHealth.getSleep();
                RUN = dataHealth.getBike();
            }else {
                STEP = 0;
                SLEEP = 0;
                RUN = 0;
            }
        }else {
            STEP = 0;
            SLEEP = 0;
            RUN = 0;
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

        if (x == 0){
            x = 1;
            stepOne = System.currentTimeMillis();
        }else {
            x = 0;
            stepTow = System.currentTimeMillis();
            STEP++;
            if ((stepTow - stepOne) < 500){
                System.out.println("=======================>>> CHẠY");
                RUN++;
                SharedPreferences.setDataString(context, dateCurrent, gson.toJson(new UserModel.DataHealth(STEP, RUN, SLEEP)));

            }else {
                System.out.println("=======================>>> ĐI BỘ");
                SharedPreferences.setDataString(context, dateCurrent, gson.toJson(new UserModel.DataHealth(STEP, RUN, SLEEP)));
            }

            tongHopKetQua();
        }
    }

    private void tongHopKetQua(){
        double caloDiBo = (STEP - RUN) * Utils.getCalo(chieuCao, canNang, soTuoi, false);
        double caloChay = RUN * Utils.getCalo(chieuCao, canNang, soTuoi, true);

        System.out.println("====================>>> dibo: " + caloDiBo);
        System.out.println("====================>>> chay: " + caloChay);

        double kmDiBo = (STEP - RUN) * 0.00075;
        double kmChay = RUN * 0.00085;

        System.out.println("==================>>>>RUN = " + RUN);
        System.out.println("==================>>>>STEP = " + STEP);

        listenerEventSensor.eventSensor(STEP, RUN, 1200, (caloDiBo + caloChay) ,(kmDiBo + kmChay));

    }
}
