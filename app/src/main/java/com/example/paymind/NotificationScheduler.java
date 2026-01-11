package com.example.paymind;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationScheduler {
    public static void scheduleReminder(
            Context context,
            long reminderTimeMillis,
            String title,
            String text,
            int subscriptionId
    ) {

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("text", text);
        intent.putExtra("subscription_id", subscriptionId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                subscriptionId, // уникальный ID!
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTimeMillis,
                    pendingIntent
            );
        }
    }
}
