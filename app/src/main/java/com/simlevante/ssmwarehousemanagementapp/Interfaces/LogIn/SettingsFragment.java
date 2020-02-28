package com.simlevante.ssmwarehousemanagementapp.Interfaces.LogIn;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import com.simlevante.ssmwarehousemanagementapp.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences userPreferences;

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

        // Darle max lenght a un EditTextPreference.
        EditTextPreference contadorAlb = findPreference(getResources().getString(R.string.setting_serie_compra));
        contadorAlb.setOnBindEditTextListener((@NonNull EditText editText) ->
        {
            int maxLength = 3;
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        });

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
        if(key.equals(getResources().getString(R.string.setting_serie_compra))) {
            Preference pref = findPreference(key);
            pref.setSummary(sharedPreferences.getString(key, "Introduzca código del contador para albarán de compra"));
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

        keyAux = getResources().getString(R.string.setting_serie_compra);
        prefAux = findPreference(keyAux);
        prefAux.setSummary(userPreferences.getString(keyAux,"Introduzca código contador albarán de compra"));
    }
}