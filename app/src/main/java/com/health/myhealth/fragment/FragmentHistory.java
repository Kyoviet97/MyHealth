package com.health.myhealth.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.health.myhealth.R;
import com.health.myhealth.SharedPreferences;
import com.health.myhealth.adapter.AdapterRecyclerHistory;
import com.health.myhealth.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class FragmentHistory extends Fragment {
    private View rootView;
    private RecyclerView recyclerHistoryHealth;
    private AdapterRecyclerHistory adapterRecyclerHistory;
    private List<UserModel.DateHealth> dateHealthList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateHealthList = new ArrayList<>();
        UserModel userModel = new Gson().fromJson(SharedPreferences.getDataString(getActivity(), "MY_DATA_HEALTH"), UserModel.class);
        if (userModel != null){
            System.out.println("===================>>>> " + new Gson().toJson(userModel.getListDateHealth()));
            dateHealthList = userModel.getListDateHealth();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fargment_history, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setRecyclerView();
    }

    private void init() {
        recyclerHistoryHealth = rootView.findViewById(R.id.recycler_history_health);
    }

    private void setRecyclerView() {
        adapterRecyclerHistory = new AdapterRecyclerHistory(dateHealthList);
        recyclerHistoryHealth.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerHistoryHealth.setAdapter(adapterRecyclerHistory);
    }
}
