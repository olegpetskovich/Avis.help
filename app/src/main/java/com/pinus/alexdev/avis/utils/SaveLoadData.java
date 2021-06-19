package com.pinus.alexdev.avis.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveLoadData {
    private SharedPreferences preferences;

    public SaveLoadData(Context context) {
        preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
    }

    public void saveInt(String key, int positionOnCoordinate) {
        // Извлеките редактор, чтобы изменить Общие настройки.

        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putInt(key, positionOnCoordinate);

        prefEditor.apply();
    }

    public int loadInt(String key) {
        return preferences.getInt(key, -1);
    }

    public void saveLong(String key, long value) {
        // Извлеките редактор, чтобы изменить Общие настройки.

        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putLong(key, value);

        prefEditor.apply();
    }

    public long loadLong(String key) {
        return preferences.getLong(key, -1);
    }

    public void saveString(String key, String value) {
        // Извлеките редактор, чтобы изменить Общие настройки.
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString(key, value);

        prefEditor.apply();
    }

    public String loadString(String key) {
        return preferences.getString(key, null);
    }

    public void saveBoolean(String key, boolean state) {
        // Извлеките редактор, чтобы изменить Общие настройки.

        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putBoolean(key, state);

        prefEditor.apply();
    }

    public boolean loadBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public boolean isApplicationFirstRun(String key, boolean value) {
        return preferences.getBoolean(key, value);
    }
}
