package com.health.myhealth.dialog;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;
import androidx.core.app.NotificationCompat;

import com.health.myhealth.R;
import com.health.myhealth.activity.LoginActivity;
import com.health.myhealth.utils.Conts;
import com.health.myhealth.utils.NotificationHelper;

public class DialogDemoPush extends AppCompatDialog implements View.OnClickListener {
    private TextView txtCount;
    private Button btnStart;
    private Button btnStop;
    private Handler handlerCount;
    private boolean isStopCount = false;
    public DialogDemoPush(Context context) {
        super(context, R.style.Theme_dialog_setup);
        setContentView(R.layout.dialog_push);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.88);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.5);
        getWindow().setLayout(width, height);
        setCancelable(false);
        isStopCount = false;
        Conts.timeCountPushDemo = 10;
        init();
    }

    private void init(){
        txtCount = findViewById(R.id.txt_count_time_push);
        btnStart = findViewById(R.id.btn_start_push_demo);
        btnStop = findViewById(R.id.btn_stop_push_demo);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    private void startCount(){
        final String content = "Bạn đã nghỉ ngơi trong một thời gian dài, hãy vận động";
        if (handlerCount == null){
            handlerCount = new Handler();
            handlerCount.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.out.println("======================>>>> " + Conts.timeCountPushDemo);
                    if (isStopCount){
                        handlerCount.removeCallbacks(this);
                        return;
                    }
                    if (Conts.timeCountPushDemo == 0){
                        handlerCount.removeCallbacks(this);
                        NotificationHelper notificationHelper = new NotificationHelper(getContext());
                        notificationHelper.createNotification("Tiêu đề", content);
                        if (isShowing()){
                            dismiss();
                        }

                    }else {
                        Conts.timeCountPushDemo --;
                        txtCount.setText("Thông báo sẽ hiển thị sau: " + Conts.timeCountPushDemo + " giây");
                        handlerCount.postDelayed(this, 1000);
                    }
                }
            }, 0);
        }
    }

//    private void showNotication(String title, String content) {
//        if (Build.VERSION.SDK_INT >= 26) {
//            String CHANNEL_ID = "service_my_health_notify";
//
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
//                    "notify",
//                    NotificationManager.IMPORTANCE_HIGH);
//
//            PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0,
//                    new Intent(getContext(), LoginActivity.class), 0);
//
//
//            ((NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
//
//            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
//                    .setSmallIcon(R.drawable.ic_favorite)  // the status icon
//                    .setWhen(System.currentTimeMillis())  // the time stamp
//                    .setContentTitle(title)  // the label of the entry
//                    .setContentText(content)  // the contents of the entry
//                    .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
//                    .setSound(alarmSound)
//                    .build();
//            startForeground(2, notification);
//        }
//
//    }

    private void onDismiss(){
        if (isShowing()){
            isStopCount = true;
            dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onDismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start_push_demo:
                btnStart.setEnabled(false);
                startCount();
                break;

            case R.id.btn_stop_push_demo:
                onDismiss();
                break;
        }
    }
}
