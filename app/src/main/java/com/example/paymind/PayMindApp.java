package com.example.paymind;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.paymind.models.Settings;
import com.example.paymind.repositories.SettingsRepository;

public class PayMindApp extends Application {
    private DBHelper dbHelper;
    private SettingsRepository settingsRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper = new DBHelper(this);
        settingsRepository = new SettingsRepository(dbHelper);

        Settings settings = settingsRepository.getSettings();
        if (settings != null) {
            if (settings.isDarkTheme()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}
