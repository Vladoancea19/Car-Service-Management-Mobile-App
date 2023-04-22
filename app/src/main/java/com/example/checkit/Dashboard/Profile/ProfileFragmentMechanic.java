package com.example.checkit.Dashboard.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.checkit.R;

public class ProfileFragmentMechanic extends Fragment {

    private TextView fullName, emailText, phoneNumberText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_fragment_mechanic, container, false);

        //Elements to variables
        fullName = view.findViewById(R.id.full_name);
        emailText = view.findViewById(R.id.email_text);
        phoneNumberText = view.findViewById(R.id.phone_number_text);

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