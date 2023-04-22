package com.example.checkit.SignUp;

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

import com.example.checkit.R;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity {

    TextView sloganText;
    ToggleButton clientToggleButton, mechanicToggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.sign_up_activity);

        sloganText = findViewById(R.id.slogan_text);
        clientToggleButton = findViewById(R.id.client_toggle_button);
        mechanicToggleButton = findViewById(R.id.mechanic_toggle_button);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SignUpFragmentClient()).commit();

        toggleMenu();
    }

    private void toggleMenu() {
        clientToggleButton.setOnClickListener(v -> {
            clientToggleButton.setChecked(true);
            clientToggleButton.setTypeface(sloganText.getTypeface(), Typeface.BOLD);
            mechanicToggleButton.setChecked(false);
            mechanicToggleButton.setTypeface(sloganText.getTypeface(), Typeface.NORMAL);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SignUpFragmentClient()).commit();
        });

        mechanicToggleButton.setOnClickListener(v -> {
            mechanicToggleButton.setChecked(true);
            mechanicToggleButton.setTypeface(sloganText.getTypeface(), Typeface.BOLD);
            clientToggleButton.setChecked(false);
            clientToggleButton.setTypeface(sloganText.getTypeface(), Typeface.NORMAL);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SignUpFragmentMechanic()).commit();
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if(view instanceof TextInputEditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if(!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    view.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}