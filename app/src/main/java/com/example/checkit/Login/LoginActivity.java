package com.example.checkit.Login;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.checkit.Dashboard.DashboardActivityClient;
import com.example.checkit.Dashboard.DashboardActivityMechanic;
import com.example.checkit.R;
import com.example.checkit.SignUp.SignUpActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String FILE_PHONE_NUMBER = "rememberMe";

    ImageView logoImage;
    ToggleButton clientToggleButton, mechanicToggleButton;
    TextInputLayout phoneNumberContainer, passwordContainer;
    CheckBox rememberMeButton;
    Button signUpButton, loginButton;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login_activity);

        //Elements to variables
        logoImage = findViewById(R.id.logo_image);
        clientToggleButton = findViewById(R.id.client_toggle_button);
        mechanicToggleButton = findViewById(R.id.mechanic_toggle_button);
        phoneNumberContainer = findViewById(R.id.phone_number_container);
        passwordContainer = findViewById(R.id.password_container);
        rememberMeButton = findViewById(R.id.remember_me_button);
        signUpButton = findViewById(R.id.to_sign_up_button);
        loginButton = findViewById(R.id.login_button);

        //Managing buttons actions
        clientToggleButton.setOnClickListener(v -> {
            clientToggleButton.setChecked(true);
            clientToggleButton.setTypeface(signUpButton.getTypeface(), Typeface.BOLD);
            mechanicToggleButton.setChecked(false);
            mechanicToggleButton.setTypeface(signUpButton.getTypeface(), Typeface.NORMAL);
        });

        mechanicToggleButton.setOnClickListener(v -> {
            mechanicToggleButton.setChecked(true);
            mechanicToggleButton.setTypeface(signUpButton.getTypeface(), Typeface.BOLD);
            clientToggleButton.setChecked(false);
            clientToggleButton.setTypeface(signUpButton.getTypeface(), Typeface.NORMAL);
        });

        loginButton.setOnClickListener(this::loginUser);

        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);

            Pair[] pairs = new Pair[5];
            pairs[0] = new Pair<View, String>(logoImage, "logo_image_transition");
            pairs[1] = new Pair<View, String>(phoneNumberContainer, "phone_number_transition");
            pairs[2] = new Pair<View, String>(passwordContainer, "password_transition");
            pairs[3] = new Pair<View, String>(loginButton, "login_signup_transition");
            pairs[4] = new Pair<View, String>(signUpButton, "other_option_transition");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
            startActivity(intent, options.toBundle());
        });
    }

    private boolean validatePhoneNumber() {
        String phoneNumberInput = Objects.requireNonNull(phoneNumberContainer.getEditText()).getText().toString();

        if(phoneNumberInput.isEmpty()) {
            phoneNumberContainer.setError("Please enter your phone number");
            return false;
        }
        else {
            phoneNumberContainer.setError(null);
            phoneNumberContainer.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = Objects.requireNonNull(passwordContainer.getEditText()).getText().toString();

        if(passwordInput.isEmpty()) {
            passwordContainer.setError("Please enter your password");
            return false;
        }
        else {
            passwordContainer.setError(null);
            passwordContainer.setErrorEnabled(false);
            return true;
        }
    }

    public void loginUser(View view) {
        if(!validatePhoneNumber() | !validatePassword()) {
            return;
        }
        else {
            isUser();
        }
    }

    private void isUser() {
        String phoneNumberInput = Objects.requireNonNull(phoneNumberContainer.getEditText()).getText().toString().trim();
        String passwordInput = Objects.requireNonNull(passwordContainer.getEditText()).getText().toString().trim();

        if(clientToggleButton.isChecked()) {
            DatabaseReference clientReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("client_users");
            Query checkClientUser = clientReference.orderByChild("phoneNumber").equalTo(phoneNumberInput);

            checkClientUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        phoneNumberContainer.setError(null);
                        phoneNumberContainer.setErrorEnabled(false);

                        String passwordStored = snapshot.child(phoneNumberInput).child("password").getValue(String.class);

                        assert passwordStored != null;
                        if(passwordStored.equals(passwordInput)) {
                            passwordContainer.setError(null);
                            passwordContainer.setErrorEnabled(false);

                            String firstNameStored = snapshot.child(phoneNumberInput).child("firstName").getValue(String.class);
                            String lastNameStored = snapshot.child(phoneNumberInput).child("lastName").getValue(String.class);
                            String fullNameStored = firstNameStored + " " + lastNameStored;
                            String emailStored = snapshot.child(phoneNumberInput).child("email").getValue(String.class);
                            String phoneNumberStored = snapshot.child(phoneNumberInput).child("phoneNumber").getValue(String.class);

                            Intent intent = new Intent(LoginActivity.this, DashboardActivityClient.class);

                            intent.putExtra("fullName", fullNameStored);
                            intent.putExtra("email", emailStored);
                            intent.putExtra("phoneNumber", phoneNumberStored);

                            startActivity(intent);
                            finish();
                        }
                        else {
                            phoneNumberContainer.setError("Wrong credentials");
                            passwordContainer.setError("Wrong credentials");
                        }

                    }
                    else {
                        phoneNumberContainer.setError("Wrong credentials");
                        passwordContainer.setError("Wrong credentials");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            DatabaseReference mechanicReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("mechanic_users");
            Query checkMechanicUser = mechanicReference.orderByChild("phoneNumber").equalTo(phoneNumberInput);

            checkMechanicUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        phoneNumberContainer.setError(null);
                        phoneNumberContainer.setErrorEnabled(false);

                        String passwordStored = snapshot.child(phoneNumberInput).child("password").getValue(String.class);

                        assert passwordStored != null;
                        if(passwordStored.equals(passwordInput)) {
                            passwordContainer.setError(null);
                            passwordContainer.setErrorEnabled(false);

                            String firstNameStored = snapshot.child(phoneNumberInput).child("firstName").getValue(String.class);
                            String lastNameStored = snapshot.child(phoneNumberInput).child("lastName").getValue(String.class);
                            String fullNameStored = firstNameStored + " " + lastNameStored;
                            String emailStored = snapshot.child(phoneNumberInput).child("email").getValue(String.class);
                            String phoneNumberStored = snapshot.child(phoneNumberInput).child("phoneNumber").getValue(String.class);

                            Intent intent = new Intent(LoginActivity.this, DashboardActivityMechanic.class);

                            intent.putExtra("fullName", fullNameStored);
                            intent.putExtra("email", emailStored);
                            intent.putExtra("phoneNumber", phoneNumberStored);

                            startActivity(intent);
                            finish();
                        }
                        else {
                            phoneNumberContainer.setError("Wrong credentials");
                            passwordContainer.setError("Wrong credentials");
                        }

                    }
                    else {
                        phoneNumberContainer.setError("Wrong credentials");
                        passwordContainer.setError("Wrong credentials");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
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