package com.example.checkit;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {

    ToggleButton clientToggleButton, mechanicToggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        clientToggleButton = findViewById(R.id.clientToggleButton);
        mechanicToggleButton = findViewById(R.id.mechanicToggleButton);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ClientSignUpFragment()).commit();

        toggleMenu();
    }

    private void toggleMenu() {
        clientToggleButton.setOnClickListener(v -> {
            clientToggleButton.setChecked(true);
            clientToggleButton.setTypeface(Typeface.DEFAULT_BOLD);
            mechanicToggleButton.setChecked(false);
            mechanicToggleButton.setTypeface(Typeface.DEFAULT);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ClientSignUpFragment()).commit();
        });

        mechanicToggleButton.setOnClickListener(v -> {
            mechanicToggleButton.setChecked(true);
            mechanicToggleButton.setTypeface(Typeface.DEFAULT_BOLD);
            clientToggleButton.setChecked(false);
            clientToggleButton.setTypeface(Typeface.DEFAULT);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MechanicSignUpFragment()).commit();
        });
    }
}