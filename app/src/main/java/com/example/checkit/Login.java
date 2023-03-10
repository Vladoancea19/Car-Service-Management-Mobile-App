package com.example.checkit;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private static final String FILE_PHONE_NUMBER = "rememberMe";
    //Variables for Login page elements
    ImageView logoImage;
    ToggleButton clientToggleButton, mechanicToggleButton;
    TextInputLayout phoneNumber, password;
    TextInputEditText phoneNumberText, passwordText;
    CheckBox rememberMe;
    Button signUpButton, loginButton;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        //Elements to variables
        logoImage = findViewById(R.id.logo_image);
        clientToggleButton = findViewById(R.id.clientToggleButton);
        mechanicToggleButton = findViewById(R.id.mechanicToggleButton);
        phoneNumber = findViewById(R.id.phone_number);
        phoneNumberText = findViewById(R.id.phone_number_text);
        password = findViewById(R.id.password);
        passwordText = findViewById(R.id.password_text);
        rememberMe = findViewById(R.id.remember_me);
        signUpButton = findViewById(R.id.sign_up_button);
        loginButton = findViewById(R.id.login_button);
        loginButton.performClick();

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
            Intent intent = new Intent(Login.this, SignUp.class);

            Pair[] pairs = new Pair[5];
            pairs[0] = new Pair<View, String>(logoImage, "logo_image");
            pairs[1] = new Pair<View, String>(phoneNumber, "phone_number");
            pairs[2] = new Pair<View, String>(password, "password");
            pairs[3] = new Pair<View, String>(loginButton, "login_signup");
            pairs[4] = new Pair<View, String>(signUpButton, "other_option");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
            startActivity(intent, options.toBundle());
        });
    }

    private boolean validatePhoneNumber() {
        String phoneNumberInput = Objects.requireNonNull(phoneNumber.getEditText()).getText().toString();

        if(phoneNumberInput.isEmpty()) {
            phoneNumber.setError("Please enter your phone number");
            return false;
        }
        else {
            phoneNumber.setError(null);
            phoneNumber.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = Objects.requireNonNull(password.getEditText()).getText().toString();

        if(passwordInput.isEmpty()) {
            password.setError("Please enter your password");
            return false;
        }
        else {
            password.setError(null);
            password.setErrorEnabled(false);
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
        String phoneNumberInput = Objects.requireNonNull(phoneNumber.getEditText()).getText().toString().trim();
        String passwordInput = Objects.requireNonNull(password.getEditText()).getText().toString().trim();

        if(clientToggleButton.isChecked()) {
            DatabaseReference clientReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("client_users");
            Query checkClientUser = clientReference.orderByChild("phoneNumber").equalTo(phoneNumberInput);

            checkClientUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {

                        phoneNumber.setError(null);
                        phoneNumber.setErrorEnabled(false);


                        String passwordStored = snapshot.child(phoneNumberInput).child("password").getValue(String.class);

                        assert passwordStored != null;
                        if(passwordStored.equals(passwordInput)) {
                            password.setError(null);
                            password.setErrorEnabled(false);

                            String firstNameStored = snapshot.child(phoneNumberInput).child("firstName").getValue(String.class);
                            String lastNameStored = snapshot.child(phoneNumberInput).child("lastName").getValue(String.class);
                            String emailStored = snapshot.child(phoneNumberInput).child("email").getValue(String.class);
                            String phoneNumberStored = snapshot.child(phoneNumberInput).child("phoneNumber").getValue(String.class);

                            Intent intent = new Intent(Login.this, ClientDashboard.class);

                            String fullNameStored = firstNameStored + " " + lastNameStored;
                            intent.putExtra("full_name", fullNameStored);
                            intent.putExtra("email", emailStored);
                            intent.putExtra("phone_number", phoneNumberStored);

                            startActivity(intent);
                            finish();
                        }
                        else {
                            phoneNumber.setError("Wrong credentials");
                            password.setError("Wrong credentials");
                        }

                    }
                    else {
                        phoneNumber.setError("Wrong credentials");
                        password.setError("Wrong credentials");
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

                        phoneNumber.setError(null);
                        phoneNumber.setErrorEnabled(false);


                        String passwordStored = snapshot.child(phoneNumberInput).child("password").getValue(String.class);

                        assert passwordStored != null;
                        if(passwordStored.equals(passwordInput)) {
                            password.setError(null);
                            password.setErrorEnabled(false);

                            String firstNameStored = snapshot.child(phoneNumberInput).child("firstName").getValue(String.class);
                            String lastNameStored = snapshot.child(phoneNumberInput).child("lastName").getValue(String.class);
                            String emailStored = snapshot.child(phoneNumberInput).child("email").getValue(String.class);
                            String phoneNumberStored = snapshot.child(phoneNumberInput).child("phoneNumber").getValue(String.class);

                            Intent intent = new Intent(Login.this, MechanicDashboard.class);

                            String fullNameStored = firstNameStored + " " + lastNameStored;
                            intent.putExtra("full_name", fullNameStored);
                            intent.putExtra("email", emailStored);
                            intent.putExtra("phone_number", phoneNumberStored);

                            startActivity(intent);
                            finish();
                        }
                        else {
                            phoneNumber.setError("Wrong credentials");
                            password.setError("Wrong credentials");
                        }

                    }
                    else {
                        phoneNumber.setError("Wrong credentials");
                        password.setError("Wrong credentials");
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