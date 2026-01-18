package com.example.paymind;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.paymind.models.Settings;
import com.example.paymind.repositories.SettingsRepository;

public class SettingsFragment extends Fragment {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchNotifications;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchDarkTheme;
    private DBHelper dbHelper;
    private SettingsRepository settingsRepository;
    private Settings currentSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);

        dbHelper = new DBHelper(getContext());
        settingsRepository = new SettingsRepository(dbHelper);

        switchNotifications = view.findViewById(R.id.switch1);
        switchDarkTheme = view.findViewById(R.id.switch2);

        currentSettings = settingsRepository.getSettings();


        if (currentSettings != null) {
            switchNotifications.setChecked(currentSettings.isPushNotificationsEnabled());
            switchDarkTheme.setChecked(currentSettings.isDarkTheme());
        }

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (currentSettings != null) {
                boolean success = settingsRepository.updatePushNotifications(isChecked, currentSettings);
                if (success) {
                    currentSettings.setPushNotificationsEnabled(isChecked);

                    if (isChecked) {
                        MyPushNotification.checkNotificationPermission(getActivity());
//                            NotificationManager notificationManager =
//                                    (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
//
//                            MyPushNotification myPushNotification = new MyPushNotification(getContext(),
//                                    notificationManager);
//                            myPushNotification.sendNotify("Тестовое уведомление", "Тест");
                    } else {
                    }
                }
            }
        });

        switchDarkTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (currentSettings != null) {
                boolean success = settingsRepository.updateDarkTheme(isChecked, currentSettings);
                if (success) {
                    currentSettings.setDarkTheme(isChecked);

                    if (isChecked) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                }
            }
        });

        return view;
    }

    public void onResume() {
        super.onResume();

        if (currentSettings == null) return;

        boolean enabled = notificationsEnabled(requireContext());

        if (!enabled && currentSettings.isPushNotificationsEnabled()) {
            settingsRepository.updatePushNotifications(false, currentSettings);
            currentSettings.setPushNotificationsEnabled(false);
            switchNotifications.setChecked(false);
        }
    }

    private boolean notificationsEnabled(Context context) {
        boolean permissionGranted = true;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissionGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED;
        }

        boolean systemEnabled =
                NotificationManagerCompat.from(context).areNotificationsEnabled();

        return permissionGranted && systemEnabled;
    }
}