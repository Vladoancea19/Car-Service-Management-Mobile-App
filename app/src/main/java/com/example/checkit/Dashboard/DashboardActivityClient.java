package com.example.checkit.Dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.checkit.Dashboard.Cars.CarsFragmentClient;
import com.example.checkit.Dashboard.Home.HomeFragmentClient;
import com.example.checkit.Dashboard.Profile.ProfileFragmentClient;
import com.example.checkit.R;
import com.google.android.material.textfield.TextInputEditText;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class DashboardActivityClient extends AppCompatActivity {

    ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity_client);

        chipNavigationBar = findViewById(R.id.bottom_navigation_menu);
        chipNavigationBar.setItemSelected(R.id.bottom_navigation_menu_home, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragmentClient()).commit();
        bottomNavigationMenu();
    }

    @SuppressLint("NonConstantResourceId")
    private void bottomNavigationMenu() {
        chipNavigationBar.setOnItemSelectedListener(i -> {
            Fragment fragment = null;
            switch (i) {
                case R.id.bottom_navigation_menu_home:
                    fragment = new HomeFragmentClient();
                    break;
                case R.id.bottom_navigation_menu_cars:
                    fragment = new CarsFragmentClient();
                    break;
                case R.id.bottom_navigation_menu_profile:
                    fragment = new ProfileFragmentClient();
                    break;
            }
            assert fragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        });
    }
}