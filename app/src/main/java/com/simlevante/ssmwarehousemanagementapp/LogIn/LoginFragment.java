package com.simlevante.ssmwarehousemanagementapp.LogIn;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.simlevante.ssmwarehousemanagementapp.Helpers.NavigationHost;
import com.simlevante.ssmwarehousemanagementapp.R;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private AppCompatImageButton settings;
    private SettingsFragment settingsFragment;

    public LoginFragment()
    {

    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        settings = v.findViewById(R.id.login_settings);
        settings.setOnClickListener(this);

        // Acceder preferencias
        //SharedPreferences userPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        //String aux1 = userPreferences.getString(getResources().getString(R.string.setting_ip),"Introduzca dirección IP");

        return v;
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.login_settings)
        {
            abrirPreferencias();
        }
    }

    private void abrirPreferencias()
    {
        settingsFragment = new SettingsFragment();
        ((NavigationHost) getActivity()).navigateTo(settingsFragment, true);
    }
}
