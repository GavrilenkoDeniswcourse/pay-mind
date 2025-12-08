package com.example.paymind;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);


        Fragment homeFragment = new HomeFragment();
        Fragment servicesFragment = new ServicesFragment();
        Fragment subscriptionsFragment = new SubscriptionsFragment();
        Fragment settingsFragment = new SettingsFragment();


        setCurrentFragment(homeFragment);


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

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


    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}