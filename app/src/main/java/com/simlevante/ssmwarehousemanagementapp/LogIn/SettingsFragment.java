package com.simlevante.ssmwarehousemanagementapp.LogIn;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import com.simlevante.ssmwarehousemanagementapp.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences userPreferences;

    public SettingsFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.fragment_settings, null);

        actualizar();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if(key.equals(getResources().getString(R.string.setting_ip))) {
            Preference pref = findPreference(key);
            pref.setSummary(sharedPreferences.getString(key, "Introduzca dirección IP"));
        }
        if(key.equals(getResources().getString(R.string.setting_puerto))) {
            Preference pref = findPreference(key);
            pref.setSummary(sharedPreferences.getString(key, "Introduzca puerto"));
        }
        if(key.equals(getResources().getString(R.string.setting_auten))) {
            Preference pref = findPreference(key);
            pref.setSummary(sharedPreferences.getString(key, "Introduzca clave de autenticación"));
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause()
    {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    public void actualizar()
    {
        userPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        Preference prefAux;
        String keyAux;

        keyAux = getResources().getString(R.string.setting_ip);
        prefAux = findPreference(keyAux);
        prefAux.setSummary(userPreferences.getString(keyAux,"Introduzca dirección IP"));

        keyAux = getResources().getString(R.string.setting_puerto);
        prefAux = findPreference(keyAux);
        prefAux.setSummary(userPreferences.getString(keyAux,"Introduzca puerto"));

        keyAux = getResources().getString(R.string.setting_auten);
        prefAux = findPreference(keyAux);
        prefAux.setSummary(userPreferences.getString(keyAux,"Introduzca clave de autenticación"));
    }
}