package com.simlevante.ssmwarehousemanagementapp.Interfaces.Menu;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.navigation.NavigationView;
import com.simlevante.ssmwarehousemanagementapp.Helpers.NavigationHost;
import com.simlevante.ssmwarehousemanagementapp.Interfaces.Entradas.EntradasFragment;
import com.simlevante.ssmwarehousemanagementapp.R;

public class MenuFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private EntradasFragment entradasFragment;

    public MenuFragment() {

    }

    public static MenuFragment newInstance() {
        MenuFragment fragment = new MenuFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        Toolbar toolbar = v.findViewById(R.id.menu_toolBar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawer = v.findViewById(R.id.menu_layout);
        toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = v.findViewById(R.id.menu_navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        boolean retorno = true;

        switch(id)
        {
            case R.id.nav_menu_entradas:
                drawer.closeDrawer(GravityCompat.START, false);
                entradasFragment = new EntradasFragment();
                ((NavigationHost) getActivity()).navigateTo(entradasFragment, false);
                break;
        }

        return retorno;
    }
}
