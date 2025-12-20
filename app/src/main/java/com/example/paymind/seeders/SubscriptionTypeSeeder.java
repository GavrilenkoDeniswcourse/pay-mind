package com.example.paymind.seeders;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class SubscriptionTypeSeeder {
    private final SQLiteDatabase db;

    public SubscriptionTypeSeeder(SQLiteDatabase db) {
        this.db = db;
    }

    public void run() {

        String[] types = {
                "Облако",
                "Музыка",
                "Игры",
                "Фильмы",
                "Видео",
        };

        db.beginTransaction();
        try {
            for (String typeName : types) {
                ContentValues values = new ContentValues();
                values.put("name", typeName);
                db.insert("subscription_types", null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}