package com.example.paymind;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        MyPushNotification notification =
                new MyPushNotification(context, manager);

        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");

        notification.sendNotify(title, text);
    }
}
