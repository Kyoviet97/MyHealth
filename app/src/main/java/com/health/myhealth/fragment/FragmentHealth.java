package com.health.myhealth.fragment;

import android.os.Bundle;
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
import com.health.myhealth.dialog.DialogTestTool;
import com.health.myhealth.model.UserModel;
import com.health.myhealth.utils.Conts;
import com.health.myhealth.utils.ListenerEventSensor;
import com.health.myhealth.R;
import com.health.myhealth.utils.SensorManager;
import com.health.myhealth.utils.SharedPreferences;
import com.health.myhealth.utils.Utils;

public class FragmentHealth extends Fragment implements ListenerEventSensor {
    private View rootView;
    private TextView txtSensor;
    private SensorManager sensorManager;

    private Gson gson;
    private int idTest = 100;
    private boolean isChecking = false;

    private TextView txtStep;
    private TextView txtRun;
    private TextView txtSleep;
    private TextView txtCalo;
    private TextView txtLong;

    private Button stopTest;

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

        gson = new Gson();
        eventStopTest();
        sensorManager = new SensorManager(getActivity(), this);
    }

    private void eventStopTest(){
        stopTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "StopTest: " + idTest, Toast.LENGTH_SHORT).show();
                stopTest.setVisibility(View.INVISIBLE);
                isChecking = false;
            }
        });
    }

    private void eventTest(int idTest){

    }

    private void getCurrentData(){

    }

    private void updateDataToLocal(){
        String dateCurrent = Utils.getDateCurrent();
        System.out.println("================>>>> " + dateCurrent);
//        SharedPreferences.setDataString(getActivity(), dateCurrent, gson.toJson(new UserModel.DataHealth(STEP, RUN, SLEEP)));
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
            txtRun.setText( String.valueOf(run));
            txtSleep.setText(Utils.showTimeSleepMinute(sleep));
            txtCalo.setText(String.valueOf(Math.round(calo * 100.0) / 100.0));
            txtLong.setText(String.valueOf(Math.round(quangDuong * 100.0) / 100.0));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_test:
                if (isChecking){
                    Toast.makeText(getActivity(), "Vui lòng dừng chức năng test hiện tại", Toast.LENGTH_SHORT).show();
                }else {
                    DialogTestTool dialogTestTool = new DialogTestTool(getActivity(), new DialogTestTool.OnClickItemDialog() {
                        @Override
                        public void onClickItem(int idItem) {
                            idTest = idItem;
                            isChecking = true;
                            updateDataToLocal();
                            stopTest.setVisibility(View.VISIBLE);
                        }
                    });
                    dialogTestTool.show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
