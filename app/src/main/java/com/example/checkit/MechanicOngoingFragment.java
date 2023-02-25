package com.example.checkit;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MechanicOngoingFragment extends Fragment {

    private AlertDialog dialog1, dialog2;
    private Button scanQR;
    private TextInputEditText phoneNumber, fullName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mechanic_ongoing, container, false);

        //Elements to variables
        Button addNew = view.findViewById(R.id.add_new);

        //Managing buttons actions
        addNew.setOnClickListener(v -> clientInfo());

        return view;
    }

    private void clientInfo() {
        AlertDialog.Builder dialogBuilder1 = new AlertDialog.Builder(getActivity());
        final View popupView = getLayoutInflater().inflate(R.layout.add_new_reparation_client_info, null);

        //Elements to variables
        Button closePopup1 = popupView.findViewById(R.id.close_popup);
        scanQR = popupView.findViewById(R.id.or_scan_qr_code);
        Button next1 = popupView.findViewById(R.id.next_button);
        phoneNumber = popupView.findViewById(R.id.phone_number_text);
        fullName = popupView.findViewById(R.id.full_name_text);

        //Managing buttons actions
        scanQR.setOnClickListener(v -> scanQR());
        next1.setOnClickListener(v -> {
            //Colectare date
            dialog1.dismiss();
            carInfo();
        });

        //Create + show popup window
        dialogBuilder1.setView(popupView);
        dialog1 = dialogBuilder1.create();
        dialog1.show();

        //Close popup window
        closePopup1.setOnClickListener(v -> dialog1.dismiss());
    }

    private void carInfo() {
        AlertDialog.Builder dialogBuilder2 = new AlertDialog.Builder(getActivity());
        //final View popupView
        View popupView = getLayoutInflater().inflate(R.layout.add_new_reparation_car_info, null);

        //Elements to variables
        Button closePopup2 = popupView.findViewById(R.id.close_popup);
        scanQR = popupView.findViewById(R.id.or_scan_qr_code);
        Button next2 = popupView.findViewById(R.id.next_button);

        //Managing buttons actions
        scanQR.setOnClickListener(v -> scanQR());
        next2.setOnClickListener(v -> carInfo());

        //Create + show popup window
        dialogBuilder2.setView(popupView);
        dialog2 = dialogBuilder2.create();
        dialog2.show();

        //Close popup window
        closePopup2.setOnClickListener(v -> dialog2.dismiss());
    }

    private void scanQR() {
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String resultValue = result.getContents();

            DatabaseReference clientReference = FirebaseDatabase.getInstance("https://checkit-cd40f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("client_users");
            Query checkClientUser = clientReference.orderByChild("phoneNumber").equalTo(resultValue);

            checkClientUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {

                        String firstNameStored = snapshot.child(resultValue).child("firstName").getValue(String.class);
                        String lastNameStored = snapshot.child(resultValue).child("lastName").getValue(String.class);

                        String fullNameStored = firstNameStored + " " + lastNameStored;

                        fullName.setText(fullNameStored);
                        phoneNumber.setText(result.getContents());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    });
}