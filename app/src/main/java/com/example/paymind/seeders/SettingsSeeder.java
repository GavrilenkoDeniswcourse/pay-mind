package com.example.paymind.seeders;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class SettingsSeeder {
    private final SQLiteDatabase db;

    public SettingsSeeder(SQLiteDatabase db) {
        this.db = db;
    }

    public void run() {
        ContentValues values = new ContentValues();
        values.put("dark_theme", 0);
        values.put("push_notifications_enabled", 0);

        db.beginTransaction();
        try {
            db.insert("settings", null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}