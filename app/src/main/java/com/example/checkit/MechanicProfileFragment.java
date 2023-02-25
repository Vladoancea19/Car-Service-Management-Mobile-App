package com.example.checkit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class MechanicProfileFragment extends Fragment {

    private TextView fullName, email, phoneNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mechanic_profile, container, false);

        //Elements to variables
        fullName = view.findViewById(R.id.full_name);
        email = view.findViewById(R.id.email);
        phoneNumber = view.findViewById(R.id.phone_number);

        //Show user data
        showUserData();

        return view;
    }

    private void showUserData() {
        Intent intent = requireActivity().getIntent();

        String userFullName = intent.getStringExtra("full_name");
        String userEmail = intent.getStringExtra("email");
        String userPhoneNumber = intent.getStringExtra("phone_number");

        fullName.setText(userFullName);
        email.setText(userEmail);
        phoneNumber.setText(userPhoneNumber);
    }
}