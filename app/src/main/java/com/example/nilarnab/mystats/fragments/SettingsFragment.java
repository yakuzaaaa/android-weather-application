package com.example.nilarnab.mystats.fragments;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nilarnab.mystats.R;
import com.example.nilarnab.mystats.utility.Utility;

/**
 * Created by nilarnab on 7/8/16.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        addPreferencesFromResource(R.xml.pref_general);
        bindPreferencWithListener(findPreference(getResources().getString(R.string.pref_pin_code)));
        bindPreferencWithListener(findPreference(getResources().getString(R.string.pref_unit_key)));

        return rootView;
    }

    private void bindPreferencWithListener(Preference preference) {
        preference.setOnPreferenceChangeListener(this);

        onPreferenceChange(preference, Utility.getStringPreference(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            String val = (String) newValue;
            int index = listPreference.findIndexOfValue(val);
            if (index >= 0) {
                preference.setSummary(listPreference.getEntries()[index]);
            }
        } else {
            preference.setSummary((String) newValue);
        }

        return true;
    }
}
