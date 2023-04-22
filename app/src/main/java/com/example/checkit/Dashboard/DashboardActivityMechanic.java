package com.example.checkit.Dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.checkit.Dashboard.Home.HomeFragmentMechanic;
import com.example.checkit.Dashboard.Profile.ProfileFragmentMechanic;
import com.example.checkit.R;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class DashboardActivityMechanic extends AppCompatActivity {

    ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity_client);

        chipNavigationBar = findViewById(R.id.bottom_navigation_menu);
        chipNavigationBar.setItemSelected(R.id.bottom_navigation_menu_home, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragmentMechanic()).commit();
        bottomMenu();
    }

    @SuppressLint("NonConstantResourceId")
    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener(i -> {
            Fragment fragment = null;
            switch (i) {
                case R.id.bottom_navigation_menu_home:
                    fragment = new HomeFragmentMechanic();
                    break;
                case R.id.bottom_navigation_menu_profile:
                    fragment = new ProfileFragmentMechanic();
                    break;
            }
            assert fragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        });
    }
}