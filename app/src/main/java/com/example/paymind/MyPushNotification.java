package com.example.paymind;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class MyPushNotification {
    private static String CHANNEL_ID="Base channel";
    private static String CHANNEL_DESCRIPTION="it's a base channel";
    private static final int NOTIFY_ID=123;
    private NotificationManager notificationManager;
    private Context context;

    public MyPushNotification(Context context, NotificationManager manager){
        notificationManager=manager;
        this.context=context;
        createNotificationChannel();
    }

    public void sendNotify(String title,String text){
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(android.R.drawable.ic_dialog_info);
        notificationManager.notify(NOTIFY_ID,builder.build());
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            int importance= NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,CHANNEL_ID,importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public static boolean checkNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        123);
                return false;
            }
        }
        return true;
    }
}
