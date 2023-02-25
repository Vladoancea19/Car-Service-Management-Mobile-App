package com.example.checkit;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class ClientProfileFragment extends Fragment {

    private AlertDialog dialog;
    private TextView fullName, email, phoneNumber;
    private ImageView qrCodeImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_client_profile, container, false);

        //Elements to variables
        fullName = view.findViewById(R.id.full_name);
        Button scanQrCode = view.findViewById(R.id.scan_qr_code);
        email = view.findViewById(R.id.email);
        phoneNumber = view.findViewById(R.id.phone_number);

        //Show user data
        showUserData();

        //Managing buttons actions
        scanQrCode.setOnClickListener(v -> showQrCode());

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

    public void showQrCode() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final View popupView = getLayoutInflater().inflate(R.layout.qr_code_popup, null);

        //Elements to variables
        Button closePopup = popupView.findViewById(R.id.close_popup);
        qrCodeImage = popupView.findViewById(R.id.qr_code_image);

        generateQR();

        //Create + show popup window
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        //Close popup window
        closePopup.setOnClickListener(v -> dialog.dismiss());
    }

    private void generateQR() {
        String personalIDString = phoneNumber.getText().toString();

        MultiFormatWriter writer = new MultiFormatWriter();

        try {
            BitMatrix matrix = writer.encode(personalIDString, BarcodeFormat.QR_CODE, 800, 800);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);

            qrCodeImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}