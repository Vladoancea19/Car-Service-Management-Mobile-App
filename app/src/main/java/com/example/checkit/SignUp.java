package com.example.checkit;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class SignUp extends AppCompatActivity {

    TextView sloganText;
    ToggleButton clientToggleButton, mechanicToggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        sloganText = findViewById(R.id.slogan_text);
        clientToggleButton = findViewById(R.id.clientToggleButton);
        mechanicToggleButton = findViewById(R.id.mechanicToggleButton);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ClientSignUpFragment()).commit();

        toggleMenu();
    }

    private void toggleMenu() {
        clientToggleButton.setOnClickListener(v -> {
            clientToggleButton.setChecked(true);
            clientToggleButton.setTypeface(sloganText.getTypeface(), Typeface.BOLD);
            mechanicToggleButton.setChecked(false);
            mechanicToggleButton.setTypeface(sloganText.getTypeface(), Typeface.NORMAL);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ClientSignUpFragment()).commit();
        });

        mechanicToggleButton.setOnClickListener(v -> {
            mechanicToggleButton.setChecked(true);
            mechanicToggleButton.setTypeface(sloganText.getTypeface(), Typeface.BOLD);
            clientToggleButton.setChecked(false);
            clientToggleButton.setTypeface(sloganText.getTypeface(), Typeface.NORMAL);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MechanicSignUpFragment()).commit();
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if(v instanceof TextInputEditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if(!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}