package com.health.myhealth.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.health.myhealth.utils.ListenerEventSensor;
import com.health.myhealth.R;
import com.health.myhealth.utils.SensorManager;
import com.health.myhealth.utils.Utils;

public class FragmentHealth extends Fragment implements ListenerEventSensor {
    private View rootView;
    private TextView txtSensor;
    private TextView reviewHealthToday;
    private SensorManager sensorManager;

    private TextView txtStep;
    private TextView txtRun;
    private TextView txtSleep;
    private TextView txtCalo;
    private TextView txtLong;

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
    }


    private void init() {
        txtSensor = rootView.findViewById(R.id.txt_sensor);

        txtStep = rootView.findViewById(R.id.txt_step);
        txtRun = rootView.findViewById(R.id.txt_run);
        txtSleep = rootView.findViewById(R.id.txt_sleep);
        txtCalo = rootView.findViewById(R.id.txt_calo);
        txtLong = rootView.findViewById(R.id.txt_long);

        reviewHealthToday = rootView.findViewById(R.id.txt_review_health_today);
        sensorManager = new SensorManager(getActivity(), this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener();
    }


    @Override
    public void eventSensor(int step, int run, int sleep, double calo, double quangDuong) {
        txtSensor.setText(String.valueOf(step));

        txtStep.setText(String.valueOf((step - run)));
        txtRun.setText( String.valueOf(run));
        txtSleep.setText(Utils.showTimeSleep2(sleep));
        txtCalo.setText(String.valueOf(Math.round(calo * 100.0) / 100.0));
        txtLong.setText(String.valueOf(Math.round(quangDuong * 100.0) / 100.0));
    }
}
