package com.example.paymind.models;

public class Settings {
    private int id;
    private boolean darkTheme;
    private boolean pushNotificationsEnabled;

    public Settings() {}

    public Settings(int id, boolean darkTheme, boolean pushNotificationsEnabled) {
        this.id = id;
        this.darkTheme = darkTheme;
        this.pushNotificationsEnabled = pushNotificationsEnabled;
    }

    // Геттеры
    public int getId() { return id; }
    public boolean isDarkTheme() { return darkTheme; }
    public boolean isPushNotificationsEnabled() { return pushNotificationsEnabled; }

    // Сеттеры
    public void setId(int id) { this.id = id; }
    public void setDarkTheme(boolean darkTheme) { this.darkTheme = darkTheme; }
    public void setPushNotificationsEnabled(boolean pushNotificationsEnabled) {
        this.pushNotificationsEnabled = pushNotificationsEnabled;
    }
}
