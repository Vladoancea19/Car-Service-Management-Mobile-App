package com.example.checkit.Dashboard.Profile;

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

import com.example.checkit.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class ProfileFragmentClient extends Fragment {

    private AlertDialog dialog;
    private TextView fullName, emailText, phoneNumberText;
    private ImageView qrCodeImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_fragment_client, container, false);

        //Elements to variables
        fullName = view.findViewById(R.id.full_name);
        Button scanQrCode = view.findViewById(R.id.scan_qr_code_button);
        emailText = view.findViewById(R.id.email_text);
        phoneNumberText = view.findViewById(R.id.phone_number_text);

        //Managing buttons actions
        scanQrCode.setOnClickListener(v -> showQR());

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

    public void showQR() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final View popupView = getLayoutInflater().inflate(R.layout.qr_popup, null);

        //Elements to variables
        Button closePopupButton = popupView.findViewById(R.id.close_popup_button);
        qrCodeImage = popupView.findViewById(R.id.qr_code_image);

        generateQR();

        //Create + show popup window
        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        //Close popup window
        closePopupButton.setOnClickListener(v -> dialog.dismiss());
    }

    private void generateQR() {
        String personalIDString = phoneNumberText.getText().toString();
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