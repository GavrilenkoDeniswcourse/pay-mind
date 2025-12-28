package com.example.paymind.seeders;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.paymind.enums.CategoryType;
import com.example.paymind.enums.CurrencyType;
import com.example.paymind.enums.PeriodType;
import com.example.paymind.enums.StatusType;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SubscriptionSeeder {
    private final SQLiteDatabase db;

    public SubscriptionSeeder(SQLiteDatabase db) {
        this.db = db;
    }

    public void run() {
        long currentTime = System.currentTimeMillis() / 1000;
        Calendar calendar = Calendar.getInstance();

        calendar.set(2025, Calendar.DECEMBER, 20, 0, 0, 0);
        long today = calendar.getTimeInMillis() / 1000;

        Map<String, Integer> types = new HashMap<>();

        Cursor cursor = db.rawQuery("SELECT id, name FROM subscription_types", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                types.put(name, id);
            } while (cursor.moveToNext());
        }

        cursor.close();

        int cloudId = types.get("Облако");
        int musicId = types.get("Музыка");
        int gamesId = types.get("Игры");
        int moviesId = types.get("Фильмы");
        int videoId = types.get("Видео");

        Object[][] subscriptions = {
                // name, category_id, cost, currency_id, start_date, renewal_date,
                // period_type_id, period_value, auto_renew, notes, status_id, reminder_days_before, type_id

                {"Netflix", CategoryType.SUBSCRIPTION.getId(), 599.0, CurrencyType.RUB.getId(),
                        today - (30 * 24 * 60 * 60), today + (30 * 24 * 60 * 60),
                        PeriodType.MONTH.getId(), 1, 1, "Премиум 4K",
                        StatusType.WAIT.getId(), 3, moviesId},

                {"Spotify Premium", CategoryType.SUBSCRIPTION.getId(), 199.0, CurrencyType.RUB.getId(),
                        today - (15 * 24 * 60 * 60), today + (15 * 24 * 60 * 60),
                        PeriodType.MONTH.getId(), 1, 1, "Семейный план",
                        StatusType.WAIT.getId(), 5, musicId},

                {"YouTube Premium", CategoryType.SUBSCRIPTION.getId(), 299.0, CurrencyType.RUB.getId(),
                        today - (7 * 24 * 60 * 60), today + (23 * 24 * 60 * 60),
                        PeriodType.MONTH.getId(), 1, 1, "Индивидуальная",
                        StatusType.WAIT.getId(), 7, videoId},

                {"PlayStation Plus", CategoryType.SUBSCRIPTION.getId(), 3999.0, CurrencyType.RUB.getId(),
                        today - (90 * 24 * 60 * 60), today + (275 * 24 * 60 * 60),
                        PeriodType.YEAR.getId(), 1, 1, "Extra уровень",
                        StatusType.WAIT.getId(), 15, gamesId},

                {"Apple Music", CategoryType.SUBSCRIPTION.getId(), 169.0, CurrencyType.RUB.getId(),
                        today - (45 * 24 * 60 * 60), today + (15 * 24 * 60 * 60),
                        PeriodType.MONTH.getId(), 1, 1, "Студенческая",
                        StatusType.WAIT.getId(), 3, musicId},

//                {"Яндекс Плюс", CategoryType.SUBSCRIPTION.getId(), 299.0, CurrencyType.RUB.getId(),
//                        today - (10 * 24 * 60 * 60), today + (20 * 24 * 60 * 60),
//                        PeriodType.MONTH.getId(), 1, 1, "С музыкой и кинопоиском",
//                        StatusType.WAIT.getId(), 2, 1},
        };

        db.beginTransaction();
        try {
            for (Object[] sub : subscriptions) {
                ContentValues values = new ContentValues();

                values.put("name", (String) sub[0]);
                values.put("category_id", (Integer) sub[1]);
                values.put("cost", (Double) sub[2]);
                values.put("currency_id", (Integer) sub[3]);
                values.put("start_date", (Long) sub[4]);
                values.put("renewal_date", (Long) sub[5]);
                values.put("period_type_id", (Integer) sub[6]);
                values.put("period_value", (Integer) sub[7]);
                values.put("auto_renew", (Integer) sub[8]);
                values.put("notes", (String) sub[9]);
                values.put("status_id", (Integer) sub[10]);
                values.put("reminder_days_before", (Integer) sub[11]);
                values.put("type_id", (Integer) sub[12]);

                values.put("created_at", currentTime);
                values.put("updated_at", currentTime);

                db.insert("subscriptions", null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}