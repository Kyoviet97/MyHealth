package com.health.myhealth.activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.health.myhealth.R;
import com.health.myhealth.adapter.ViewPagerAdapter;
import com.health.myhealth.dialog.DialogSettingPush;
import com.health.myhealth.dialog.DialogSettingSleep;
import com.health.myhealth.fragment.FragmentHealth;
import com.health.myhealth.fragment.FragmentHistory;
import com.health.myhealth.service.ScreenReceiver;
import com.health.myhealth.service.ServiceCountStep;
import com.health.myhealth.utils.AlarmManager;
import com.health.myhealth.utils.SharedPreferences;

public class HealthActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private BroadcastReceiver mReceiver = null;
    private boolean isOffScreen = true;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        init();
        checkScreenOff();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkServiceRun();
        AlarmManager.stopAlarm();
        isOffScreen = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Nếu app dừng lại và màn hình bị tắt sẽ khởi động chạy ngầm
        if (isOffScreen && ScreenReceiver.wasScreenOn){
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
        //Kiểm tra nếu app đang chạy ngầm thì khi mở app sẽ tắt chế độ chạy ngầm
        //(Nếu không kiểm tra app có chạy ngầm hay không mà tắt chế độ chạy ngầm luôn sẽ bị lỗi app)
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
        //Khởi tạo quản lý fragment và truyền vào 2 fragment cùng tiêu đề ( Có thể thay đổi tiêu đề nếu muốn)
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
        //Kiểm tra nếu chưa khởi động dịch vụ chạy ẩn và đang giữ trạng thái login thì sẽ bắt đầu chạy ẩn (Nếu logout sẽ hết phiên làm việc và không cộng các dữ liệu như: bước, calo, time ngủ...)
        if (!isMyServiceRunning(ServiceCountStep.class) && SharedPreferences.getDataInt(this, "CHECK_LOGIN") == 1){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, ServiceCountStep.class));
                return;
            }
            startService(new Intent(this, ServiceCountStep.class));
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            isOffScreen = false;
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Click back thêm 1 lần để đóng app", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                SharedPreferences.setDataInt(this, "CHECK_LOGIN", 0);
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;

            case R.id.menu_ngu:
                DialogSettingSleep dialogNgu = new DialogSettingSleep(this);
                dialogNgu.show();
                return true;

            case R.id.menu_nhac_nho:
                DialogSettingPush dialogNhacNho = new DialogSettingPush(this);
                dialogNhacNho.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}