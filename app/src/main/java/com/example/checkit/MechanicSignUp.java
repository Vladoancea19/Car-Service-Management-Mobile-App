package com.example.checkit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MechanicSignUp extends AppCompatActivity {

    //Variables for SignUp page elements
    ImageView logoImage;
    TextView logoText, sloganText;
    TextInputLayout firstName, lastName, email, phoneNumber, password;
    Button signUpButton, loginButton, mechanicButton, clientButton;

    //Firebase variables
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mechanic_sign_up);

        //Elements to variables
        logoImage = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.logo_text);
        sloganText = findViewById(R.id.slogan_text);
        clientButton = findViewById(R.id.client_button);
        mechanicButton = findViewById(R.id.mechanic_button);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phone_number);
        password = findViewById(R.id.password);
        signUpButton = findViewById(R.id.sign_up_button);
        loginButton = findViewById(R.id.login_button);

        //Managing buttons actions
        clientButton.setOnClickListener(v -> {
            Intent intent = new Intent(MechanicSignUp.this, ClientSignUp.class);

            Pair[] pairs = new Pair[12];
            pairs[0] = new Pair<View, String>(logoImage, "logo_image");
            pairs[1] = new Pair<View, String>(logoText, "logo_text");
            pairs[2] = new Pair<View, String>(sloganText, "slogan_text");
            pairs[3] = new Pair<View, String>(firstName, "first_name");
            pairs[4] = new Pair<View, String>(lastName, "last_name");
            pairs[5] = new Pair<View, String>(email, "email");
            pairs[6] = new Pair<View, String>(phoneNumber, "phone_number");
            pairs[7] = new Pair<View, String>(password, "password");
            pairs[8] = new Pair<View, String>(clientButton, "client_button");
            pairs[9] = new Pair<View, String>(mechanicButton, "mechanic_button");
            pairs[10] = new Pair<View, String>(signUpButton, "login_signup");
            pairs[11] = new Pair<View, String>(loginButton, "other_option");


            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MechanicSignUp.this, pairs);
            startActivity(intent, options.toBundle());
        });

        signUpButton.setOnClickListener(this::registerUser);

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MechanicSignUp.this, Login.class);

            Pair[] pairs = new Pair[5];
            pairs[0] = new Pair<View, String>(logoImage, "logo_image");
            pairs[1] = new Pair<View, String>(phoneNumber, "phone_number");
            pairs[2] = new Pair<View, String>(password, "password");
            pairs[3] = new Pair<View, String>(loginButton, "login_signup");
            pairs[4] = new Pair<View, String>(signUpButton, "other_option");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MechanicSignUp.this, pairs);
            startActivity(intent, options.toBundle());
        });
    }

    private boolean validateFirstName() {
        String firstNameInput = firstName.getEditText().getText().toString();

        if(firstNameInput.isEmpty()) {
            firstName.setError("This field cannot be empty!");
            return false;
        }
        else {
            firstName.setError(null);
            firstName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateLastName() {
        String lastNameInput = lastName.getEditText().getText().toString();

        if(lastNameInput.isEmpty()) {
            lastName.setError("This field cannot be empty!");
            return false;
        }
        else {
            lastName.setError(null);
            lastName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        String emailInput = email.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(emailInput.isEmpty()) {
            email.setError("This field cannot be empty!");
            return false;
        }
        else if (!emailInput.matches(emailPattern)) {
            email.setError("Invalid email address");
            return false;
        }
        else {
            email.setError(null);
            return true;
        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumberInput = phoneNumber.getEditText().getText().toString();

        if(phoneNumberInput.isEmpty()) {
            phoneNumber.setError("This field cannot be empty!");
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
        String passwordPattern = "^" +
                "(?=.*[a-zA-Z])" +
                "(?=.*[0-9])" +
                "(?=.*[!@#$%^&*()_=+])" +
                "(?=\\S+$)" +
                ".{6,}" +
                "$";

        if(passwordInput.isEmpty()) {
            password.setError("This field cannot be empty!");
            return false;
        }
        else if(!passwordInput.matches(passwordPattern)) {
            password.setError("Password must be minimum six characters long (at least one number, letter and special character included)");
            return false;
        }
        else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    public void registerUser(View view) {
        if(!validateFirstName() | !validateLastName() | !validateEmail() | !validatePhoneNumber() | !validatePassword()) {
            return;
        }

        //Setting database reference
        database = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = database.getReference("mechanic_users");

        //Get the data from the input fields
        String firstNameInput = firstName.getEditText().getText().toString();
        String lastNameInput = lastName.getEditText().getText().toString();
        String emailInput = email.getEditText().getText().toString();
        String phoneNumberInput = phoneNumber.getEditText().getText().toString();
        String passwordInput = password.getEditText().getText().toString();


        //Save input into database
        MechanicUserHelper mechanicUserHelper = new MechanicUserHelper(firstNameInput, lastNameInput, emailInput, phoneNumberInput, passwordInput);

        reference.child(phoneNumberInput).setValue(mechanicUserHelper);

        //Transition to Login page
        Intent intent = new Intent(MechanicSignUp.this, Login.class);

        Pair[] pairs = new Pair[5];
        pairs[0] = new Pair<View, String>(logoImage, "logo_image");
        pairs[1] = new Pair<View, String>(phoneNumber, "phone_number");
        pairs[2] = new Pair<View, String>(password, "password");
        pairs[3] = new Pair<View, String>(loginButton, "login_signup");
        pairs[4] = new Pair<View, String>(signUpButton, "other_option");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MechanicSignUp.this, pairs);
        startActivity(intent, options.toBundle());

        //Pop-up after succesfully creating account
        Toast.makeText(this, "Your account has been successfully created!", Toast.LENGTH_LONG).show();
    }
}