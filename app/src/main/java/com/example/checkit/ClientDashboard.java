package com.example.checkit;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class ClientDashboard extends AppCompatActivity {

    ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_dashboard);

        chipNavigationBar = findViewById(R.id.bottom_nav_menu);
        chipNavigationBar.setItemSelected(R.id.bottom_nav_ongoing, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ClientOngoingFragment()).commit();
        bottomMenu();
    }

    @SuppressLint("NonConstantResourceId")
    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener(i -> {
            Fragment fragment = null;
            switch (i) {
                case R.id.bottom_nav_ongoing:
                    fragment = new ClientOngoingFragment();
                    break;
                case R.id.bottom_nav_history:
                    fragment = new ClientHistoryFragment();
                    break;
                case R.id.bottom_nav_profile:
                    fragment = new ClientProfileFragment();
                    break;
            }
            assert fragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        });
    }
}