package com.example.checkit.SignUp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.checkit.Login.LoginActivity;
import com.example.checkit.Models.MechanicUserModel;
import com.example.checkit.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpFragmentMechanic extends Fragment {

    ImageView logoImage;
    TextView logoText, sloganText;
    TextInputLayout firstNameContainer, lastNameContainer, emailContainer, phoneNumberContainer, passwordContainer;
    Button signUpButton, loginButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sign_up_fragment_mechanic, container, false);

        //Elements to variables
        logoImage = view.findViewById(R.id.logo_image);
        logoText = view.findViewById(R.id.logo_text);
        sloganText = view.findViewById(R.id.slogan_text);
        firstNameContainer = view.findViewById(R.id.first_name_container);
        lastNameContainer = view.findViewById(R.id.last_name_container);
        emailContainer = view.findViewById(R.id.email_container);
        phoneNumberContainer = view.findViewById(R.id.phone_number_container);
        passwordContainer = view.findViewById(R.id.password_container);
        signUpButton = view.findViewById(R.id.sign_up_button);
        loginButton = view.findViewById(R.id.to_login_button);

        signUpButton.setOnClickListener(this::registerUser);

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);

            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        return view;
    }

    private boolean validateFirstName() {
        String firstNameInput = Objects.requireNonNull(firstNameContainer.getEditText()).getText().toString();

        if(firstNameInput.isEmpty()) {
            firstNameContainer.setError("This field cannot be empty!");
            return false;
        }
        else {
            firstNameContainer.setError(null);
            firstNameContainer.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateLastName() {
        String lastNameInput = Objects.requireNonNull(lastNameContainer.getEditText()).getText().toString();

        if(lastNameInput.isEmpty()) {
            lastNameContainer.setError("This field cannot be empty!");
            return false;
        }
        else {
            lastNameContainer.setError(null);
            lastNameContainer.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        String emailInput = Objects.requireNonNull(emailContainer.getEditText()).getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(emailInput.isEmpty()) {
            emailContainer.setError("This field cannot be empty!");
            return false;
        }
        else if (!emailInput.matches(emailPattern)) {
            emailContainer.setError("Invalid email address");
            return false;
        }
        else {
            emailContainer.setError(null);
            return true;
        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumberInput = Objects.requireNonNull(phoneNumberContainer.getEditText()).getText().toString();

        if(phoneNumberInput.isEmpty()) {
            phoneNumberContainer.setError("This field cannot be empty!");
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
        String passwordPattern = "^" +
                "(?=.*[a-zA-Z])" +
                "(?=.*[0-9])" +
                "(?=.*[!@#$%^&*()_=+])" +
                "(?=\\S+$)" +
                ".{6,}" +
                "$";

        if(passwordInput.isEmpty()) {
            passwordContainer.setError("This field cannot be empty!");
            return false;
        }
        else if(!passwordInput.matches(passwordPattern)) {
            passwordContainer.setError("Password must be minimum six characters long (at least one number, letter and special character included)");
            return false;
        }
        else {
            passwordContainer.setError(null);
            passwordContainer.setErrorEnabled(false);
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
        String firstNameInput = Objects.requireNonNull(firstNameContainer.getEditText()).getText().toString();
        String lastNameInput = Objects.requireNonNull(lastNameContainer.getEditText()).getText().toString();
        String emailInput = Objects.requireNonNull(emailContainer.getEditText()).getText().toString();
        String phoneNumberInput = Objects.requireNonNull(phoneNumberContainer.getEditText()).getText().toString();
        String passwordInput = Objects.requireNonNull(passwordContainer.getEditText()).getText().toString();


        //Save input into database
        MechanicUserModel mechanicUserModel = new MechanicUserModel(firstNameInput, lastNameInput, emailInput, phoneNumberInput, passwordInput);

        reference.child(phoneNumberInput).setValue(mechanicUserModel);

        //Transition to LoginActivity page
        Intent intent = new Intent(getActivity(), LoginActivity.class);

        startActivity(intent);
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        //Pop-up after successfully creating account
        Toast.makeText(getActivity(), "Your account has been successfully created!", Toast.LENGTH_LONG).show();
    }
}