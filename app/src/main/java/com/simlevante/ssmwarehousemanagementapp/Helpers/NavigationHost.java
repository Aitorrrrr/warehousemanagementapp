package com.simlevante.ssmwarehousemanagementapp.Helpers;

import androidx.fragment.app.Fragment;

public interface NavigationHost
{
    void navigateTo(Fragment fragment, boolean addToBackstack);
    void popBackStack();
}