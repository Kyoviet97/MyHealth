package com.health.myhealth.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.health.myhealth.dialog.DialogSetTime;
import com.health.myhealth.dialog.DialogTestTool;
import com.health.myhealth.model.UserModel;
import com.health.myhealth.utils.Conts;
import com.health.myhealth.utils.ListenerEventSensor;
import com.health.myhealth.R;
import com.health.myhealth.utils.NotificationHelper;
import com.health.myhealth.utils.SensorManager;
import com.health.myhealth.utils.SharedPreferences;
import com.health.myhealth.utils.Utils;

public class FragmentHealth extends Fragment implements ListenerEventSensor {
    private View rootView;
    private TextView txtSensor;
    private SensorManager sensorManager;

    private Gson gson;
    private boolean isChecking = false;

    private TextView txtStep;
    private TextView txtRun;
    private TextView txtSleep;
    private TextView txtCalo;
    private TextView txtLong;

    private Button stopTest;

    private UserModel.DataHealth dataHealth;
    private String dateCurrent;
    private int soTuoi;
    private Float canNang;
    private Float chieuCao;

    private int STEP;
    private Long SLEEP;
    private int RUN;

    private Handler handlerTest;
    private Boolean isStopHandler;
    private Boolean isStopHandlerSleep;
    private Boolean isSleepTest;
    private int timeShow = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_health, container, false);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }


    private void init() {
        txtSensor = rootView.findViewById(R.id.txt_sensor);
        txtStep = rootView.findViewById(R.id.txt_step);
        txtRun = rootView.findViewById(R.id.txt_run);
        txtSleep = rootView.findViewById(R.id.txt_sleep);
        txtCalo = rootView.findViewById(R.id.txt_calo);
        txtLong = rootView.findViewById(R.id.txt_long);

        stopTest = rootView.findViewById(R.id.btn_stop_test);
        stopTest.setVisibility(View.INVISIBLE);

        isStopHandler = false;
        handlerTest = new Handler();
        gson = new Gson();
        eventStopTest();
        sensorManager = new SensorManager(getActivity(), this);
        getDataBMI();
    }

    private void eventStopTest() {
        stopTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStopHandler = true;
                isStopHandlerSleep = true;
                isSleepTest = false;
                stopTest.setVisibility(View.INVISIBLE);
                isChecking = false;
            }
        });
    }

    private void startTest(final int idTest) {
        isStopHandler = false;
        int timeTest = 0;
        switch (idTest) {
            case 0:
                timeTest = 800;
                break;

            case 1:
                timeTest = 600;
                break;

            case 2:
                timeTest = 500;
                break;

            case 3:
                timeTest = 1000;
                break;
        }


        final int finalTimeTest = timeTest;
        handlerTest.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isStopHandler) {
                    handlerTest.removeCallbacks(this);
                } else {
                    updateDataToLocal(idTest);
                    handlerTest.postDelayed(this, finalTimeTest);
                }
            }
        }, timeTest);

    }

    private void updateDataToLocal(final int idTest) {
        getData(new DataHealth() {
            @Override
            public void dataHealth(final int STEP, final Long SLEEP, final int RUN) {
                int newStep = STEP;
                Long newSleep = SLEEP;
                int newRun = RUN;
                if (idTest == 0) {
                    newStep = newStep + 1;
                }

                if (idTest == 1) {
                    newRun = newRun + 1;
                    newStep = newStep + 1;
                }

                if (idTest == 2) {
                    newRun = newRun + 1;
                    newStep = newStep + 1;
                }

                if (idTest == 3) {
                    newSleep = newSleep + 1;
                }

                double caloDiBo = (newStep - newRun) * Utils.getCalo(chieuCao, canNang, soTuoi, false);
                double caloChay = newRun * Utils.getCalo(chieuCao, canNang, soTuoi, true);
                double kmDiBo = (newStep - newRun) * 0.00075;
                double kmChay = newRun * 0.00085;

                double calo = (caloDiBo + caloChay);
                double quangDuong = (kmChay + kmDiBo);

                txtSensor.setText(String.valueOf(newStep));
                txtStep.setText(String.valueOf((newStep - newRun)));
                txtRun.setText(String.valueOf(newRun));
                txtSleep.setText(Utils.showTimeSleepMinute(newSleep));
                txtCalo.setText(String.valueOf(Math.round(calo * 100.0) / 100.0));
                txtLong.setText(String.valueOf(Math.round(quangDuong * 100.0) / 100.0));

                SharedPreferences.setDataString(getContext(), dateCurrent, gson.toJson(new UserModel.DataHealth(newStep, newRun, newSleep)));
            }
        });

    }

    private void getDataBMI() {
        dateCurrent = Utils.getDateCurrent();
        String strDataProfile = SharedPreferences.getDataString(getActivity(), "MY_DATA_HEALTH");
        UserModel userModel = gson.fromJson(strDataProfile, UserModel.class);
        this.soTuoi = Integer.parseInt(userModel.getAge());
        this.canNang = Float.parseFloat(userModel.getWeight());
        this.chieuCao = Float.parseFloat(userModel.getHeight());
    }

    private void getData(DataHealth dataReturn) {
        dateCurrent = Utils.getDateCurrent();
        String strData = SharedPreferences.getDataString(getActivity(), dateCurrent);
        if (!strData.equals("")) {
            dataHealth = gson.fromJson(strData, UserModel.DataHealth.class);
            if (dataHealth != null) {
                STEP = dataHealth.getStep();
                SLEEP = dataHealth.getSleep();
                RUN = dataHealth.getBike();
                dataReturn.dataHealth(STEP, SLEEP, RUN);
            }
        } else {
            UserModel.DataHealth newData = new UserModel.DataHealth(0, 0, 0);
            SharedPreferences.setDataString(getActivity(), dateCurrent, new Gson().toJson(newData));
            getData(dataReturn);
        }
    }

    private void showNotifi() {
        isStopHandler = false;
        timeShow = 0;
        handlerTest.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isStopHandler) {
                    handlerTest.removeCallbacks(this);
                } else {
                    timeShow = timeShow + 1;
                    stopTest.setText(String.valueOf((15 - timeShow)));
                    if (timeShow >= 15) {
                        isStopHandler = true;
                        stopTest.setVisibility(View.INVISIBLE);
                        isChecking = false;

                        String titleVanDong = SharedPreferences.getDataString(getActivity(), "TITLE_VD");
                        String contentVanDong = SharedPreferences.getDataString(getActivity(), "CONTENT_VD");
                        NotificationHelper noti = new NotificationHelper(getActivity());
                        noti.createNotification(titleVanDong, contentVanDong);
                    }
                    handlerTest.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }


    private void showNotifiSleep(final int time, final int timeStart) {
        isStopHandlerSleep = false;
        timeShow = 0;
        handlerTest.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isStopHandlerSleep) {
                    handlerTest.removeCallbacks(this);
                } else {
                    timeShow = timeShow + 1;
                    stopTest.setText(String.valueOf((time - timeShow)));
                    if (timeShow >= time) {
                        isStopHandlerSleep = true;
                        String title = "Đến giờ ngủ";
                        String content = "Hãy đi ngủ đúng giờ";
                        NotificationHelper noti = new NotificationHelper(getActivity());
                        noti.createNotification(title, content);
                        stopTest.setText("Stop");
                        Toast.makeText(getContext(), "Bắt đầu đếm thời gian ngủ sau: " + timeStart + "s", Toast.LENGTH_LONG).show();
                        startSleepTest(timeStart * 1000);
                    }
                    handlerTest.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    private void startSleepTest(int time){
        isSleepTest = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isSleepTest){
                    startTest(3);
                }
            }
        }, time);
    }


    private void showDialogTimeSleep() {
        DialogSetTime dialogSetTime = new DialogSetTime(getActivity(), new DialogSetTime.OnClickItemDialog() {
            @Override
            public void onClickItem(int time, int timeStart) {
                stopTest.setVisibility(View.VISIBLE);
                showNotifiSleep(time, timeStart);
            }
        });
        dialogSetTime.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.setPauseCountStep(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.setPauseCountStep(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sensorManager.unregisterListener();
    }

    @Override
    public void eventSensor(int step, int run, long sleep, double calo, double quangDuong) {
        //Lắng nghe các dữ liệu mà main quản lý chuyển động trả về
        txtSensor.setText(String.valueOf(step));
        txtStep.setText(String.valueOf((step - run)));
        txtRun.setText(String.valueOf(run));
        txtSleep.setText(Utils.showTimeSleepMinute(sleep));
        txtCalo.setText(String.valueOf(Math.round(calo * 100.0) / 100.0));
        txtLong.setText(String.valueOf(Math.round(quangDuong * 100.0) / 100.0));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_test:
                if (isChecking) {
                    Toast.makeText(getActivity(), "Vui lòng dừng chức năng test hiện tại", Toast.LENGTH_SHORT).show();
                } else {
                    DialogTestTool dialogTestTool = new DialogTestTool(getActivity(), new DialogTestTool.OnClickItemDialog() {
                        @Override
                        public void onClickItem(int idItem) {
                            isChecking = true;
                            if (idItem == 4) {
                                showNotifi();
                                stopTest.setVisibility(View.VISIBLE);
                            } else if (idItem == 5) {
                                showDialogTimeSleep();
                            } else {
                                stopTest.setText("Stop");
                                startTest(idItem);
                                stopTest.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    dialogTestTool.show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    interface DataHealth {
        void dataHealth(int STEP, Long SLEEP, int RUN);
    }

}

