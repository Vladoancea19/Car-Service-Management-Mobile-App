package com.example.checkit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    //Variables for Login page elements
    ImageView logoImage;
    ToggleButton clientToggleButton, mechanicToggleButton;
    TextInputLayout phoneNumber, password;
    Button signUpButton, loginButton;


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
        password = findViewById(R.id.password);
        signUpButton = findViewById(R.id.sign_up_button);
        loginButton = findViewById(R.id.login_button);

        //Managing buttons actions
        clientToggleButton.setOnClickListener(v -> {
            clientToggleButton.setChecked(true);
            clientToggleButton.setTypeface(Typeface.DEFAULT_BOLD);
            mechanicToggleButton.setChecked(false);
            mechanicToggleButton.setTypeface(Typeface.DEFAULT);
        });

        mechanicToggleButton.setOnClickListener(v -> {
            mechanicToggleButton.setChecked(true);
            mechanicToggleButton.setTypeface(Typeface.DEFAULT_BOLD);
            clientToggleButton.setChecked(false);
            clientToggleButton.setTypeface(Typeface.DEFAULT);
        });

        loginButton.setOnClickListener(this::loginUser);

        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, ClientSignUp.class);

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
        String phoneNumberInput = phoneNumber.getEditText().getText().toString();

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
        String passwordInput = password.getEditText().getText().toString();

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
        String phoneNumberInput = phoneNumber.getEditText().getText().toString().trim();
        String passwordInput = password.getEditText().getText().toString().trim();

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
}