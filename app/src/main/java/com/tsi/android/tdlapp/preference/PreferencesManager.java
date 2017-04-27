package com.tsi.android.tdlapp.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.tsi.android.tdlapp.R;

public class PreferencesManager {

    private SharedPreferences preferences;
    private static PreferencesManager preferencesManagerInstance;

    private PreferencesManager() {}

    public static PreferencesManager getInstance(Context context) {
        if (preferencesManagerInstance == null) {
            preferencesManagerInstance = new PreferencesManager();
            preferencesManagerInstance.preferences = context.getSharedPreferences(context.getString(R.string.prefs), Context.MODE_PRIVATE);
        }
        return preferencesManagerInstance;
    }

    public int getColorPreference(String key) {
        return preferences.getInt(key, Color.BLACK);
    }

    public void saveColorPreference(String key, int value) {
        preferences.edit().clear().putInt(key, value).commit();
    }
}
