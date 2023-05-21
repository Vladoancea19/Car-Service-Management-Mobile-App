package com.example.checkit.Dashboard.Profile;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.checkit.Dashboard.DashboardActivityMechanic;
import com.example.checkit.Login.LoginActivity;
import com.example.checkit.R;
import com.example.checkit.SplashScreen.SplashScreenActivity;
import com.google.android.material.button.MaterialButton;

public class ProfileFragmentMechanic extends Fragment {

    private TextView fullName, emailText, phoneNumberText;
    private MaterialButton logOutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_fragment_mechanic, container, false);

        //Elements to variables
        fullName = view.findViewById(R.id.full_name);
        emailText = view.findViewById(R.id.email_text);
        phoneNumberText = view.findViewById(R.id.phone_number_text);
        logOutButton = view.findViewById(R.id.log_out_button);

        //Manage buttons actions
        logOutButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("rememberMe", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            startActivity(intent);
            getActivity().finish();
        });

        //Show user data
        showUserData();

        return view;
    }

    private void showUserData() {
        Intent intent = requireActivity().getIntent();

        String userFullName = intent.getStringExtra("fullName");
        String userEmail = intent.getStringExtra("email");
        String userPhoneNumber = intent.getStringExtra("phoneNumber");

        fullName.setText(userFullName);
        emailText.setText(userEmail);
        phoneNumberText.setText(userPhoneNumber);
    }
}