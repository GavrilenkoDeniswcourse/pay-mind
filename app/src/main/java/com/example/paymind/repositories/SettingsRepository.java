package com.example.paymind.repositories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.paymind.DBHelper;
import com.example.paymind.models.Settings;
import com.example.paymind.models.SubscriptionType;

import java.util.ArrayList;
import java.util.List;

public class SettingsRepository {
    private final DBHelper dbHelper;

    public SettingsRepository(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        initSettings();
    }

    private void initSettings() {
        Settings settings = getSettings();
        if (settings == null) {
            createDefaultSettings();
        }
    }

    private void createDefaultSettings() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("dark_theme", 0); // false
        values.put("push_notifications_enabled", 1); // true

        long result = db.insert("settings", null, values);
        db.close();

        if (result == -1) {
            Log.e("SettingsRepository", "Ошибка при создании настроек по умолчанию");
        }
    }

    public Settings getSettings() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Settings settings = null;

        Cursor cursor = db.rawQuery("SELECT * FROM settings LIMIT 1", null);

        if (cursor.moveToFirst()) {
            settings = new Settings();
            settings.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            settings.setDarkTheme(cursor.getInt(cursor.getColumnIndexOrThrow("dark_theme")) == 1);
            settings.setPushNotificationsEnabled(
                    cursor.getInt(cursor.getColumnIndexOrThrow("push_notifications_enabled")) == 1
            );
        }

        cursor.close();
        db.close();
        return settings;
    }

    public boolean updateDarkTheme(boolean enabled, Settings settings) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("dark_theme", enabled ? 1 : 0);

        int rowsUpdated = db.update("settings", values, "id = " + settings.getId(), null);
        db.close();

        return rowsUpdated > 0;
    }

    public boolean updatePushNotifications(boolean enabled, Settings settings) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("push_notifications_enabled", enabled ? 1 : 0);

        int rowsUpdated = db.update("settings", values, "id = " + settings.getId(), null);
        db.close();

        return rowsUpdated > 0;
    }

    public boolean updateSettings(Settings settings) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("dark_theme", settings.isDarkTheme() ? 1 : 0);
        values.put("push_notifications_enabled", settings.isPushNotificationsEnabled() ? 1 : 0);

        int rowsUpdated = db.update("settings", values, "id = ?",
                new String[]{String.valueOf(settings.getId())});
        db.close();

        return rowsUpdated > 0;
    }
}
