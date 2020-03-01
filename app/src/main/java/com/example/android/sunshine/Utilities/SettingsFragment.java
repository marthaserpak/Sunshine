package com.example.android.sunshine.Utilities;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.example.android.sunshine.R;


public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public void setPreferenceSummary(Preference preferences,
                                     Object value) {
        String stringValue = value.toString();
        String key = findPreference(preferences.toString()).getKey();

        if (preferences instanceof ListPreference) {

            /* the preference's 'entries' list
            (since they have separate labels/values). */
            ListPreference listPreference = (ListPreference) preferences;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                listPreference.setSummary(listPreference.getEntries()
                        [prefIndex]);
            }
        } else {
            // For other preferences, set the summary
            // to the value's simple string representation.
            preferences.setSummary(stringValue);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState,
                                    String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences sharedPreferences = getPreferenceScreen()
                .getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();
        for(int i = 0; i < count; i++) {
            Preference p = preferenceScreen.getPreference(i);
            if (!(p instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(p.getKey(),
                        "");
                setPreferenceSummary(p,value);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {

        Preference preference = findPreference(key);
        if( null != preference) {
            if(!(preference instanceof CheckBoxPreference)) {
                setPreferenceSummary(preference,
                        sharedPreferences.getString(key, ""));
            }
        }
    }



}
