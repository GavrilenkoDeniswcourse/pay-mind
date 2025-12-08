package com.example.paymind;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Загружаем layout для страницы услуг
        View view = inflater.inflate(R.layout.activity_settings, container, false);

        // Здесь можно добавить код для инициализации элементов на странице
        // Например: TextView textView = view.findViewById(R.id.some_text_view);

        return view;
    }
}