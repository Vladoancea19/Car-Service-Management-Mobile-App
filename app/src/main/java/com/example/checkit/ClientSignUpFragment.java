package com.example.checkit;

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

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ClientSignUpFragment extends Fragment {

    //Variables for SignUp page elements
    ImageView logoImage;
    TextView logoText, sloganText;
    TextInputLayout firstName, lastName, email, phoneNumber, password;
    Button signUpButton, loginButton;

    //Firebase variables
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_client_sign_up, container, false);

        //Elements to variables
        logoImage = view.findViewById(R.id.logo_image);
        logoText = view.findViewById(R.id.logo_text);
        sloganText = view.findViewById(R.id.slogan_text);
        firstName = view.findViewById(R.id.first_name);
        lastName = view.findViewById(R.id.last_name);
        email = view.findViewById(R.id.email);
        phoneNumber = view.findViewById(R.id.phone_number);
        password = view.findViewById(R.id.password);
        signUpButton = view.findViewById(R.id.sign_up_button);
        loginButton = view.findViewById(R.id.login_button);

        signUpButton.setOnClickListener(this::registerUser);

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Login.class);

            startActivity(intent);
            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        return view;
    }

    private boolean validateFirstName() {
        String firstNameInput = Objects.requireNonNull(firstName.getEditText()).getText().toString();

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
        String lastNameInput = Objects.requireNonNull(lastName.getEditText()).getText().toString();

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
        String emailInput = Objects.requireNonNull(email.getEditText()).getText().toString();
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
        String phoneNumberInput = Objects.requireNonNull(phoneNumber.getEditText()).getText().toString();

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
        String passwordInput = Objects.requireNonNull(password.getEditText()).getText().toString();
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
        reference = database.getReference("client_users");

        //Get the data from the input fields
        String firstNameInput = Objects.requireNonNull(firstName.getEditText()).getText().toString();
        String lastNameInput = Objects.requireNonNull(lastName.getEditText()).getText().toString();
        String emailInput = Objects.requireNonNull(email.getEditText()).getText().toString();
        String phoneNumberInput = Objects.requireNonNull(phoneNumber.getEditText()).getText().toString();
        String passwordInput = Objects.requireNonNull(password.getEditText()).getText().toString();

        //Save input into database
        ClientUserHelper clientUserHelper = new ClientUserHelper(firstNameInput, lastNameInput, emailInput, phoneNumberInput, passwordInput);

        reference.child(phoneNumberInput).setValue(clientUserHelper);

        //Transition to Login page
        Intent intent = new Intent(getActivity(), Login.class);

        startActivity(intent);
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        //Pop-up after successfully creating account
        Toast.makeText(getActivity(), "Your account has been successfully created!", Toast.LENGTH_LONG).show();
    }
}