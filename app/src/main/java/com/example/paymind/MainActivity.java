package com.example.paymind;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_LAST_FRAGMENT = "last_fragment";
    private int currentMenuItemId = R.id.home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentMenuItemId = prefs.getInt(KEY_LAST_FRAGMENT, R.id.home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(currentMenuItemId);

        Fragment homeFragment = new HomeFragment();
        Fragment servicesFragment = new ServicesFragment();
        Fragment subscriptionsFragment = new SubscriptionsFragment();
        Fragment settingsFragment = new SettingsFragment();

        showFragmentForMenuItem(currentMenuItemId,
                homeFragment, servicesFragment, subscriptionsFragment, settingsFragment);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            currentMenuItemId = item.getItemId();

            saveLastFragment(currentMenuItemId);

            if (item.getItemId() == R.id.home) {
                setCurrentFragment(homeFragment);
            }
            else if (item.getItemId() == R.id.services) {
                setCurrentFragment(servicesFragment);
            }
            else if (item.getItemId() == R.id.subscriptions) {
                setCurrentFragment(subscriptionsFragment);
            }
            else if (item.getItemId() == R.id.settings) {
                setCurrentFragment(settingsFragment);
            }
            return true;
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveLastFragment(currentMenuItemId);
    }

    private void saveLastFragment(int menuItemId) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .putInt(KEY_LAST_FRAGMENT, menuItemId)
                .apply();
    }

    private void showFragmentForMenuItem(int menuItemId,
                                         Fragment homeFragment, Fragment servicesFragment,
                                         Fragment subscriptionsFragment, Fragment settingsFragment) {
        if (menuItemId == R.id.home) {
            setCurrentFragment(homeFragment);
        }
        else if (menuItemId == R.id.services) {
            setCurrentFragment(servicesFragment);
        }
        else if (menuItemId == R.id.subscriptions) {
            setCurrentFragment(subscriptionsFragment);
        }
        else if (menuItemId == R.id.settings) {
            setCurrentFragment(settingsFragment);
        }
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}