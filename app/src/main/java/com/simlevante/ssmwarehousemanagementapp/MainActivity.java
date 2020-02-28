package com.simlevante.ssmwarehousemanagementapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.simlevante.ssmwarehousemanagementapp.Helpers.NavigationHost;
import com.simlevante.ssmwarehousemanagementapp.Helpers.ToastHost;
import com.simlevante.ssmwarehousemanagementapp.Interfaces.LogIn.LoginFragment;

public class MainActivity extends AppCompatActivity implements NavigationHost, ToastHost, LoginFragment.GuardarUsuario {

    private String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.containerBase, new LoginFragment())
                    .commit();
        }
    }

    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack)
    {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerBase, fragment);

        if (addToBackstack) {
            transaction.addToBackStack(fragment.getClass().getName());
        }

        transaction.commit();
    }

    @Override
    public void toast(String texto)
    {
        Toast toast = Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void guardarUsuario(String usuario)
    {
        this.usuario = usuario;
    }

    public String getUsuarioGlobal()
    {
        return this.usuario;
    }
}