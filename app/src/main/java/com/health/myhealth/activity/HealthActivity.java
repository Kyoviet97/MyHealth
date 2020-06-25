package com.health.myhealth.activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.health.myhealth.R;
import com.health.myhealth.adapter.ViewPagerAdapter;
import com.health.myhealth.fragment.FragmentHealth;
import com.health.myhealth.fragment.FragmentHistory;
import com.health.myhealth.service.ScreenReceiver;
import com.health.myhealth.service.ServiceCountStep;

public class HealthActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private BroadcastReceiver mReceiver = null;
    private boolean isOffScreen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        init();
        checkScreenOff();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkServiceRun();
        isOffScreen = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isOffScreen && ScreenReceiver.wasScreenOn){
            System.out.println("====================>>>> SCREEN TURNED OFF");
            startServiceHealth();
        }
    }

    private void checkScreenOff(){
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }

    private void checkServiceRun() {
        if (isMyServiceRunning(ServiceCountStep.class)){
            stopService(new Intent(this, ServiceCountStep.class));
        }
    }

    private void init() {
        tabLayout = findViewById(R.id.tab_layout_health);
        viewPager = findViewById(R.id.view_page);
        setupViewPager();
    }

    private void setupViewPager() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ViewPagerAdapter adapter = new ViewPagerAdapter(fragmentManager);
        adapter.addFragment(new FragmentHealth(), "Hôm nay");
        adapter.addFragment(new FragmentHistory(), "Lịch sử");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(adapter);//deprecated
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void startServiceHealth(){
        if (!isMyServiceRunning(ServiceCountStep.class)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, ServiceCountStep.class));
                return;
            }
            startService(new Intent(this, ServiceCountStep.class));
        }
    }

    @Override
    public void onBackPressed() {
        isOffScreen = false;
       finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }

        startServiceHealth();
    }
}