package com.example.checkit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MechanicOngoingFragment extends Fragment {

    private AlertDialog.Builder dialogBuilder1, dialogBuilder2;
    private AlertDialog dialog1, dialog2;
    private Button addNew, closePopup1, closePopup2, scanQR, next1, next2;
    private TextInputEditText phoneNumber;
    private View popupView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mechanic_ongoing, container, false);

        //Elements to variables
        addNew = view.findViewById(R.id.add_new);

        //Managing buttons actions
        addNew.setOnClickListener(v -> clientInfo());

        return view;
    }

    private void clientInfo() {
        dialogBuilder1 = new AlertDialog.Builder(getActivity());
        final View popupView = getLayoutInflater().inflate(R.layout.add_new_reparation_client_info, null);

        //Elements to variables
        closePopup1 = popupView.findViewById(R.id.close_popup);
        scanQR = popupView.findViewById(R.id.or_scan_qr_code);
        next1 = popupView.findViewById(R.id.next_button);

        //Managing buttons actions
        scanQR.setOnClickListener(v -> scanQR());
        next1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Colectare date
                dialog1.dismiss();
                carInfo();
            }
        });

        //Create + show popup window
        dialogBuilder1.setView(popupView);
        dialog1 = dialogBuilder1.create();
        dialog1.show();

        //Close popup window
        closePopup1.setOnClickListener(v -> dialog1.dismiss());
    }

    private void carInfo() {
        dialogBuilder2 = new AlertDialog.Builder(getActivity());
        //final View popupView
        popupView = getLayoutInflater().inflate(R.layout.add_new_reparation_car_info, null);

        //Elements to variables
        closePopup2 = popupView.findViewById(R.id.close_popup);
        scanQR = popupView.findViewById(R.id.or_scan_qr_code);
        next2 = popupView.findViewById(R.id.next_button);

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
//        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
       if(result.getContents() != null) {
           //????????????
           phoneNumber = popupView.findViewById(R.id.phone_number_text);
           phoneNumber.setText(result.toString());

            AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
            build.setTitle("Result");
            build.setMessage(result.getContents());
            build.setPositiveButton("OK", (dialog, which) -> dialog.dismiss()).show();
       }
    });
}